package com.happy.service;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import com.happy.common.BaseData;
import com.happy.logger.LoggerManage;
import com.happy.manage.MediaManage;
import com.happy.model.MessageIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.audio.player.Player;
import com.tulskiy.musique.audio.player.PlayerEvent;
import com.tulskiy.musique.audio.player.PlayerEvent.PlayerEventCode;
import com.tulskiy.musique.audio.player.PlayerListener;
import com.tulskiy.musique.model.Track;
import com.tulskiy.musique.system.TrackIO;
import com.tulskiy.musique.util.AudioMath;

/**
 * 播放服务
 * 
 * @author Administrator
 * 
 */
public class MediaPlayerService implements Observer {

	private static MediaPlayerService _MediaPlayerService;
	private static LoggerManage logger;
	/**
	 * 当前播放歌曲
	 */
	private SongInfo songInfo;

	/**
	 * 播放器
	 */
	private Player mediaPlayer;

	/**
	 * 歌词线程
	 */
	private Thread lrcThread;
	/**
	 * 快进是否完成
	 */
	private boolean isSeekFinish = true;

	/**
	 * 当前的基本播放进度
	 */
	private long baseCurrentMillis = 0;

	public static MediaPlayerService getMediaPlayerService() {
		if (_MediaPlayerService == null) {
			_MediaPlayerService = new MediaPlayerService();
		}
		return _MediaPlayerService;
	}

	public MediaPlayerService() {
		ObserverManage.getObserver().addObserver(this);
	}

	public void init() {
		logger = LoggerManage.getZhangLogger();
		logger.info("播放器服务启动");
	}

	public void close() {
		initPlayer();
	}

	@Override
	public void update(Observable o, final Object data) {

		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {
				SongInfo msongInfo = songMessage.getSongInfo();
				if (msongInfo != null) {
					songInfo = msongInfo;
				}
				playInfoMusic(songMessage.getSongInfo());
			} else if (songMessage.getType() == SongMessage.SERVICEPAUSEMUSIC) {
				initMusic();
			} else if (songMessage.getType() == SongMessage.INITMUSIC) {
				SongInfo msongInfo = songMessage.getSongInfo();
				if (msongInfo != null) {
					songInfo = msongInfo;
				}
				initPlayer();
			} else if (songMessage.getType() == SongMessage.SERVICEPLAYINIT) {
				initPlayer();
			} else if (songMessage.getType() == SongMessage.SERVICESEEKTOMUSIC) {
				int progress = songMessage.getProgress();
				seekTo(progress);
			} else if (songMessage.getType() == SongMessage.SERVICESTOPMUSIC) {
				stopMusic();
			}
		} else if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(MessageIntent.PLAYERVOLUME)) {
				initVolume();
			}
		}

	}

	/**
	 * 停止音乐
	 */
	protected void stopMusic() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.pause();
				mediaPlayer = null;
			}
			if (lrcThread != null) {
				lrcThread = null;
			}
			if (songInfo != null) {
				SongMessage msg = new SongMessage();
				songInfo.setPlayProgress(0);
				msg.setSongInfo(songInfo);
				msg.setType(SongMessage.SERVICESTOPEDMUSIC);
				ObserverManage.getObserver().setMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化声音音量
	 */
	protected void initVolume() {
		if (mediaPlayer != null) {
			mediaPlayer.getAudioOutput().setVolume(
					(float) (BaseData.volumeSize * 1.0 / 100));
		}
	}

	/**
	 * 快进
	 * 
	 * @param progress
	 */
	private void seekTo(int progress) {
		try {
			if (mediaPlayer != null) {

				isSeekFinish = false;
				// 结束播放，重新创建一个播放器
				initPlayer();
				// 设置歌曲进度
				songInfo.setPlayProgress(progress);
				playInfoMusic(songInfo);

				MediaManage.getMediaManage().setPlayStatus(MediaManage.PLAYING);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化播放器
	 */
	public void initPlayer() {

		try {
			if (mediaPlayer != null) {
				mediaPlayer.pause();
				mediaPlayer = null;
			}
			if (lrcThread != null) {
				lrcThread = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化播放器
	 */
	private void initMusic() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.pause();
				mediaPlayer = null;

				SongMessage msg = new SongMessage();
				msg.setSongInfo(songInfo);
				msg.setType(SongMessage.SERVICEPAUSEEDMUSIC);
				ObserverManage.getObserver().setMessage(msg);
			}
			if (lrcThread != null) {
				lrcThread = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 播放歌曲
	 * 
	 * @param songInfo
	 */
	private void playInfoMusic(SongInfo msongInfo) {
		if (songInfo == null) {

			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.SERVICEERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGEPLAYSONGNULL);
			ObserverManage.getObserver().setMessage(msg);

			return;
		}
		File songFile = new File(songInfo.getFilePath());

		if (!songFile.exists()) {
			logger.error("播放文件不存在!");
			// 下一首
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.NEXTMUSIC);
			ObserverManage.getObserver().setMessage(songMessage);

			return;
		}

		try {
			if (mediaPlayer == null) {
				mediaPlayer = new Player();
				mediaPlayer.addListener(new PlayerListener() {

					@Override
					public void onEvent(PlayerEvent e) {
						if (e.getEventCode() == PlayerEventCode.STOPPED) {
							if (!BaseData.makeLrcDialogIsShow) {
								// 播放结束，播放下一首
								SongMessage songMessage = new SongMessage();
								songMessage.setType(SongMessage.NEXTMUSIC);
								ObserverManage.getObserver().setMessage(
										songMessage);
							} else {
								// 停止播放歌曲
								SongMessage songMessage = new SongMessage();
								songMessage.setType(SongMessage.STOPMUSIC);
								// 通知
								ObserverManage.getObserver().setMessage(
										songMessage);
							}
						} else if (e.getEventCode() == PlayerEventCode.SEEK_FINISHED) {
							if (mediaPlayer != null) {
								// 获取快进后的播放进度
								double currentMS = mediaPlayer
										.getCurrentMillis();
								long progress = Math.round(currentMS);
								baseCurrentMillis = progress;
							}
							isSeekFinish = true;
						}
					}
				});
				initVolume();

				AudioFileReader audioFileReader = TrackIO
						.getAudioFileReader(songFile.getName());
				Track track = audioFileReader.read(songFile);

				mediaPlayer.open(track);
				long millis = songInfo.getPlayProgress();
				if (millis != 0) {
					isSeekFinish = false;
					long seekBytes = AudioMath.millisToSamples(millis, track
							.getTrackData().getSampleRate());
					mediaPlayer.seek(seekBytes);
				} else {
					// 设置基本的进度
					baseCurrentMillis = 0;
				}
			}
		} catch (Exception e) {
			logger.error("不能播放此文件:" + songInfo.getFilePath());
			e.printStackTrace();

			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.NEXTMUSIC);
			ObserverManage.getObserver().setMessage(songMessage);
		}

		if (lrcThread == null) {
			lrcThread = new Thread(new LrcRunable());
			lrcThread.start();
		}

	}

	/**
	 * 歌词绘画线程，歌词绘画每隔100ms去刷新歌词页面
	 * 
	 * @author Administrator
	 * 
	 */
	private class LrcRunable implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
					if (mediaPlayer != null && mediaPlayer.isPlaying()
							&& isSeekFinish) {
						int status = MediaManage.getMediaManage()
								.getPlayStatus();

						// 只有当前正在播放才去刷新页面，如果当前正在快进，则不要刷新页面，免得页面出现闪烁
						if (songInfo != null && status == MediaManage.PLAYING) {

							// 由于直接获取当前的进度，在绘画歌词的时候会比较闪烁，所以，我这里采用获取line当前的frame所在的位置，再加上上一次快进的位置，得到当前的位置
							// 由于这种方法，导致了在快进的时候，要重新创建一个播放器和记录快进后的播放进度
							// 原谅我目前只想到这种方法
							double currentMS = mediaPlayer.getAudioOutput()
									.getCurrentMillis();
							long progress = Math.round(currentMS);
							// System.out.println(progress);
							// 等于0时不刷新，防止快进时闪屏
							if (progress == 0)
								continue;

							songInfo.setPlayProgress(progress
									+ baseCurrentMillis);
							SongMessage msg = new SongMessage();
							msg.setSongInfo(songInfo);
							msg.setType(SongMessage.SERVICEPLAYINGMUSIC);
							ObserverManage.getObserver().setMessage(msg);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

}

package com.happy.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.db.CategoryDB;
import com.happy.db.SongInfoDB;
import com.happy.model.Category;
import com.happy.model.EventIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.service.MediaPlayerService;
import com.happy.util.DateUtil;
import com.happy.util.IDGenerate;
import com.happy.util.MediaUtils;

/**
 * 播放器管理
 * 
 * @author Administrator
 * 
 */
public class MediaManage implements Observer {

	private static MediaManage _mediaManage;

	/**
	 * 播放列表
	 */
	private List<Category> categorys;

	/**
	 * 当前播放列表下的歌曲列表
	 */
	private List<SongInfo> songlist;

	/**
	 * 当前播放歌曲
	 */
	private SongInfo songInfo;

	/**
	 * 正在播放
	 */
	public static final int PLAYING = 0;
	/**
	 * 暂停
	 */
	public static final int PAUSE = 1;
	/**
	 * 快进
	 */
	public static final int SEEKTO = 2;
	/**
	 * 播放歌曲状态
	 */
	private int playStatus = PAUSE;
	/**
	 * 旧的播放歌曲id
	 */
	public static String oldPlayInfoID = "-1";

	public MediaManage() {
		ObserverManage.getObserver().addObserver(this);
	}

	public static MediaManage getMediaManage() {
		if (_mediaManage == null) {
			_mediaManage = new MediaManage();

		}
		return _mediaManage;
	}

	/**
	 * 初始化播放列表数据
	 */
	public void initPlayListData() {
		categorys = CategoryDB.getCategoryDB().getAllCategory();
		if (categorys == null || categorys.size() == 0) {
			// 创建【默认列表】
			categorys = new ArrayList<Category>();
			Category category = new Category("默认列表");
			category.setCid(IDGenerate.getId(Category.key));
			category.setCreateTime(DateUtil.dateToString(new Date()));
			// 加载默认歌曲
			String defFilePath = Constants.PATH_AUDIO + File.separator
					+ "蔡健雅 - Beautiful Love.mp3";
			File defFile = new File(defFilePath);

			List<SongInfo> songInfos = new ArrayList<SongInfo>();
			if (defFile.exists()) {
				SongInfo songInfo = MediaUtils.getSongInfoByFile(defFilePath);
				if (songInfo != null) {
					songInfo.setCategoryId(category.getCid());
					SongInfoDB.getSongInfoDB().add(songInfo);

					songInfos.add(songInfo);
				}
			}
			category.setSongInfos(songInfos);
			categorys.add(category);
			CategoryDB.getCategoryDB().add(category);

		} else {
			for (int i = 0; i < categorys.size(); i++) {
				Category category = categorys.get(i);
				// 获取该列表下的歌曲数据
				List<SongInfo> songInfos = SongInfoDB.getSongInfoDB()
						.getSongList(category.getCid());
				category.setSongInfos(songInfos);

				categorys.remove(i);
				categorys.add(i, category);
			}
		}
		// printData();
	}

	/**
	 * 添加歌曲到播放列表中
	 * 
	 * @param pId
	 *            播放列表的id
	 * @param songInfo
	 */
	public void addSongInfo(String pId, SongInfo songInfo) {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			if (category.getCid().equals(pId)) {
				List<SongInfo> songInfos = category.getSongInfos();
				songInfos.add(songInfo);
				category.setSongInfos(songInfos);

				categorys.remove(i);
				categorys.add(i, category);
				break;
			}
		}
		// printData();
	}

	/**
	 * 移除歌曲
	 * 
	 * @param pId
	 * @param sid
	 */
	public void delSongInfo(String pId, String sid) {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			if (category.getCid().equals(pId)) {
				List<SongInfo> songInfos = category.getSongInfos();

				for (int j = 0; j < songInfos.size(); j++) {
					SongInfo songInfo = songInfos.get(j);
					if (songInfo.getSid().equals(sid)) {
						songInfos.remove(j);
						break;
					}
				}

				category.setSongInfos(songInfos);

				categorys.remove(i);
				categorys.add(i, category);
				break;
			}
		}
		// printData();
	}

	/**
	 * 添加播放列表数据
	 * 
	 * @param category
	 */
	public void addCategory(Category category) {
		categorys.add(category);
		// printData();
	}

	/**
	 * 刪除播放列表
	 * 
	 * @param pId
	 */
	public void delCategory(String pId) {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			if (category.getCid().equals(pId)) {
				categorys.remove(i);
				break;
			}
		}
		// printData();
	}

	/**
	 * 获取该播放列表下的所有歌曲
	 * 
	 * @param pId
	 * @return
	 */
	public List<SongInfo> getSongInfoList(String pId) {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			if (category.getCid().equals(pId)) {
				return category.getSongInfos();
			}
		}
		return new ArrayList<SongInfo>();
	}

	/**
	 * 更新播放列表下的所有歌曲列表
	 * 
	 * @param pId
	 * @param songInfos
	 */
	public void updateSongInfoList(String pId, List<SongInfo> songInfos) {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			if (category.getCid().equals(pId)) {

				category.setSongInfos(songInfos);

				categorys.remove(i);
				categorys.add(i, category);
				break;
			}
		}
		// printData();
	}

	// /**
	// * 输出列表的所有数据
	// */
	// private void printData() {
	// for (int i = 0; i < categorys.size(); i++) {
	// Category category = categorys.get(i);
	// System.out.println("-----" + category.getCategoryName() + "-----");
	// List<SongInfo> songInfos = category.getSongInfos();
	// for (int j = 0; j < songInfos.size(); j++) {
	// SongInfo songInfo = songInfos.get(j);
	// System.out.println(songInfo.getDisplayName());
	// }
	// }
	// }

	/**
	 * 播放歌曲
	 */
	private void playMusic() {
		if (songInfo != null) {
			playInfoMusic(songInfo, false);
		} else {
			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.ERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGESONGNULL);
			ObserverManage.getObserver().setMessage(msg);
		}
	}

	/**
	 * 播放歌曲
	 * 
	 * @param songInfo
	 */
	private void playInfoMusic(SongInfo mSongInfo, boolean isInit) {

		this.songInfo = mSongInfo;

		if (isInit) {

			// 关闭当前的播放器
			MediaPlayerService.getMediaPlayerService().close();

			songInfo.setPlayProgress(0);
			SongMessage msg = new SongMessage();
			msg.setSongInfo(songInfo);
			msg.setType(SongMessage.INITMUSIC);
			ObserverManage.getObserver().setMessage(msg);
		}

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 设置当前的歌曲id
				BaseData.playInfoID = songInfo.getSid();

				playStatus = PLAYING;

				SongMessage msg2 = new SongMessage();
				msg2.setType(SongMessage.SERVICEPLAYMUSIC);
				msg2.setSongInfo(songInfo);
				ObserverManage.getObserver().setMessage(msg2);
			}

		}.start();

	}

	/**
	 * 快进歌曲
	 * 
	 * @param progress
	 */
	private void seekTo(int progress) {
		if (songInfo == null) {
			return;
		}

		// 快进的话，要修改当前的播放状态
		if (playStatus == PLAYING) {
			playStatus = SEEKTO;
			//
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.SERVICESEEKTOMUSIC);
			songMessage.setProgress(progress);
			ObserverManage.getObserver().setMessage(songMessage);
		} else {
			songInfo.setPlayProgress(progress);
			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.SERVICEPAUSEEDMUSIC);
			songMessage.setSongInfo(songInfo);
			ObserverManage.getObserver().setMessage(songMessage);
		}
	}

	/**
	 * 暂停歌曲
	 */
	private void pauseMusic() {
		if (songInfo != null) {

			playStatus = PAUSE;

			SongMessage msg = new SongMessage();
			msg.setSongInfo(songInfo);
			msg.setType(SongMessage.SERVICEPAUSEMUSIC);
			ObserverManage.getObserver().setMessage(msg);

		} else {
			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.ERRORMUSIC);
			msg.setErrorMessage(SongMessage.ERRORMESSAGESONGNULL);
			ObserverManage.getObserver().setMessage(msg);
		}
	}

	/**
	 * 停止当前播放
	 */
	public void stopToPlay() {
		playStatus = PAUSE;
		// 结束播放保存歌曲索引

		EventIntent eventIntent = new EventIntent();
		eventIntent.setEventType(EventIntent.SONGLIST);
		eventIntent.setpLId(BaseData.playInfoPID);
		eventIntent.setsId(BaseData.playInfoID);
		eventIntent.setMouseType(EventIntent.RESET);
		ObserverManage.getObserver().setMessage(eventIntent);

		oldPlayInfoID = BaseData.playInfoID;
		BaseData.playInfoID = "-1";

		// 结束播放

		SongMessage msg = new SongMessage();
		msg.setSongInfo(null);
		msg.setType(SongMessage.INITMUSIC);
		ObserverManage.getObserver().setMessage(msg);
	}

	/**
	 * 上一首
	 * 
	 * @param playModel
	 */
	private void preMusic(int playModel) {
		// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环

		// 先判断当前的播放模式，再根据播放模式来获取上一首歌曲的索引

		boolean isInit = true;

		int playIndex = getPlayingSongIndex();
		switch (playModel) {
		case 0:
			// 顺序播放
			// playIndex--;
			playIndex = getPrePlaySequenceIndex(playIndex);
			if (playIndex == -1 || playIndex >= songlist.size()) {
				stopToPlay();
				return;
			}

			if (songlist.size() != 0) {
				songInfo = songlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}

			break;
		case 1:
			// 随机播放

			// playIndex = new Random().nextInt(songlist.size());
			playIndex = getPlayRandomIndex();
			if (playIndex == -1 || playIndex >= songlist.size()) {
				stopToPlay();
				return;
			}

			if (songlist.size() != 0) {
				songInfo = songlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}
			break;
		case 2:
			// 循环播放

			// playIndex--;
			// if (playIndex < 0) {
			// playIndex = 0;
			// }
			playIndex = getPrePlayListRepeatIndex(playIndex);
			if (playIndex == -1 || playIndex >= songlist.size()) {
				stopToPlay();
				return;
			}
			if (songlist.size() != 0) {
				songInfo = songlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}
			break;
		case 3:
			// 单曲播放
			stopToPlay();
			return;
		case 4:
			// 单曲循环播放

			break;
		}

		// playIndex == -1说明在单曲播放状态下歌曲被移除了
		if (songInfo == null || songInfo.getSid() == null || playIndex == -1) {
			stopToPlay();
			return;
		}
		// 保存歌曲索引

		BaseData.playInfoID = songInfo.getSid();

		EventIntent eventIntent = new EventIntent();
		eventIntent.setEventType(EventIntent.SONGLIST);
		eventIntent.setpLId(BaseData.playInfoPID);
		eventIntent.setsId(BaseData.playInfoID);
		eventIntent.setMouseType(EventIntent.DOUBLECLICK);

		ObserverManage.getObserver().setMessage(eventIntent);

		playInfoMusic(songInfo, isInit);
	}

	/**
	 * 获取当前歌曲的索引
	 * 
	 * @return
	 */
	private int getPlayingSongIndex() {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			List<SongInfo> songInfos = category.getSongInfos();
			for (int j = 0; j < songInfos.size(); j++) {
				SongInfo songInfo = songInfos.get(j);
				if (songInfo.getSid().equals(oldPlayInfoID)) {
					this.songlist = songInfos;
					return j;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param playIndex
	 * @return
	 */
	private int getPlayRandomIndex() {
		int index = new Random().nextInt(songlist.size());
		for (int i = index; i < songlist.size(); i++) {
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取列表循环播放上一首
	 * 
	 * @param oldPlayIndex
	 * @return
	 */
	private int getPrePlayListRepeatIndex(int oldPlayIndex) {
		for (int i = oldPlayIndex - 1; i >= 0; i--) {
			if (i < 0)
				break;
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		for (int i = songlist.size() - 1; i >= 0; i--) {
			if (i < 0)
				break;
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取顺序播放上一首
	 * 
	 * @param oldPlayIndex
	 * @return
	 */
	private int getPrePlaySequenceIndex(int oldPlayIndex) {
		for (int i = oldPlayIndex - 1; i >= 0; i--) {
			if (i < 0)
				break;
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 播放下一首歌曲
	 * 
	 * @param playModel
	 *            播放模式
	 */
	private void nextMusic(int playModel) {
		// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环

		// 先判断当前的播放模式，再根据播放模式来获取下一首歌曲的索引

		boolean isInit = true;

		// 顺序播放
		int playIndex = getPlayingSongIndex();
		switch (playModel) {
		case 0:

			// playIndex++;
			playIndex = getNextPlaySequenceIndex(playIndex);
			if (playIndex == -1 || playIndex >= songlist.size()) {
				stopToPlay();
				return;
			}
			if (songlist.size() != 0) {
				songInfo = songlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}

			break;
		case 1:
			// 随机播放

			// playIndex = new Random().nextInt(songlist.size());
			playIndex = getPlayRandomIndex();
			if (playIndex == -1 || playIndex >= songlist.size()) {
				stopToPlay();
				return;
			}
			if (songlist.size() != 0) {
				songInfo = songlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}
			break;
		case 2:
			// 循环播放

			// playIndex++;
			playIndex = getNextPlayListRepeatIndex(playIndex);
			// if (playIndex >= songlist.size()) {
			// playIndex = 0;
			// }
			if (playIndex == -1 || playIndex >= songlist.size()) {
				stopToPlay();
				return;
			}

			if (songlist.size() != 0) {
				songInfo = songlist.get(playIndex);
			} else {
				stopToPlay();
				return;
			}

			break;
		case 3:
			// 单曲播放
			stopToPlay();
			return;
		case 4:
			// 单曲循环播放
			break;
		}
		// playIndex == -1说明在单曲播放状态下歌曲被移除了
		if (songInfo == null || songInfo.getSid() == null || playIndex == -1) {
			stopToPlay();
			return;
		}

		BaseData.playInfoID = songInfo.getSid();

		EventIntent eventIntent = new EventIntent();
		eventIntent.setEventType(EventIntent.SONGLIST);
		eventIntent.setpLId(BaseData.playInfoPID);
		eventIntent.setsId(BaseData.playInfoID);
		eventIntent.setMouseType(EventIntent.DOUBLECLICK);

		ObserverManage.getObserver().setMessage(eventIntent);

		playInfoMusic(songInfo, isInit);
	}

	/**
	 * 获取列表循环播放的下一首的索引
	 * 
	 * @param oldPlayIndex
	 * @return
	 */
	private int getNextPlayListRepeatIndex(int oldPlayIndex) {
		for (int i = oldPlayIndex + 1; i < songlist.size(); i++) {
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		for (int i = 0; i < songlist.size(); i++) {
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取顺序播放的下一首的索引
	 * 
	 * @param oldPlayIndex
	 *            旧的索引
	 * @return
	 */
	private int getNextPlaySequenceIndex(int oldPlayIndex) {
		for (int i = oldPlayIndex + 1; i < songlist.size(); i++) {
			SongInfo songInfo = songlist.get(i);
			if (songInfo != null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 结束音乐播放
	 */
	protected void stopMusic() {
		SongMessage msg = new SongMessage();
		msg.setType(SongMessage.SERVICESTOPMUSIC);
		ObserverManage.getObserver().setMessage(msg);
	}

	public int getPlayStatus() {
		return playStatus;
	}

	public void setPlayStatus(int playStatus) {
		this.playStatus = playStatus;
	}

	public List<Category> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<Category> categorys) {
		this.categorys = categorys;
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

	public void setSongInfo(SongInfo songInfo) {
		this.songInfo = songInfo;
	}

	@Override
	public void update(Observable o, Object data) {

		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.PLAYMUSIC) {
				playMusic();
			} else if (songMessage.getType() == SongMessage.SEEKTOMUSIC) {
				int progress = songMessage.getProgress();
				seekTo(progress);
			} else if (songMessage.getType() == SongMessage.PLAYINFOMUSIC) {
				pauseMusic();
				playInfoMusic(songMessage.getSongInfo(), true);
			} else if (songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC) {
				if (songInfo != null && songMessage.getSongInfo() != null) {
					// 相同，则更新进度
					if (songInfo.getSid().equals(
							songMessage.getSongInfo().getSid())) {
						songInfo = songMessage.getSongInfo();
					}
				}
			} else if (songMessage.getType() == SongMessage.PAUSEMUSIC) {
				pauseMusic();
			} else if (songMessage.getType() == SongMessage.NEXTMUSIC) {
				if (songInfo == null) {
					return;
				}
				stopToPlay();
				nextMusic(BaseData.playModel);
			} else if (songMessage.getType() == SongMessage.PREMUSIC) {
				if (songInfo == null) {
					return;
				}
				stopToPlay();
				preMusic(BaseData.playModel);
			} else if (songMessage.getType() == SongMessage.REINITMUSIC) {
				if (songInfo == null) {
					return;
				}
				// 重新播放歌曲
				playInfoMusic(songInfo, true);
			} else if (songMessage.getType() == SongMessage.STOPMUSIC) {
				if (songInfo == null) {
					return;
				}
				playStatus = PAUSE;
				stopMusic();
			} else if (songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC) {
				playStatus = PAUSE;
				if (songInfo != null && songMessage.getSongInfo() != null) {
					// 相同，则更新进度
					if (songInfo.getSid().equals(
							songMessage.getSongInfo().getSid())) {
						songInfo = songMessage.getSongInfo();
					}
				}
			}
		}
	}

}

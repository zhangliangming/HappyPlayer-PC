package com.happy.util;

import java.io.File;
import java.util.Date;

import com.happy.lyrics.utils.FileUtils;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.model.SongInfo;
import com.tulskiy.musique.audio.AudioFileReader;
import com.tulskiy.musique.model.Track;
import com.tulskiy.musique.system.TrackIO;
import com.tulskiy.musique.util.AudioMath;

public class MediaUtils {
	/**
	 * 通过文件获取mp3的相关数据信息
	 * 
	 * @param filePath
	 * @return
	 */

	public static SongInfo getSongInfoByFile(String filePath) {
		File sourceFile = new File(filePath);
		if (!sourceFile.exists())
			return null;
		SongInfo songInfo = null;
		try {

			AudioFileReader audioFileReader = TrackIO
					.getAudioFileReader(sourceFile.getName());
			Track track = audioFileReader.read(sourceFile);

			double totalMS = AudioMath.samplesToMillis(track.getTrackData()
					.getTotalSamples(), track.getTrackData().getSampleRate());
			long duration = Math.round(totalMS);

			String durationStr = TimeUtils.parseString((int) duration);

			songInfo = new SongInfo();
			// 文件名
			String displayName = sourceFile.getName();

			int index = displayName.lastIndexOf(".");
			displayName = displayName.substring(0, index);

			String artist = "";
			String title = "";
			if (displayName.contains("-")) {
				String[] titleArr = displayName.split("-");
				artist = titleArr[0].trim();
				title = titleArr[1].trim();
			} else {
				title = displayName;
			}

			if (sourceFile.length() < 1024 * 1024) {
				return null;
			}

			songInfo.setSid(IDGenerate.getId("SI-"));
			songInfo.setDisplayName(displayName);
			songInfo.setSinger(artist);
			songInfo.setTitle(title);
			songInfo.setDuration(duration);
			songInfo.setDurationStr(durationStr);
			songInfo.setSize(sourceFile.length());
			songInfo.setSizeStr(FileUtils.getFileSize(sourceFile.length()));
			songInfo.setFilePath(filePath);
			songInfo.setType(SongInfo.LOCALSONG);
			// songInfo.setIslike(SongInfo.UNLIKE);
			// songInfo.setDownloadStatus(SongInfo.DOWNLOADED);
			songInfo.setCreateTime(DateUtil.dateToString(new Date()));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return songInfo;

	}
}

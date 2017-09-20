package com.happy.util;

import java.io.File;
import java.util.TreeMap;

import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.utils.LyricsIOUtils;

/**
 * 
 * @author zhangliangming
 * @功能 歌词解析器
 * 
 */
public class LyricsParserUtil {

	private LyricsFileReader lyricsFileReader;
	private LyricsInfo lyricsIfno;

	/**
	 * TreeMap，用于封装每行的歌词信息
	 */
	private TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap = null;

	public LyricsParserUtil(File lyricsFile) {
		lyricsFileReader = LyricsIOUtils.getLyricsFileReader(lyricsFile);
		try {
			lyricsIfno = lyricsFileReader.readFile(lyricsFile);

			lyricsLineTreeMap = lyricsIfno.getLyricsLineInfoTreeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LyricsParserUtil() {
		if (lyricsLineTreeMap == null)
			lyricsLineTreeMap = new TreeMap<Integer, LyricsLineInfo>();
	}

	/**
	 * 通过播放的进度，获取所唱歌词行数
	 * 
	 * @param msec
	 *            歌曲的当前时间值
	 * @return
	 */
	public int getLineNumberFromCurPlayingTime(int msec) {
		for (int i = 0; i < lyricsLineTreeMap.size(); i++) {
			if (msec >= lyricsLineTreeMap.get(i).getStartTime()
					&& msec <= lyricsLineTreeMap.get(i).getEndTime()) {
				return i;
			}
			if (msec > lyricsLineTreeMap.get(i).getEndTime()
					&& i + 1 < lyricsLineTreeMap.size()
					&& msec < lyricsLineTreeMap.get(i + 1).getStartTime()) {
				return i;
			}
		}
		if (msec >= lyricsLineTreeMap.get(lyricsLineTreeMap.size() - 1)
				.getEndTime()) {
			return lyricsLineTreeMap.size() - 1;
		}
		return -1;
	}

	/**
	 * 获取当前时间正在唱的歌词的第几个字
	 * 
	 * @param lyricsLineNum
	 *            行数
	 * @param msec
	 *            歌曲的当前时间值
	 * @return
	 */
	public int getDisWordsIndexFromCurPlayingTime(int lyricsLineNum, int msec) {
		if (lyricsLineNum == -1)
			return -1;
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (msec < elapseTime) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取当前歌词的第几个歌词的播放进度
	 * 
	 * @param lyricsLineNum
	 * @param msec
	 * @return
	 */
	public int getLenFromCurPlayingTime(int lyricsLineNum, int msec) {
		if (lyricsLineNum == -1)
			return 0;
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (msec < elapseTime) {
				return lyrLine.getWordsDisInterval()[i] - (elapseTime - msec);
			}
		}
		return 0;
	}

	/**
	 * 获取当前上下移动的距离
	 * 
	 * @param lyricsLineNum
	 *            当前 歌词行
	 * @param msec
	 *            当前播放的进度
	 * @param sy
	 *            要上下移动的总距离
	 * @return 要上下移动的距离
	 */
	public float getOffsetDYFromCurPlayingTime(int lyricsLineNum, int msec,
			int sy) {
		if (lyricsLineNum == -1)
			return sy;
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		int endTime = lyrLine.getEndTime();
		// 以整行歌词的1/3时间 作为上下移动的时间
		// int dTime = (endTime - elapseTime) / 3;
		// if (msec < elapseTime + dTime) {
		// float dy = (float) sy / dTime;
		// return dy * (dTime - (elapseTime + dTime - msec));
		// }
		// 以第一个歌词的时间为上下移动的时间
		// if (lyrLine.getLyricsWords().length != 0) {
		// int dTime = lyrLine.wordsDisInterval[0];
		// float dy = (float) sy / dTime;
		// if (msec < elapseTime + dTime) {
		// return dy * (dTime - (elapseTime + dTime - msec));
		// }
		// }

		// 默认300ms为上下移动的时间
		int dTime = Math.min(400, (endTime - elapseTime) / 2);
		if (msec < elapseTime + dTime) {
			float dy = (float) sy / dTime;
			return dy * (dTime - (elapseTime + dTime - msec));
		}

		return sy;
	}

	/**
	 * 
	 * @param lyricsLineNum
	 * @param msec
	 * @param height
	 *            上下移动的总长度
	 * @return 需要移动的距离
	 */
	public float getoffsetYFromCurPlayingTime(int lyricsLineNum, int msec,
			float height) {
		if (lyricsLineNum == -1)
			return 0;
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		int startTime = lyrLine.getStartTime();
		int endTime = lyrLine.getEndTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (msec < elapseTime) {
				int time = lyrLine.getWordsDisInterval()[i]
						- (elapseTime - msec);
				return height / (endTime - startTime) * time;
			}
		}
		return 0;
	}

	/**
	 * 获取当前所在行歌词的间隔时间长度
	 * 
	 * @param lyricsLineNum
	 * @return
	 */
	public int getOffsetYTimeFromCurPlayingTime(int lyricsLineNum) {
		if (lyricsLineNum == -1)
			return 0;
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int startTime = lyrLine.getStartTime();
		int endTime = lyrLine.getEndTime();
		return (endTime - startTime);
	}

	public TreeMap<Integer, LyricsLineInfo> getLyricsLineTreeMap() {
		return lyricsLineTreeMap;
	}

	public void setLyricsLineTreeMap(
			TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap) {
		this.lyricsLineTreeMap = lyricsLineTreeMap;
	}

	public LyricsInfo getLyricsIfno() {
		return lyricsIfno;
	}

	public void setLyricsIfno(LyricsInfo lyricsIfno) {
		this.lyricsIfno = lyricsIfno;
	}

}

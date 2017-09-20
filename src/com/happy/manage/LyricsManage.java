package com.happy.manage;

import java.io.File;

import com.happy.util.LyricsParserUtil;

/**
 * 歌词处理类
 * 
 * @author Administrator
 * 
 */
public class LyricsManage {

	/**
	 * 当前歌词的歌曲sid
	 */
	private static String mSid = "";

	/**
	 * 当前歌词解析器
	 */
	private static LyricsParserUtil lyricsParser = null;

	/**
	 * 通过歌曲的sid和歌词路径获取歌词解析器
	 * 
	 * @param sid
	 * @param lrcFilePath
	 * @return
	 */
	public static LyricsParserUtil getLyricsParser(String sid, File lrcFile) {
		if (sid.equals(mSid)) {
			if (lyricsParser == null) {
				lyricsParser = new LyricsParserUtil(lrcFile);
			}
		} else {
			mSid = sid;
			lyricsParser = new LyricsParserUtil(lrcFile);
		}
		return lyricsParser;
	}

	/**
	 * 获取歌词解析
	 * @param sid
	 * @return
	 */
	public static LyricsParserUtil getLyricsParser(String sid) {
		if (sid.equals(mSid)) {
			return lyricsParser;
		}
		return null;
	}

	/**
	 * 
	 * @param sid
	 * @param lrcInputStream
	 * @return
	 */
	public static LyricsParserUtil getKscLyricsParserByInputStream(String sid) {
		if (sid.equals(mSid)) {
			if (lyricsParser == null) {
				lyricsParser = new LyricsParserUtil();
			}
		} else {
			mSid = sid;
			lyricsParser = new LyricsParserUtil();

		}
		return lyricsParser;
	}

	/**
	 * 清空数据
	 */
	public static void clean() {
		lyricsParser = null;
	}
}

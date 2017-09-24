package com.happy.manage;

import java.io.File;

import com.happy.util.LyricsUtil;

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
	private static LyricsUtil mLyricsUtil = null;

	/**
	 * 通过歌曲的sid和歌词路径获取歌词解析器
	 * 
	 * @param sid
	 * @param lrcFilePath
	 * @return
	 */
	public static LyricsUtil getLyricsParser(String sid, File lrcFile) {
		if (sid.equals(mSid)) {
			if (mLyricsUtil == null) {
				mLyricsUtil = new LyricsUtil(lrcFile);
			}
		} else {
			mSid = sid;
			mLyricsUtil = new LyricsUtil(lrcFile);
		}
		return mLyricsUtil;
	}

	/**
	 * 获取歌词解析
	 * 
	 * @param sid
	 * @return
	 */
	public static LyricsUtil getLyricsParser(String sid) {
		if (sid.equals(mSid)) {
			return mLyricsUtil;
		}
		return null;
	}

	/**
	 * 
	 * @param sid
	 * @param lrcInputStream
	 * @return
	 */
	public static LyricsUtil getKscLyricsParserByInputStream(String sid) {
		if (sid.equals(mSid)) {
			if (mLyricsUtil == null) {
				mLyricsUtil = new LyricsUtil();
			}
		} else {
			mSid = sid;
			mLyricsUtil = new LyricsUtil();

		}
		return mLyricsUtil;
	}

	/**
	 * 清空数据
	 */
	public static void clean() {
		mLyricsUtil = null;
	}
}

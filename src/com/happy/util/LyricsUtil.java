package com.happy.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.SwingWorker;

import org.apache.commons.codec.binary.Base64;

import com.happy.common.Constants;
import com.happy.lyrics.LyricsFileReader;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.LyricsTag;
import com.happy.lyrics.model.TranslateLrcLineInfo;
import com.happy.lyrics.utils.CharUtils;
import com.happy.lyrics.utils.LyricsIOUtils;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;

/**
 * 歌词处理类 Created by zhangliangming on 2017/9/11.
 */

public class LyricsUtil {
	/**
	 * 时间补偿值,其单位是毫秒，正值表示整体提前，负值相反。这是用于总体调整显示快慢的。
	 */
	private int mDefOffset = 0;
	/**
	 * 增量
	 */
	private int mOffset = 0;

	private LyricsInfo mLyricsIfno;

	/**
	 * 歌词文件路径
	 */
	private String mLrcFilePath;

	private String mHash;

	/**
	 * 默认的歌词集合
	 */
	private TreeMap<Integer, LyricsLineInfo> mDefLyricsLineTreeMap;

	/**
	 * 翻译行歌词
	 */
	private List<LyricsLineInfo> mTranslateLrcLineInfos;
	/**
	 * 音译歌词
	 */
	private List<LyricsLineInfo> mTransliterationLrcLineInfos;

	/**
	 * 没有的额外的歌词
	 */
	public static final int NOEXTRA_LRC = 0;
	/**
	 * 翻译歌词
	 */
	public static final int TRANSLATE_LRC = 1;
	/**
	 * 音译歌词
	 */
	public static final int TRANSLITERATION_LRC = 2;

	/**
	 * 翻译和音译歌词
	 */
	public static final int TRANSLATE_AND_TRANSLITERATION_LRC = 3;

	/**
	 * 额外的歌词类型
	 */
	public int mExtraLrcType = NOEXTRA_LRC;

	/**
	 * 
	 * @param lrcFile
	 */
	public LyricsUtil(File lrcFile) {
		loadLrc(lrcFile);
	}

	public LyricsUtil() {

	}

	/**
	 * 加载歌词文件
	 * 
	 * @param context
	 * @param sid
	 * @param title
	 * @param singer
	 * @param lrcUrl
	 */
	public static void loadLyrics(final String sid, String title,
			String singer, final String displayName, String lrcUrl,
			final int type) {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {

				File lrcFile = getLrcFile(displayName);
				if (lrcFile == null) {
					return null;
				}

				SongMessage songMessage = new SongMessage();

				if (type == SongMessage.KSCTYPELRC) {

					songMessage.setType(SongMessage.LRCKSCLOADED);
				} else if (type == SongMessage.KSCTYPEDES) {

					songMessage.setType(SongMessage.DESKSCLOADED);
				} else if (type == SongMessage.KSCTYPELOCK) {

					songMessage.setType(SongMessage.LOCKKSCLOADED);
				}

				songMessage.setLrcFilePath(lrcFile.getPath());
				songMessage.setSid(sid);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);

				return null;

			}

			@Override
			protected void done() {

			}
		}.execute();
	}

	/**
	 * 通过音频文件名获取歌词文件
	 * 
	 * @param displayName
	 * @return
	 */
	public static File getLrcFile(String displayName) {
		File lrcFile = null;
		List<String> lrcExts = LyricsIOUtils.getSupportLyricsExts();
		for (int i = 0; i < lrcExts.size(); i++) {
			String lrcFilePath = Constants.PATH_LYRICS + File.separator
					+ displayName + "." + lrcExts.get(i);
			lrcFile = new File(lrcFilePath);
			if (lrcFile.exists()) {
				break;
			}
		}
		return lrcFile;
	}

	/**
	 * 加载歌词数据
	 * 
	 * @param lyricsFile
	 */
	public void loadLrc(File lyricsFile) {
		mLrcFilePath = lyricsFile.getPath();
		LyricsFileReader lyricsFileReader = LyricsIOUtils
				.getLyricsFileReader(lyricsFile);
		try {
			mLyricsIfno = lyricsFileReader.readFile(lyricsFile);
			Map<String, Object> tags = mLyricsIfno.getLyricsTags();
			if (tags.containsKey(LyricsTag.TAG_OFFSET)) {
				mDefOffset = 0;
				try {
					mDefOffset = Integer.parseInt((String) tags
							.get(LyricsTag.TAG_OFFSET));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mDefOffset = 0;
			}
			// 默认歌词行
			mDefLyricsLineTreeMap = mLyricsIfno.getLyricsLineInfoTreeMap();
			// 翻译歌词集合
			if (mLyricsIfno.getTranslateLyricsInfo() != null)
				mTranslateLrcLineInfos = getTranslateLrc(mDefLyricsLineTreeMap,
						mLyricsIfno.getTranslateLyricsInfo()
								.getTranslateLrcLineInfos());

			// 音译歌词集合
			if (mLyricsIfno.getTransliterationLyricsInfo() != null)
				mTransliterationLrcLineInfos = getTransliterationLrc(
						mDefLyricsLineTreeMap, mLyricsIfno
								.getTransliterationLyricsInfo()
								.getTransliterationLrcLineInfos());

			initExtraLrcType();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param base64FileContentString
	 *            歌词base64文件
	 * @param saveLrcFile
	 *            要保存的的lrc文件
	 * @param fileName
	 *            含后缀名的文件名称
	 */
	public void loadLrc(String base64FileContentString, File saveLrcFile,
			String fileName) {
		loadLrc(Base64.decodeBase64(base64FileContentString), saveLrcFile,
				fileName);
	}

	/**
	 * @param base64ByteArray
	 *            歌词base64数组
	 * @param saveLrcFile
	 * @param fileName
	 */
	public void loadLrc(byte[] base64ByteArray, File saveLrcFile,
			String fileName) {
		if (saveLrcFile != null)
			mLrcFilePath = saveLrcFile.getPath();
		LyricsFileReader lyricsFileReader = LyricsIOUtils
				.getLyricsFileReader(fileName);
		try {
			mLyricsIfno = lyricsFileReader.readLrcText(base64ByteArray,
					saveLrcFile);
			Map<String, Object> tags = mLyricsIfno.getLyricsTags();
			if (tags.containsKey(LyricsTag.TAG_OFFSET)) {
				mDefOffset = 0;
				try {
					mDefOffset = Integer.parseInt((String) tags
							.get(LyricsTag.TAG_OFFSET));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mDefOffset = 0;
			}
			// 默认歌词行
			mDefLyricsLineTreeMap = mLyricsIfno.getLyricsLineInfoTreeMap();
			// 翻译歌词集合
			if (mLyricsIfno.getTranslateLyricsInfo() != null)
				mTranslateLrcLineInfos = getTranslateLrc(mDefLyricsLineTreeMap,
						mLyricsIfno.getTranslateLyricsInfo()
								.getTranslateLrcLineInfos());
			// 音译歌词集合
			if (mLyricsIfno.getTransliterationLyricsInfo() != null)
				mTransliterationLrcLineInfos = getTransliterationLrc(
						mDefLyricsLineTreeMap, mLyricsIfno
								.getTransliterationLyricsInfo()
								.getTransliterationLrcLineInfos());

			initExtraLrcType();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化额外歌词类型
	 */
	private void initExtraLrcType() {

		// 判断音译和翻译歌词
		if (mTranslateLrcLineInfos != null && mTranslateLrcLineInfos.size() > 0
				&& mTransliterationLrcLineInfos != null
				&& mTransliterationLrcLineInfos.size() > 0) {
			// 有翻译歌词和音译歌词
			mExtraLrcType = TRANSLATE_AND_TRANSLITERATION_LRC;
		} else if (mTranslateLrcLineInfos != null
				&& mTranslateLrcLineInfos.size() > 0) {
			// 有翻译歌词
			mExtraLrcType = TRANSLATE_LRC;
		} else if (mTransliterationLrcLineInfos != null
				&& mTransliterationLrcLineInfos.size() > 0) {
			// 音译歌词
			mExtraLrcType = TRANSLITERATION_LRC;
		} else {
			// 无翻译歌词和音译歌词
			mExtraLrcType = NOEXTRA_LRC;
		}
	}

	/**
	 * 通过播放的进度，获取所唱歌词行数
	 * 
	 * @param oldPlayingTime
	 * @return
	 */
	public int getLineNumber(
			TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap,
			int oldPlayingTime) {

		// 添加歌词增量
		int curPlayingTime = oldPlayingTime + getPlayOffset();

		for (int i = 0; i < lyricsLineTreeMap.size(); i++) {
			if (curPlayingTime >= lyricsLineTreeMap.get(i).getStartTime()
					&& curPlayingTime <= lyricsLineTreeMap.get(i).getEndTime()) {
				return i;
			}
			if (curPlayingTime > lyricsLineTreeMap.get(i).getEndTime()
					&& i + 1 < lyricsLineTreeMap.size()
					&& curPlayingTime < lyricsLineTreeMap.get(i + 1)
							.getStartTime()) {
				return i;
			}
		}
		if (curPlayingTime >= lyricsLineTreeMap.get(
				lyricsLineTreeMap.size() - 1).getEndTime()) {
			return lyricsLineTreeMap.size() - 1;
		}
		return 0;
	}

	/**
	 * 通过播放的进度，获取所唱歌词行数
	 * 
	 * @param lyricsLineInfos
	 * @param oldPlayingTime
	 * @return
	 */
	public int getLineNumber(List<LyricsLineInfo> lyricsLineInfos,
			int oldPlayingTime) {

		// 添加歌词增量
		int curPlayingTime = oldPlayingTime + getPlayOffset();

		for (int i = 0; i < lyricsLineInfos.size(); i++) {
			if (curPlayingTime >= lyricsLineInfos.get(i).getStartTime()
					&& curPlayingTime <= lyricsLineInfos.get(i).getEndTime()) {
				return i;
			}
			if (curPlayingTime > lyricsLineInfos.get(i).getEndTime()
					&& i + 1 < lyricsLineInfos.size()
					&& curPlayingTime < lyricsLineInfos.get(i + 1)
							.getStartTime()) {
				return i;
			}
		}
		if (curPlayingTime >= lyricsLineInfos.get(lyricsLineInfos.size() - 1)
				.getEndTime()) {
			return lyricsLineInfos.size() - 1;
		}
		return 0;
	}

	/**
	 * 获取当前时间正在唱的歌词的第几个字
	 * 
	 * @param lyricsLineNum
	 *            行数
	 * @param oldPlayingTime
	 * @return
	 */
	public int getDisWordsIndex(
			TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap,
			int lyricsLineNum, int oldPlayingTime) {
		if (lyricsLineNum == -1)
			return -1;

		// 添加歌词增量
		int curPlayingTime = oldPlayingTime + getPlayOffset();
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {

			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (curPlayingTime < elapseTime) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取当前时间正在唱的歌词的第几个字
	 * 
	 * @param lyricsLineInfos
	 * @param lyricsLineNum
	 * @param oldPlayingTime
	 * @return
	 */
	public int getDisWordsIndex(List<LyricsLineInfo> lyricsLineInfos,
			int lyricsLineNum, int oldPlayingTime) {
		if (lyricsLineNum == -1)
			return -1;

		// 添加歌词增量
		int curPlayingTime = oldPlayingTime + getPlayOffset();
		LyricsLineInfo lyrLine = lyricsLineInfos.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {

			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (curPlayingTime < elapseTime) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取当前歌词的第几个歌词的播放时间
	 * 
	 * @param lyricsLineNum
	 *            行数
	 * @param oldPlayingTime
	 * @return
	 */
	public int getDisWordsIndexLenTime(
			TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap,
			int lyricsLineNum, int oldPlayingTime) {
		if (lyricsLineNum == -1)
			return 0;
		// 添加歌词增量
		int curPlayingTime = oldPlayingTime + getPlayOffset();
		LyricsLineInfo lyrLine = lyricsLineTreeMap.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (curPlayingTime < elapseTime) {
				return lyrLine.getWordsDisInterval()[i]
						- (elapseTime - curPlayingTime);
			}
		}
		return 0;
	}

	/**
	 * 获取当前歌词的第几个歌词的播放时间
	 * 
	 * @param lyricsLineInfos
	 * @param lyricsLineNum
	 * @param oldPlayingTime
	 * @return
	 */
	public int getDisWordsIndexLenTime(List<LyricsLineInfo> lyricsLineInfos,
			int lyricsLineNum, int oldPlayingTime) {
		if (lyricsLineNum == -1)
			return 0;
		// 添加歌词增量
		int curPlayingTime = oldPlayingTime + getPlayOffset();
		LyricsLineInfo lyrLine = lyricsLineInfos.get(lyricsLineNum);
		int elapseTime = lyrLine.getStartTime();
		for (int i = 0; i < lyrLine.getLyricsWords().length; i++) {
			elapseTime += lyrLine.getWordsDisInterval()[i];
			if (curPlayingTime < elapseTime) {
				return lyrLine.getWordsDisInterval()[i]
						- (elapseTime - curPlayingTime);
			}
		}
		return 0;
	}

	/**
	 * 从默认歌词集合中，替换翻译歌词，获取翻译歌词
	 * 
	 * @param mDefLyricsLineTreeMap
	 * @param translateLrcLineInfos
	 * @return
	 */
	private List<LyricsLineInfo> getTranslateLrc(
			TreeMap<Integer, LyricsLineInfo> mDefLyricsLineTreeMap,
			List<TranslateLrcLineInfo> translateLrcLineInfos) {
		List<LyricsLineInfo> newLyricsLineInfos = new ArrayList<LyricsLineInfo>();
		for (int i = 0; i < mDefLyricsLineTreeMap.size(); i++) {
			TranslateLrcLineInfo origLyricsLineInfo = translateLrcLineInfos
					.get(i);
			LyricsLineInfo defLyricsLineInfo = mDefLyricsLineTreeMap.get(i);

			// 构造新的翻译行歌词
			LyricsLineInfo newLyricsLineInfo = new LyricsLineInfo();
			newLyricsLineInfo.copy(newLyricsLineInfo, defLyricsLineInfo);
			//
			String lineLyrics = origLyricsLineInfo.getLineLyrics();
			newLyricsLineInfo.setLineLyrics(lineLyrics);
			String[] newLyricsWords = getLyricsWords(lineLyrics);
			int[] newWordsDisInterval = getWordsDisInterval(newLyricsWords,
					newLyricsLineInfo);
			newLyricsLineInfo.setLyricsWords(newLyricsWords);
			newLyricsLineInfo.setWordsDisInterval(newWordsDisInterval);
			//
			newLyricsLineInfos.add(newLyricsLineInfo);
		}
		return newLyricsLineInfos;
	}

	/**
	 * 获取每个字的时间
	 * 
	 * @param newLyricsWords
	 * @param newLyricsLineInfo
	 * @return
	 */
	private int[] getWordsDisInterval(String[] newLyricsWords,
			LyricsLineInfo newLyricsLineInfo) {
		int[] wordsDisInterval = new int[newLyricsWords.length];
		int startTime = newLyricsLineInfo.getStartTime();
		int endTime = newLyricsLineInfo.getEndTime();
		int avgTime = (endTime - startTime) / wordsDisInterval.length;
		for (int i = 0; i < wordsDisInterval.length; i++) {
			wordsDisInterval[i] = avgTime;
		}

		return wordsDisInterval;
	}

	/**
	 * 根据行歌词，分割字
	 * 
	 * @param lineLyrics
	 * @return
	 */
	private String[] getLyricsWords(String lineLyrics) {
		Stack<String> lrcStack = new Stack<String>();
		if (StringUtils.isBlank(lineLyrics)) {
			lrcStack.add("");
		} else {
			String temp = "";
			for (int i = 0; i < lineLyrics.length(); i++) {
				char c = lineLyrics.charAt(i);
				if (CharUtils.isChinese(c) || CharUtils.isHangulSyllables(c)
						|| CharUtils.isHiragana(c)) {

					if (!temp.equals("")) {
						lrcStack.push(temp);
						temp = "";
					}

					lrcStack.push(String.valueOf(c));
				} else if (Character.isSpaceChar(c)) {
					if (!temp.equals("")) {
						lrcStack.push(temp);
						temp = "";
					}
					String tw = lrcStack.pop();
					if (tw != null) {
						lrcStack.push("" + tw + " " + "");
					}
				} else {
					temp += String.valueOf(c);
				}
			}
			//
			if (!temp.equals("")) {
				lrcStack.push("" + temp + "");
				temp = "";
			}
		}

		String[] lyricsWords = new String[lrcStack.size()];
		Iterator<String> it = lrcStack.iterator();
		int i = 0;
		while (it.hasNext()) {
			lyricsWords[i++] = it.next();
		}
		return lyricsWords;
	}

	/**
	 * 从默认歌词集合中，替换音译歌词，获取音译歌词集合
	 * 
	 * @param mDefLyricsLineTreeMap
	 * @param transliterationLrcLineInfos
	 * @return
	 */
	private List<LyricsLineInfo> getTransliterationLrc(
			TreeMap<Integer, LyricsLineInfo> mDefLyricsLineTreeMap,
			List<LyricsLineInfo> transliterationLrcLineInfos) {
		List<LyricsLineInfo> newLyricsLineInfos = new ArrayList<LyricsLineInfo>();
		for (int i = 0; i < mDefLyricsLineTreeMap.size(); i++) {
			LyricsLineInfo origLyricsLineInfo = transliterationLrcLineInfos
					.get(i);
			LyricsLineInfo defLyricsLineInfo = mDefLyricsLineTreeMap.get(i);

			// 构造新的音译行歌词
			LyricsLineInfo newLyricsLineInfo = new LyricsLineInfo();
			newLyricsLineInfo.copy(newLyricsLineInfo, defLyricsLineInfo);
			//
			String[] defLyricsWords = defLyricsLineInfo.getLyricsWords();
			String[] origLyricsWords = origLyricsLineInfo.getLyricsWords();
			String[] newLyricsWords = new String[defLyricsWords.length];
			String newLineLyrics = "";
			for (int j = 0; j < defLyricsWords.length; j++) {
				if (defLyricsWords[j].lastIndexOf(" ") != -1) {
					newLyricsWords[j] = origLyricsWords[j].trim() + " ";
				} else {
					String origLyricsWordsString = origLyricsWords[j].trim();
					boolean isWord = origLyricsWordsString.matches("[a-zA-Z]+");
					if (isWord) {
						newLyricsWords[j] = origLyricsWords[j].trim() + " ";
					} else {
						newLyricsWords[j] = origLyricsWords[j].trim();
					}
				}
				newLineLyrics += newLyricsWords[j];
			}

			newLyricsLineInfo.setLyricsWords(newLyricsWords);
			newLyricsLineInfo.setLineLyrics(newLineLyrics);

			newLyricsLineInfos.add(newLyricsLineInfo);
		}

		return newLyricsLineInfos;
	}

	/**
	 * 播放的时间补偿值
	 * 
	 * @return
	 */
	public int getPlayOffset() {
		return mDefOffset + mOffset;
	}

	public String getHash() {
		return mHash;
	}

	public void setHash(String mHash) {
		this.mHash = mHash;
	}

	public LyricsInfo getLyricsIfno() {
		return mLyricsIfno;
	}

	public String getLrcFilePath() {
		return mLrcFilePath;
	}

	public void setLrcFilePath(String mLrcFilePath) {
		this.mLrcFilePath = mLrcFilePath;
	}

	public int getOffset() {
		return mOffset;
	}

	// //////////////////////////////////////////////////////////////////////////////

	public void setOffset(int offset) {
		this.mOffset = offset;
	}

	public int getExtraLrcType() {
		return mExtraLrcType;
	}

	public TreeMap<Integer, LyricsLineInfo> getDefLyricsLineTreeMap() {
		return mDefLyricsLineTreeMap;
	}

	public void setDefLyricsLineTreeMap(
			TreeMap<Integer, LyricsLineInfo> mDefLyricsLineTreeMap) {
		this.mDefLyricsLineTreeMap = mDefLyricsLineTreeMap;
	}

	public List<LyricsLineInfo> getTranslateLrcLineInfos() {
		return mTranslateLrcLineInfos;
	}

	public List<LyricsLineInfo> getTransliterationLrcLineInfos() {
		return mTransliterationLrcLineInfos;
	}

}

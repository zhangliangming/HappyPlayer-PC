package com.happy.widget.panel.madetransliterationlrc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.happy.common.BaseData;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.TransliterationLyricsInfo;
import com.happy.manage.LyricsManage;
import com.happy.model.SongInfo;
import com.happy.util.LyricsParserUtil;
import com.happy.util.StringUtils;
import com.happy.widget.scrollbar.BaseScrollBarUI;

/**
 * 制作音译歌词面板
 * 
 * @author zhangliangming
 * 
 */
public class TransliterationLrcPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度和宽度
	 */
	private int mWidth, mHeight;

	/**
	 * 滚动面板
	 */
	private JScrollPane jScrollPane;

	/**
	 * listViewPanel面板
	 */
	private JPanel listViewPanel;

	public TransliterationLrcPanel(int width, int height) {

		this.mWidth = width;
		this.mHeight = height;

		initComponent();

	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		this.setPreferredSize(new Dimension(mWidth, mHeight));
		this.setMaximumSize(new Dimension(mWidth, mHeight));
		this.setMinimumSize(new Dimension(mWidth, mHeight));
		//
		listViewPanel = new JPanel();
		listViewPanel.setOpaque(false);

		jScrollPane = new JScrollPane(listViewPanel);
		jScrollPane.setBorder(null);

		jScrollPane.getVerticalScrollBar().setUI(
				new BaseScrollBarUI(BaseData.listViewAlpha));
		jScrollPane.getVerticalScrollBar().setOpaque(false);
		jScrollPane.getVerticalScrollBar().setUnitIncrement(30);
		// 不显示水平的滚动条
		jScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setOpaque(false);
		jScrollPane.getViewport().setOpaque(false);
		// jScrollPane.getViewport().setBackground(new Color(255, 255, 255, 0));

		//
		this.setLayout(new BorderLayout());
		this.add(jScrollPane, "Center");

		//
		listViewPanel.setLayout(new BoxLayout(listViewPanel, BoxLayout.Y_AXIS));

	}

	/**
	 * 初始化数据
	 * 
	 * @param songInfo
	 */
	public void initData(SongInfo songInfo) {
		if (songInfo != null) {
			LyricsParserUtil lyricsParserUtil = LyricsManage
					.getLyricsParser(songInfo.getSid());
			if (lyricsParserUtil != null
					&& lyricsParserUtil.getLyricsLineTreeMap() != null
					&& lyricsParserUtil.getLyricsLineTreeMap().size() > 0) {

				// 更新ui
				refreshUI(lyricsParserUtil.getLyricsIfno());
			}
		}
	}

	/**
	 * 刷新ui
	 * 
	 * @param lyricsLineInfos
	 */
	private void refreshUI(LyricsInfo lyricsInfo) {

		TreeMap<Integer, LyricsLineInfo> lyricsLineInfos = lyricsInfo
				.getLyricsLineInfoTreeMap();
		TransliterationLyricsInfo transliterationLyricsInfo = lyricsInfo
				.getTransliterationLyricsInfo();

		for (int i = 0; i < lyricsLineInfos.size(); i++) {

			LyricsLineInfo lyricsLineInfo = lyricsLineInfos.get(i);

			LyricsLineInfo transliterationLyricsLineInfo = null;
			if (transliterationLyricsInfo != null
					&& transliterationLyricsInfo
							.getTransliterationLrcLineInfos() != null
					&& transliterationLyricsInfo
							.getTransliterationLrcLineInfos().size() > 0) {
				transliterationLyricsLineInfo = transliterationLyricsInfo
						.getTransliterationLrcLineInfos().get(i);
			}

			TransliterationLrcComPanel translateLrcComPanel = new TransliterationLrcComPanel(
					mWidth, lyricsLineInfo, transliterationLyricsLineInfo);
			listViewPanel.add(translateLrcComPanel);

		}

	}

	/**
	 * 获取翻译歌词
	 * 
	 * @param lyricsInfo
	 * 
	 * @return
	 */
	public TransliterationLyricsInfo getTransliterationLyricsInfo(
			LyricsInfo lyricsInfo) {
		if (listViewPanel.getComponentCount() == 0)
			return null;
		boolean flag = false;
		List<LyricsLineInfo> transliterationLyricsInfos = new ArrayList<LyricsLineInfo>();
		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			TransliterationLrcComPanel translateLrcComPanel = (TransliterationLrcComPanel) listViewPanel
					.getComponent(i);
			JTextField inputLrcJTextField = translateLrcComPanel
					.getInputTranslateTextField();
			String lineLyrics = inputLrcJTextField.getText();
			if (!lineLyrics.equals("") && !flag) {
				flag = true;
			}

			String[] origLyricsWords = lyricsInfo.getLyricsLineInfoTreeMap()
					.get(i).getLyricsWords();
			String[] lyricsWords = new String[origLyricsWords.length];
			for (int k = 0; k < lyricsWords.length; k++) {
				lyricsWords[k] = "";
			}

			if (!StringUtils.isBlank(lineLyrics)) {
				String[] tempLyricsWords = getLyricsWords(lineLyrics);
				for (int j = 0; j < lyricsWords.length
						&& j < tempLyricsWords.length; j++) {
					lyricsWords[j] = tempLyricsWords[j];
				}
			}

			LyricsLineInfo lyricsLineInfo = new LyricsLineInfo();
			lyricsLineInfo.setLyricsWords(lyricsWords);
			transliterationLyricsInfos.add(lyricsLineInfo);

		}
		if (!flag) {
			return null;
		}
		TransliterationLyricsInfo transliterationLyricsInfo = new TransliterationLyricsInfo();
		transliterationLyricsInfo
				.setTransliterationLrcLineInfos(transliterationLyricsInfos);
		return transliterationLyricsInfo;
	}

	/**
	 * 获取歌词字
	 * 
	 * @param inputLineString
	 * @return
	 */
	private String[] getLyricsWords(String inputLineString) {
		List<String> lineLyricsList = new ArrayList<String>();
		String temp = "";
		for (int i = 0; i < inputLineString.length(); i++) {
			char c = inputLineString.charAt(i);
			if (Character.isSpaceChar(c)) {

				lineLyricsList.add(temp.trim());

				temp = "";
			} else {
				temp += String.valueOf(c);
			}
		}
		if (!temp.equals("")) {
			lineLyricsList.add(temp);
		}
		// 歌词分隔
		String[] lyricsWords = lineLyricsList.toArray(new String[lineLyricsList
				.size()]);
		return lyricsWords;
	}
}

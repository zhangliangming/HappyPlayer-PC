package com.happy.widget.panel.madetranslatelrc;

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
import com.happy.lyrics.model.TranslateLrcLineInfo;
import com.happy.lyrics.model.TranslateLyricsInfo;
import com.happy.manage.LyricsManage;
import com.happy.model.SongInfo;
import com.happy.util.LyricsUtil;
import com.happy.widget.scrollbar.BaseScrollBarUI;

/**
 * 制作翻译歌词面板
 * 
 * @author zhangliangming
 * 
 */
public class TranslateLrcPanel extends JPanel {
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

	public TranslateLrcPanel(int width, int height) {

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
			LyricsUtil lyricsParserUtil = LyricsManage
					.getLyricsParser(songInfo.getSid());
			if (lyricsParserUtil != null
					&& lyricsParserUtil.getDefLyricsLineTreeMap() != null
					&& lyricsParserUtil.getDefLyricsLineTreeMap().size() > 0) {

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
		TranslateLyricsInfo translateLyricsInfo = lyricsInfo
				.getTranslateLyricsInfo();

		for (int i = 0; i < lyricsLineInfos.size(); i++) {

			LyricsLineInfo lyricsLineInfo = lyricsLineInfos.get(i);
			TranslateLrcLineInfo translateLrcLineInfo = null;
			if (translateLyricsInfo != null
					&& translateLyricsInfo.getTranslateLrcLineInfos() != null
					&& translateLyricsInfo.getTranslateLrcLineInfos().size() > 0) {
				translateLrcLineInfo = translateLyricsInfo
						.getTranslateLrcLineInfos().get(i);
			}

			TranslateLrcComPanel translateLrcComPanel = new TranslateLrcComPanel(
					mWidth, lyricsLineInfo, translateLrcLineInfo);
			listViewPanel.add(translateLrcComPanel);

		}

	}

	/**
	 * 获取翻译歌词
	 * 
	 * @return
	 */
	public TranslateLyricsInfo getTranslateLyricsInfo() {
		if (listViewPanel.getComponentCount() == 0)
			return null;
		boolean flag = false;
		List<TranslateLrcLineInfo> translateLrcLineInfos = new ArrayList<TranslateLrcLineInfo>();
		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			TranslateLrcComPanel translateLrcComPanel = (TranslateLrcComPanel) listViewPanel
					.getComponent(i);
			JTextField inputLrcJTextField = translateLrcComPanel
					.getInputTranslateTextField();
			String lineLyrics = inputLrcJTextField.getText();
			if (!lineLyrics.equals("") && !flag) {
				flag = true;
			}
			TranslateLrcLineInfo translateLrcLineInfo = new TranslateLrcLineInfo();
			translateLrcLineInfo.setLineLyrics(lineLyrics);
			translateLrcLineInfos.add(translateLrcLineInfo);
		}
		if (!flag) {
			return null;
		}
		TranslateLyricsInfo translateLyricsInfo = new TranslateLyricsInfo();
		translateLyricsInfo.setTranslateLrcLineInfos(translateLrcLineInfos);
		return translateLyricsInfo;
	}

}

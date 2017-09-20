package com.happy.widget.panel.madetranslatelrc;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.happy.lyrics.model.LyricsLineInfo;

/**
 * 翻译歌词内容面板
 * 
 * @author zhangliangming
 * 
 */
public class TranslateLrcComPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 宽度
	 */
	private int mWidth, mHeight = 50;
	private LyricsLineInfo lyricsLineInfo;
	private JTextField inputTranslateTextField;

	public TranslateLrcComPanel(int width, LyricsLineInfo lyricsLineInfo) {
		this.mWidth = width;
		this.lyricsLineInfo = lyricsLineInfo;

		setPreferredSize(new Dimension(mWidth, mHeight));
		setMaximumSize(new Dimension(mWidth, mHeight));
		setMinimumSize(new Dimension(mWidth, mHeight));

		initComponent();
		this.setOpaque(false);
	}

	private void initComponent() {
		this.setLayout(null);
		JLabel songNameJLabel = new JLabel(lyricsLineInfo.getLineLyrics());
		songNameJLabel.setForeground(new Color(129, 129, 129));
		songNameJLabel.setBounds(10, 0, mWidth / 2 - 10, mHeight);

		//
		inputTranslateTextField = new JTextField();
		inputTranslateTextField.setBounds(mWidth / 2, 0, mWidth / 2 - 10,
				mHeight);

		this.add(inputTranslateTextField);
		this.add(songNameJLabel);
	}

	/**
	 * 获取输入框的翻译歌词
	 * 
	 * @return
	 */
	public JTextField getInputTranslateTextField() {
		return inputTranslateTextField;
	}
}

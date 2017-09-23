package com.happy.widget.panel.madetranslatelrc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.model.TranslateLrcLineInfo;

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
	private TranslateLrcLineInfo translateLrcLineInfo;
	private JTextField inputTranslateTextField;
	private JLabel lyricsLabel;

	public TranslateLrcComPanel(int width, LyricsLineInfo lyricsLineInfo,
			TranslateLrcLineInfo translateLrcLineInfo) {
		this.mWidth = width;
		this.lyricsLineInfo = lyricsLineInfo;
		this.translateLrcLineInfo = translateLrcLineInfo;

		setPreferredSize(new Dimension(mWidth, mHeight));
		setMaximumSize(new Dimension(mWidth, mHeight));
		setMinimumSize(new Dimension(mWidth, mHeight));

		initComponent();
		this.setOpaque(false);
	}

	private void initComponent() {
		this.setLayout(null);
		lyricsLabel = new JLabel(lyricsLineInfo.getLineLyrics());
		lyricsLabel.setForeground(new Color(129, 129, 129));
		lyricsLabel.setBounds(10, 0, mWidth / 2 - 10, mHeight);

		//
		inputTranslateTextField = new JTextField();
		if (translateLrcLineInfo != null) {
			inputTranslateTextField.setText(translateLrcLineInfo
					.getLineLyrics());
		}
		inputTranslateTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				lyricsLabel.setForeground(new Color(129, 129, 129));

			}

			@Override
			public void focusGained(FocusEvent e) {
				lyricsLabel.setForeground(new Color(138, 1, 226));

			}
		});
		inputTranslateTextField.setBounds(mWidth / 2, 0, mWidth / 2 - 10,
				mHeight);

		this.add(inputTranslateTextField);
		this.add(lyricsLabel);
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

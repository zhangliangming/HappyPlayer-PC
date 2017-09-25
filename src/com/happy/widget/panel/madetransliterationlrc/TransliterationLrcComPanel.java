package com.happy.widget.panel.madetransliterationlrc;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.happy.lyrics.model.LyricsLineInfo;

/**
 * 音译歌词内容面板
 * 
 * @author zhangliangming
 * 
 */
public class TransliterationLrcComPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 宽度
	 */
	private int mWidth, mHeight = 50;
	private LyricsLineInfo inputLyricsLineInfo;
	private LyricsLineInfo transliterationLyricsInfo;
	private JTextField inputTransliterationTextField;
	private TransliterationLineComPanel lineLyricsComPanel;

	public TransliterationLrcComPanel(int width, LyricsLineInfo lyricsLineInfo,
			LyricsLineInfo transliterationLyricsInfo) {
		this.mWidth = width;

		inputLyricsLineInfo = new LyricsLineInfo();
		inputLyricsLineInfo.copy(inputLyricsLineInfo, lyricsLineInfo);

		this.transliterationLyricsInfo = transliterationLyricsInfo;

		setPreferredSize(new Dimension(mWidth, mHeight));
		setMaximumSize(new Dimension(mWidth, mHeight));
		setMinimumSize(new Dimension(mWidth, mHeight));

		initComponent(lyricsLineInfo);
		this.setOpaque(false);
	}

	private void initComponent(LyricsLineInfo lyricsLineInfo) {
		this.setLayout(null);

		lineLyricsComPanel = new TransliterationLineComPanel(lyricsLineInfo,
				(mWidth / 2 - 10), mHeight);
		lineLyricsComPanel.setBounds(10, 0, mWidth / 2 - 10, mHeight);

		//
		inputTransliterationTextField = new JTextField();
		inputTransliterationTextField.setDocument(new SpacePlainDocument());
		inputTransliterationTextField.getDocument().addDocumentListener(
				new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent e) {
						// System.out.println("removeUpdate");
						onChange();

					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						// System.out.println("insertUpdate");
						onChange();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						// System.out.println("changedUpdate");

					}
				});
		if (transliterationLyricsInfo != null) {

			inputTransliterationTextField.setText(transliterationLyricsInfo
					.getLineLyrics());

		}
		inputTransliterationTextField.setBounds(mWidth / 2, 0, mWidth / 2 - 10,
				mHeight);

		this.add(inputTransliterationTextField);
		this.add(lineLyricsComPanel);
	}

	/**
	 * 输入框内容发生变化
	 * 
	 * @param e
	 */
	protected void onChange() {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				String[] origLyricsWords = inputLyricsLineInfo.getLyricsWords();
				String lineLyrics = inputTransliterationTextField.getText();

				String[] inputLyricsWords = getLyricsWords(lineLyrics);

				// System.out.println("之前：");
				// for (int i = 0; i < inputLyricsWords.length; i++) {
				// System.out.print("[" + inputLyricsWords[i] + "]");
				// }
				// System.out.println();

				lineLyrics = "";
				for (int i = 0; i < origLyricsWords.length
						&& i < inputLyricsWords.length; i++) {
					lineLyrics += inputLyricsWords[i];
				}

				String[] inputSetLyricsWords = getLyricsWords(lineLyrics);

				lineLyricsComPanel.setLrcIndex(inputSetLyricsWords.length - 1);
				TransliterationLrcComPanel.this.repaint();
			}
		});

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

				lineLyricsList.add(temp.trim() + " ");

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

	/**
	 * 获取输入框的翻译歌词
	 * 
	 * @return
	 */
	public JTextField getInputTransliterationTextField() {
		return inputTransliterationTextField;
	}

	/**
	 * 空格监听
	 * 
	 * @author zhangliangming
	 * 
	 */
	public class SpacePlainDocument extends PlainDocument {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str.length() == 1 && Character.isSpaceChar(str.charAt(0))) {
				String[] origLyricsWords = inputLyricsLineInfo.getLyricsWords();
				String lineLyrics = inputTransliterationTextField.getText();
				String[] inputLyricsWords = getLyricsWords(lineLyrics);

				if (origLyricsWords.length < inputLyricsWords.length) {
					return;
				}
			}

			super.insertString(offs, str, a);

		}
	}
}

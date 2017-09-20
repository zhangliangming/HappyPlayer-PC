package com.happy.widget.panel.madelrc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.widget.button.DefButton;

/**
 * 制作歌词列表item面板
 * 
 * @author zhangliangming
 * 
 */
public class LrcListViewItemPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3285370418675829685L;
	/**
	 * 歌词索引
	 */
	private int lyricsLineNum = 0;
	/**
	 * 该行歌词数据
	 */
	private LyricsLineInfo lyricsLineInfo;
	/**
	 * 歌词内容面板
	 */
	private LrcComPanel lrcComPanel;

	private MakeLrcZhiZuoPanel makeLrcZhiZuoPanel;

	/**
	 * 重置按钮
	 */
	private DefButton resetButton;
	/**
	 * 宽度
	 */
	private int width;

	private int height = 60;
	/**
	 * 顶部和底部距离
	 */
	private int paddTopRoButtom = 5;

	private int padding = 25;
	/**
	 * 内容面板
	 */
	private JPanel comPanel;

	public LrcListViewItemPanel(int lyricsLineNum,
			LyricsLineInfo lyricsLineInfo,
			MakeLrcZhiZuoPanel makeLrcZhiZuoPanel, JPanel comPanel, int width) {
		this.lyricsLineNum = lyricsLineNum;
		this.comPanel = comPanel;
		this.lyricsLineInfo = lyricsLineInfo;
		this.makeLrcZhiZuoPanel = makeLrcZhiZuoPanel;
		this.width = width;
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(width, height));
		// 初始化组件
		initComponent();
	}

	private void initComponent() {

		int bWidth = width / 7;
		int bHeight = height - paddTopRoButtom * 2;
		resetButton = new DefButton(bWidth, bHeight);
		resetButton.setText("重置");
		int x = width - bWidth - padding * 2;
		int y = (height - bHeight) / 2;
		resetButton.setBounds(x, y, bWidth, bHeight);
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lrcComPanel.setSelect(false);
				lrcComPanel.reset();
			}
		});

		int lWidth = width - resetButton.getWidth() - padding * 4;
		lrcComPanel = new LrcComPanel(lyricsLineNum, comPanel,
				makeLrcZhiZuoPanel, lyricsLineInfo, lWidth, bHeight);
		lrcComPanel.setBounds(padding, y, lWidth, bHeight);
		if (lyricsLineNum == 0) {
			lrcComPanel.setSelect(true);
			lrcComPanel.setLrcIndex(-1);
		}

		this.setLayout(null);
		this.add(resetButton);
		// this.add(lrcCom);
		this.add(lrcComPanel);
	}

	public LrcComPanel getLrcComPanel() {
		return lrcComPanel;
	}

}

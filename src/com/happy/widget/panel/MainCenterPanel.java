package com.happy.widget.panel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.happy.ui.MainFrame;
import com.happy.widget.button.ImageButton;
import com.happy.widget.dialog.DesLrcDialog;

/**
 * 主界面中部面板
 * 
 * @author zhangliangming
 * 
 */
public class MainCenterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度和宽度
	 */
	private int mWidth, mHeight;
	/**
	 * 显示和隐藏窗口
	 */
	private ImageButton showButton, hideButton;

	private MainFrame mainFrame;

	private SongListPanel songListPanel;

	private LyricsPanel lyricsPanel;

	private DesLrcDialog desktopLrcDialog;

	private MainOperatePanel mainOperatePanel;

	public MainCenterPanel(MainOperatePanel mainOperatePanel,
			DesLrcDialog desktopLrcDialog, MainFrame mainFrame, int width,
			int height) {
		this.mainOperatePanel = mainOperatePanel;
		this.desktopLrcDialog = desktopLrcDialog;
		this.mainFrame = mainFrame;
		this.mWidth = width;
		this.mHeight = height;

		initComponent();

		this.setOpaque(false);
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// 歌曲列表面板
		final int slPanelWidth = mWidth / 5 * 2 - 80;
		int slPanelHeight = mHeight;
		songListPanel = new SongListPanel(mainFrame, this, slPanelWidth,
				slPanelHeight);

		//
		int bWidth = 15;
		int bHeight = 80;
		// 隐藏按钮
		hideButton = new ImageButton(bWidth, bHeight, "hide_def.png",
				"hide_rollover.png", "hide_pressed.png");
		// 设置按钮大小
		hideButton.setPreferredSize(new Dimension(bWidth, bHeight));
		hideButton.setMaximumSize(new Dimension(bWidth, bHeight));
		hideButton.setMinimumSize(new Dimension(bWidth, bHeight));

		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showButton.setVisible(true);
				songListPanel.setVisible(false);
				hideButton.setVisible(false);
				//
				lyricsPanel.getManyLineLyricsView().setWidth(mWidth);
			}
		});

		// 显示按钮
		showButton = new ImageButton(bWidth, bHeight, "show_def.png",
				"show_rollover.png", "show_pressed.png");
		// 设置按钮大小
		showButton.setPreferredSize(new Dimension(bWidth, bHeight));
		showButton.setMaximumSize(new Dimension(bWidth, bHeight));
		showButton.setMinimumSize(new Dimension(bWidth, bHeight));
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideButton.setVisible(true);
				showButton.setVisible(false);
				songListPanel.setVisible(true);
				//
				lyricsPanel.getManyLineLyricsView().setWidth(
						mWidth - slPanelWidth);
			}
		});
		showButton.setVisible(false);

		// 歌词面板
		int lWidth = mWidth - slPanelWidth;
		int lHeight = mHeight;
		lyricsPanel = new LyricsPanel(mainOperatePanel, desktopLrcDialog,
				lWidth, lHeight);
		this.add(songListPanel);
		this.add(hideButton);
		this.add(showButton);
		this.add(lyricsPanel);
	}

	public LyricsPanel getLyricsPanel() {
		return lyricsPanel;
	}

	public SongListPanel getSongListPanel() {
		return songListPanel;
	}
}

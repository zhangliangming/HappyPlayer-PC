package com.happy.widget.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.happy.common.Constants;
import com.happy.ui.MainFrame;
import com.happy.widget.button.ImageButton;

/**
 * 主界面的标题菜单面板
 * 
 * @author zhangliangming
 * 
 */
public class MainMenuPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度和宽度
	 */
	private int mWidth, mHeight;
	/**
	 * 主界面窗口
	 */
	private MainFrame mMainFrame;

	public MainMenuPanel(int width, int height, MainFrame mainFrame) {
		this.mWidth = width;
		this.mHeight = height;
		this.mMainFrame = mainFrame;

		initComponent();

		// this.setBackground(Color.red);
		this.setOpaque(false);// 设置透明
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		this.setLayout(null);
		int buttonSize = mHeight / 5 * 3;
		int buttonY = (mHeight - buttonSize) / 2;
		int padding = 15;
		// 关闭按钮
		ImageButton closeButton = new ImageButton(buttonSize, buttonSize,
				"close_def.png", "close_rollover.png", "close_pressed.png");
		closeButton.setBounds(mWidth - buttonSize - padding, buttonY,
				buttonSize, buttonSize);
		closeButton.setToolTipText("关闭");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mMainFrame.exitPlayer();
			}
		});
		// 最小化按钮
		ImageButton minButton = new ImageButton(buttonSize, buttonSize,
				"min_def.png", "min_rollover.png", "min_pressed.png");
		minButton.setBounds(closeButton.getX() - buttonSize - padding, buttonY,
				buttonSize, buttonSize);
		minButton.setToolTipText("最小化");
		minButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mMainFrame.setExtendedState(Frame.ICONIFIED);
			}
		});
		// 标题
		int mTitleLaberWidth = Constants.APPTIPTITLE.length() * 50;
		int mTitleLaberHeight = mHeight / 3;
		int mTitleLaberX = padding * 2;
		int mTitleLaberY = (mHeight - mTitleLaberHeight) / 2;
		JLabel titleLabel = new JLabel();
		titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, mTitleLaberHeight));
		titleLabel.setText(Constants.APPTITLEANDYEAR);
		titleLabel.setForeground(Color.white);
		titleLabel.setBounds(mTitleLaberX, mTitleLaberY, mTitleLaberWidth,
				mTitleLaberHeight);

		//
		this.add(titleLabel);
		this.add(minButton);
		this.add(closeButton);
	}

}

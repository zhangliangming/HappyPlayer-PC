package com.happy.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.event.PanelMoveFrame;
import com.happy.logger.LoggerManage;
import com.happy.manage.MadeLrcDialogManage;
import com.happy.manage.MadeTranslateLrcDialogManage;
import com.happy.manage.MadeTransliterationLrcDialogManage;
import com.happy.manage.MediaManage;
import com.happy.model.MessageIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.DataUtil;
import com.happy.widget.dialog.DesLrcDialog;
import com.happy.widget.panel.MainCenterPanel;
import com.happy.widget.panel.MainMenuPanel;
import com.happy.widget.panel.MainOperatePanel;

/**
 * 主界面
 * 
 * @author zhangliangming
 * 
 */
public class MainFrame extends JFrame implements Observer {
	private static LoggerManage logger = LoggerManage.getZhangLogger();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 背景图片
	 */
	private JLabel bgJLabel;
	/**
	 * 窗口宽度
	 */
	private int mainFrameWidth;
	/**
	 * 窗口高度
	 */
	private int mainFrameHeight;

	/**
	 * 
	 */
	private MainOperatePanel mainOperatePanel;

	/**
	 * 桌面歌词窗口
	 */
	private DesLrcDialog desktopLrcDialog;

	public MainFrame() {
		init();// 初始化
		initDesktopLrcDialog();
		initComponent();// 初始化控件
		openDesLrcDialog();
		initSkin();// 初始化皮肤
		//
		setVisible(true);
		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 定义窗口关闭事件
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitPlayer();
			}
		});

		this.setTitle(Constants.APPTITLE);
		this.setUndecorated(true);
		// 状态栏图标
		String iconFilePath = Constants.PATH_ICON + File.separator
				+ BaseData.iconFileName;
		this.setIconImage(new ImageIcon(iconFilePath).getImage());

		// 设置基本的窗口数据
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		// 默认窗口宽度
		mainFrameWidth = screenDimension.width / 5 * 3 + 100;
		// 默认窗口高度
		mainFrameHeight = screenDimension.height / 4 * 3;

		this.setSize(mainFrameWidth, mainFrameHeight);
		this.setLocationRelativeTo(null);
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		this.getContentPane().setLayout(null);

		// 主界面标题栏菜单面板
		int mmPanelWidth = mainFrameWidth;
		int mmPanelHeight = 55;
		int mmPanelX = 0;
		int mmPanelY = 0;
		MainMenuPanel mainMenuPanel = new MainMenuPanel(mmPanelWidth,
				mmPanelHeight, this);
		new PanelMoveFrame(mainMenuPanel, this);
		mainMenuPanel
				.setBounds(mmPanelX, mmPanelY, mmPanelWidth, mmPanelHeight);// 位置
		// 底部操作面板
		int moPanelWidth = mainFrameWidth;
		int moPanelHeight = 75;
		int moPanelX = 0;
		int moPanelY = mainFrameHeight - moPanelHeight;
		mainOperatePanel = new MainOperatePanel(desktopLrcDialog, moPanelWidth,
				moPanelHeight);
		mainOperatePanel.setBounds(moPanelX, moPanelY, moPanelWidth,
				moPanelHeight);
		new PanelMoveFrame(mainOperatePanel, this);

		// 界面中部面板
		int mcPanelWidth = mainFrameWidth;
		int mcPanelHeight = mainFrameHeight - mainMenuPanel.getHeight()
				- mainOperatePanel.getHeight();
		int mcPanelX = 0;
		int mcPanelY = mainMenuPanel.getHeight() - 0;
		MainCenterPanel mainCenterPanel = new MainCenterPanel(desktopLrcDialog,
				this, mcPanelWidth, mcPanelHeight);
		mainCenterPanel.setBounds(mcPanelX, mcPanelY, mcPanelWidth,
				mcPanelHeight);

		//
		this.getContentPane().add(mainCenterPanel);
		this.getContentPane().add(mainOperatePanel);
		this.getContentPane().add(mainMenuPanel);
	}

	/**
	 * 初始化皮肤
	 */
	private void initSkin() {

		bgJLabel = new JLabel(getSkinImageIcon());// 把背景图片显示在一个标签里面
		// 把标签的大小位置设置为图片刚好填充整个面板
		bgJLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.getContentPane().add(bgJLabel);
	}

	/**
	 * 获取皮肤图片
	 * 
	 * @return
	 */
	private ImageIcon getSkinImageIcon() {
		String backgroundFilePath = Constants.PATH_SKIN + File.separator
				+ BaseData.bGroundFileName;
		ImageIcon background = new ImageIcon(backgroundFilePath);// 背景图片
		background.setImage(background.getImage().getScaledInstance(
				this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH));
		return background;
	}

	public MainOperatePanel getMainOperatePanel() {
		return mainOperatePanel;
	}

	/**
	 * 退出播放器
	 */
	public void exitPlayer() {
		this.setVisible(false);
		desktopLrcDialog.setVisible(false);
		MediaManage.getMediaManage().stopToPlay();
		logger.info("准备退出播放器");
		DataUtil.saveData();
		logger.info("退出成功");
		System.exit(0);
	}

	@Override
	public void update(Observable o, Object data) {
		updateUI(data);
	}

	private void updateUI(Object data) {

		if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(MessageIntent.FRAME_NORMAL)) {

				setAlwaysOnTop(true);
				setExtendedState(Frame.NORMAL);
				setAlwaysOnTop(false);
				setVisible(true);

			} else if (messageIntent.getAction().equals(
					MessageIntent.OPENORCLOSEDESLRC)) {
				openDesLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.CLOSEDESLRC)) {
				desktopLrcDialog.setVisible(false);
				openDesLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.OPEN_MADELRCDIALOG)) {
				openMakeLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.CLOSE_MADELRCDIALOG)) {
				hideMadeLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.OPEN_MADETRANSLATELRCDIALOG)) {
				openMakeTranslateLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.CLOSE_MADETRANSLATELRCDIALOG)) {
				hideMadeTranslateLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.OPEN_MADETRANSLITERATIONLRCDIALOG)) {
				openMakeTransliterationLrcDialog();
			} else if (messageIntent.getAction().equals(
					MessageIntent.CLOSE_MADETRANSLITERATIONLRCDIALOG)) {
				hideMadeTransliterationLrcDialog();
			}
		} else if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.INITMUSIC) {
				SongInfo mSongInfo = songMessage.getSongInfo();
				if (mSongInfo != null) {
					setTitle(mSongInfo.getDisplayName());
				}

			}
		}
	}

	/**
	 * 关闭音译歌词
	 */
	private void hideMadeTransliterationLrcDialog() {
		MadeTransliterationLrcDialogManage.hideMadeTransliterationLrcDialog();

	}

	/**
	 * 打开音译歌词
	 */
	private void openMakeTransliterationLrcDialog() {

		this.setExtendedState(Frame.ICONIFIED);

		MadeTransliterationLrcDialogManage.initMadeTransliterationLrcDialog();

		int x = this.getX()
				+ (this.getWidth() - MadeTransliterationLrcDialogManage
						.getWidth()) / 2;
		int y = this.getY()
				+ (this.getHeight() - MadeTransliterationLrcDialogManage
						.getHeight()) / 2;

		MadeTransliterationLrcDialogManage.showMadeTransliterationLrcDialog(x,
				y);

	}

	/**
	 * 关闭翻译歌词窗口
	 */
	private void hideMadeTranslateLrcDialog() {
		MadeTranslateLrcDialogManage.hideMadeTranslateLrcDialog();

	}

	/**
	 * 打开翻译歌词窗口
	 */
	private void openMakeTranslateLrcDialog() {
		this.setExtendedState(Frame.ICONIFIED);

		MadeTranslateLrcDialogManage.initMadeTranslateLrcDialog();

		int x = this.getX()
				+ (this.getWidth() - MadeTranslateLrcDialogManage.getWidth())
				/ 2;
		int y = this.getY()
				+ (this.getHeight() - MadeTranslateLrcDialogManage.getHeight())
				/ 2;

		MadeTranslateLrcDialogManage.showMadeTranslateLrcDialog(x, y);
	}

	/**
	 * 隐藏制作歌词窗口
	 */
	private void hideMadeLrcDialog() {
		MadeLrcDialogManage.hideMadeLrcDialog();
	}

	/**
	 * 打开制作歌词窗口
	 */
	private void openMakeLrcDialog() {

		this.setExtendedState(Frame.ICONIFIED);

		MadeLrcDialogManage.initMadeLrcDialog();

		int x = this.getX()
				+ (this.getWidth() - MadeLrcDialogManage.getWidth()) / 2;
		int y = this.getY()
				+ (this.getHeight() - MadeLrcDialogManage.getHeight()) / 2;

		MadeLrcDialogManage.showMadeLrcDialog(x, y);
	}

	/**
	 * 显示桌面窗口
	 */
	private void openDesLrcDialog() {

		if (BaseData.showDesktopLyrics) {

			// 获取屏幕边界
			Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
					this.getGraphicsConfiguration());
			// 取得底部边界高度，即任务栏高度
			int taskHeight = screenInsets.bottom;

			int y = desktopLrcDialog.getmY() - taskHeight
					- desktopLrcDialog.getmHeight();
			desktopLrcDialog.setLocation(0, y);
			desktopLrcDialog.setVisible(true);
		} else {
			desktopLrcDialog.setVisible(false);
		}
	}

	/**
	 * 桌面歌词窗口
	 */
	private void initDesktopLrcDialog() {
		desktopLrcDialog = new DesLrcDialog();
	}
}

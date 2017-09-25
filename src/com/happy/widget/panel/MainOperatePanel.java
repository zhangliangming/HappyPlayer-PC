package com.happy.widget.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.manage.MediaManage;
import com.happy.manage.SongProgressTipManage;
import com.happy.model.MessageIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.MainFrame;
import com.happy.widget.button.ImageButton;
import com.happy.widget.dialog.DesLrcDialog;
import com.happy.widget.dialog.ProgressTipDialog;
import com.happy.widget.panel.lrc.ManyLineLyricsView;
import com.happy.widget.panel.lrc.ManyLineLyricsView.ExtraLyricsListener;
import com.happy.widget.slider.BaseSlider;

/**
 * 主界面底部面板
 * 
 * @author zhangliangming
 * 
 */
public class MainOperatePanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度和宽度
	 */
	private int mWidth, mHeight;
	/**
	 * 播放按钮
	 */
	private ImageButton playButton;
	/**
	 * 暂停按钮
	 */
	private ImageButton pauseButton;
	/**
	 * 上一首
	 */
	private ImageButton preButton;
	/**
	 * 下一首
	 */
	private ImageButton nextButton;
	/**
	 * 间隔
	 */
	private int padding = 15;

	/**
	 * 歌名Label
	 * 
	 */
	private JLabel songNameLabel;

	// 翻译歌词
	private ImageButton mNoTranslateBtn;
	private ImageButton mHideTranslateBtn;
	private ImageButton mShowTranslateBtn;
	// 音译歌词
	private ImageButton mNoTransliterationBtn;
	private ImageButton mHideTransliterationBtn;
	private ImageButton mShowTransliterationBtn;

	//
	private ExtraLyricsListener mExtraLyricsListener;

	/**
	 * 显示桌面歌词按钮
	 */
	private ImageButton showDesktopLyricsButton;
	/**
	 * 不显示桌面歌词按钮
	 */
	private ImageButton unShowDesktopLyricsButton;

	/**
	 * 制作歌词按钮
	 */
	private ImageButton makeLyricsButton;
	/**
	 * 音量滑块
	 */
	private BaseSlider volumeSlider;

	/**
	 * 音量
	 */
	private ImageButton volumeButton;

	/**
	 * 随机播放
	 */
	private ImageButton randomButton;
	/**
	 * 顺序播放
	 */
	private ImageButton sequenceButton;
	/**
	 * 单曲循环
	 */
	private ImageButton singleRepeatButton;

	/**
	 * 列表循环播放
	 */
	private ImageButton listRepeatButton;

	/**
	 * 单曲播放
	 */
	private ImageButton singleButton;

	/**
	 * 播放模式
	 */
	private int playModel = BaseData.playModel;

	/**
	 * 歌曲进度
	 * 
	 */
	private JLabel songProgressLabel;

	/**
	 * 歌曲进度条
	 */
	private BaseSlider songSlider;

	/**
	 * 判断其是否是正在拖动
	 */
	private boolean isStartTrackingTouch = false;

	private DesLrcDialog desLrcDialog;
	private MainFrame mainFrame;

	public MainOperatePanel(DesLrcDialog desktopLrcDialog, int width,
			int height, MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.desLrcDialog = desktopLrcDialog;
		this.mWidth = width;
		this.mHeight = height;

		initComponent();

		// this.setBackground(Color.red);
		this.setOpaque(false);// 设置透明

		ObserverManage.getObserver().addObserver(this);

		//
		mExtraLyricsListener = new ExtraLyricsListener() {

			@Override
			public void noExtraLrcCallback() {
				// 翻译歌词
				mNoTranslateBtn.setVisible(true);
				mHideTranslateBtn.setVisible(false);
				mShowTranslateBtn.setVisible(false);
				// 音译歌词
				mNoTransliterationBtn.setVisible(true);
				mHideTransliterationBtn.setVisible(false);
				mShowTransliterationBtn.setVisible(false);
			}

			@Override
			public void hasTransliterationLrcCallback() {
				// 翻译歌词
				mNoTranslateBtn.setVisible(true);
				mHideTranslateBtn.setVisible(false);
				mShowTranslateBtn.setVisible(false);
				// 音译歌词
				mNoTransliterationBtn.setVisible(false);
				mHideTransliterationBtn.setVisible(false);
				mShowTransliterationBtn.setVisible(true);

			}

			@Override
			public void hasTranslateLrcCallback() {
				// 翻译歌词
				mNoTranslateBtn.setVisible(false);
				mHideTranslateBtn.setVisible(false);
				mShowTranslateBtn.setVisible(true);
				// 音译歌词
				mNoTransliterationBtn.setVisible(true);
				mHideTransliterationBtn.setVisible(false);
				mShowTransliterationBtn.setVisible(false);

			}

			@Override
			public void hasTranslateAndTransliterationLrcCallback() {
				// 翻译歌词
				mNoTranslateBtn.setVisible(false);
				mHideTranslateBtn.setVisible(false);
				mShowTranslateBtn.setVisible(true);
				// 音译歌词
				mNoTransliterationBtn.setVisible(false);
				mHideTransliterationBtn.setVisible(false);
				mShowTransliterationBtn.setVisible(true);

			}
		};
		mExtraLyricsListener.noExtraLrcCallback();

	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		this.setLayout(null);
		int buttonSize = mHeight / 3 * 2;
		// 上一首
		int pbX = padding;
		int pbY = (mHeight - buttonSize) / 2;
		preButton = new ImageButton(buttonSize, buttonSize, "pre_def.png",
				"pre_rollover.png", "pre_pressed.png");
		preButton.setBounds(pbX, pbY, buttonSize, buttonSize);
		preButton.setToolTipText("上一首");
		preButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PREMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		// 播放
		int plbX = padding + preButton.getX() + preButton.getWidth();
		int plbY = (mHeight - buttonSize) / 2;
		playButton = new ImageButton(buttonSize, buttonSize, "play_def.png",
				"play_rollover.png", "play_pressed.png");
		playButton.setBounds(plbX, plbY, buttonSize, buttonSize);
		playButton.setToolTipText("播放");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		// 暂停
		int pabX = padding + preButton.getX() + preButton.getWidth();
		int pabY = (mHeight - buttonSize) / 2;
		pauseButton = new ImageButton(buttonSize, buttonSize, "pause_def.png",
				"pause_rollover.png", "pause_pressed.png");
		pauseButton.setBounds(pabX, pabY, buttonSize, buttonSize);
		pauseButton.setToolTipText("暂停");
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});
		pauseButton.setVisible(false);

		// 下一首
		int nbX = padding + pauseButton.getX() + pauseButton.getWidth();
		int nbY = (mHeight - buttonSize) / 2;
		nextButton = new ImageButton(buttonSize, buttonSize, "next_def.png",
				"next_rollover.png", "next_pressed.png");
		nextButton.setBounds(nbX, nbY, buttonSize, buttonSize);
		nextButton.setToolTipText("下一首");
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.NEXTMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		// 制作歌词
		int mlbWidth = mHeight;
		int mlbHeight = mHeight / 2;
		int mlbX = mWidth - padding - mlbWidth;
		int mlbY = (mHeight - mlbHeight) / 2;
		makeLyricsButton = new ImageButton(mlbWidth, mlbHeight,
				"desMakeLrc_def.png", "desMakeLrc_rollover.png",
				"desMakeLrc_pressed.png");
		makeLyricsButton.setBounds(mlbX, mlbY, mlbWidth, mlbHeight);
		makeLyricsButton.setToolTipText("制作歌词");
		makeLyricsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (BaseData.playInfoID.equals("-1")) {
					JOptionPane.showMessageDialog(null, "请选择歌曲", "提示",
							JOptionPane.ERROR_MESSAGE);
				} else {
					MessageIntent messageIntent = new MessageIntent();
					messageIntent.setAction(MessageIntent.OPEN_MADELRCDIALOG);
					ObserverManage.getObserver().setMessage(messageIntent);
				}
			}
		});

		//
		int usdlbWidth = mHeight / 2;
		int usdlbHeight = mHeight / 2;
		int usdlbX = mWidth - padding - usdlbWidth - mlbWidth;
		int usdlbY = (mHeight - usdlbHeight) / 2;
		unShowDesktopLyricsButton = new ImageButton(usdlbWidth, usdlbHeight,
				"undeslrc_def.png", "undeslrc_rollover.png",
				"undeslrc_pressed.png");
		unShowDesktopLyricsButton.setBounds(usdlbX, usdlbY, usdlbWidth,
				usdlbHeight);
		unShowDesktopLyricsButton.setToolTipText("打开桌面歌词");
		unShowDesktopLyricsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BaseData.showDesktopLyrics = true;
				BaseData.desLrcIsLock = false;
				initDesktopLrc();

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.OPENORCLOSEDESLRC);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		});

		//
		showDesktopLyricsButton = new ImageButton(usdlbWidth, usdlbHeight,
				"deslrc_def.png", "deslrc_rollover.png", "deslrc_pressed.png");
		showDesktopLyricsButton.setBounds(usdlbX, usdlbY, usdlbWidth,
				usdlbHeight);
		showDesktopLyricsButton.setToolTipText("关闭桌面歌词");
		showDesktopLyricsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BaseData.showDesktopLyrics = false;
				initDesktopLrc();

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.OPENORCLOSEDESLRC);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		});
		initDesktopLrc();

		int extraSize = (mHeight - padding) / 2;
		int extraY = (mHeight - extraSize) / 2;

		// 无音译歌词
		mNoTransliterationBtn = new ImageButton(extraSize, extraSize,
				"bqo.png", "bqo.png", "bqo.png");
		mNoTransliterationBtn.setBounds(unShowDesktopLyricsButton.getX()
				- unShowDesktopLyricsButton.getWidth() + padding / 4, extraY,
				extraSize, extraSize);
		mNoTransliterationBtn.setToolTipText("无音译歌词");

		// 隐藏音译歌词
		mHideTransliterationBtn = new ImageButton(extraSize, extraSize,
				"bqn.png", "bqn.png", "bqn.png");
		mHideTransliterationBtn.setBounds(unShowDesktopLyricsButton.getX()
				- unShowDesktopLyricsButton.getWidth() + padding / 4, extraY,
				extraSize, extraSize);
		mHideTransliterationBtn.setToolTipText("隐藏音译歌词");
		mHideTransliterationBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// 音译歌词
				mHideTransliterationBtn.setVisible(false);
				mShowTransliterationBtn.setVisible(true);

				// 设置多行歌词
				mainFrame.getMainCenterPanel().getLyricsPanel()
						.getManyLineLyricsView()
						.setExtraLrcStatus(ManyLineLyricsView.NOSHOWEXTRALRC);

			}
		});

		// 显示音译歌词
		mShowTransliterationBtn = new ImageButton(extraSize, extraSize,
				"bqo1.png", "bqo1.png", "bqo1.png");
		mShowTransliterationBtn.setBounds(unShowDesktopLyricsButton.getX()
				- unShowDesktopLyricsButton.getWidth() + padding / 4, extraY,
				extraSize, extraSize);
		mShowTransliterationBtn.setToolTipText("显示音译歌词");
		mShowTransliterationBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// 音译歌词
				mHideTransliterationBtn.setVisible(true);
				mShowTransliterationBtn.setVisible(false);

				// 翻译歌词
				if (!mNoTranslateBtn.isVisible()) {
					mHideTranslateBtn.setVisible(false);
					mShowTranslateBtn.setVisible(true);
				}

				// 设置多行歌词
				mainFrame
						.getMainCenterPanel()
						.getLyricsPanel()
						.getManyLineLyricsView()
						.setExtraLrcStatus(
								ManyLineLyricsView.SHOWTRANSLITERATIONLRC);

			}
		});

		// 翻译歌词
		// 无翻译歌词
		mNoTranslateBtn = new ImageButton(extraSize, extraSize, "bqm.png",
				"bqm.png", "bqm.png");
		mNoTranslateBtn.setBounds(mNoTransliterationBtn.getX()
				- mNoTransliterationBtn.getWidth() - padding / 2, extraY,
				extraSize, extraSize);
		mNoTranslateBtn.setToolTipText("无翻译歌词");

		// 隐藏翻译歌词
		mHideTranslateBtn = new ImageButton(extraSize, extraSize, "bql.png",
				"bql.png", "bql.png");
		mHideTranslateBtn.setBounds(mNoTransliterationBtn.getX()
				- mNoTransliterationBtn.getWidth() - padding / 2, extraY,
				extraSize, extraSize);
		mHideTranslateBtn.setToolTipText("隐藏翻译歌词");
		mHideTranslateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mHideTranslateBtn.setVisible(false);
				mShowTranslateBtn.setVisible(true);

				mainFrame.getMainCenterPanel().getLyricsPanel()
						.getManyLineLyricsView()
						.setExtraLrcStatus(ManyLineLyricsView.NOSHOWEXTRALRC);
			}
		});

		// 显示翻译歌词
		mShowTranslateBtn = new ImageButton(extraSize, extraSize, "bqm1.png",
				"bqm1.png", "bqm1.png");
		mShowTranslateBtn.setBounds(mNoTransliterationBtn.getX()
				- mNoTransliterationBtn.getWidth() - padding / 2, extraY,
				extraSize, extraSize);
		mShowTranslateBtn.setToolTipText("显示翻译歌词");
		mShowTranslateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 翻译歌词
				mHideTranslateBtn.setVisible(true);
				mShowTranslateBtn.setVisible(false);
				// 音译歌词
				if (!mNoTransliterationBtn.isVisible()) {
					mHideTransliterationBtn.setVisible(false);
					mShowTransliterationBtn.setVisible(true);
				}
				mainFrame.getMainCenterPanel().getLyricsPanel()
						.getManyLineLyricsView()
						.setExtraLrcStatus(ManyLineLyricsView.SHOWTRANSLATELRC);

			}
		});

		//
		volumeSlider = new BaseSlider();
		volumeSlider.setOpaque(false); // slider的背景透明
		volumeSlider.setFocusable(false);
		volumeSlider.setValue(BaseData.volumeSize);
		volumeSlider.setMaximum(100);
		int vsW = 90;
		int vsH = 20;
		int vsx = mNoTranslateBtn.getX() - padding - vsW;
		int vsy = (mHeight - vsH) / 2 + 5;

		volumeSlider.setBounds(vsx, vsy, vsW, vsH);

		volumeSlider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				final int x = e.getX();
				// // /* 获取鼠标的位置 */
				volumeSlider.setValue(volumeSlider.getMaximum() * x
						/ volumeSlider.getWidth());
				//
				// //设置ui
				// Constants.volumeSize = volumeSlider.getValue();
				// initVolumeButtonUI(volumeButton.getWidth() / 3 * 2);

			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SongProgressTipManage.hideSongProgressTipDialog();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});
		volumeSlider.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {
				int progress = volumeSlider.getMaximum() * e.getX()
						/ volumeSlider.getWidth();

				String tip = progress + "";

				ProgressTipDialog songProgressTipDialog = SongProgressTipManage
						.getSongInfoTipManage().getSongProgressTipDialog();

				songProgressTipDialog.getTipPanel().setTipString(tip);

				songProgressTipDialog.setSize(new Dimension(
						songProgressTipDialog.getTipPanel().getTextWidth() + 8,
						BaseData.appFontSize + 8));
				int x = e.getXOnScreen();
				int y = volumeSlider.getLocationOnScreen().y
						+ volumeSlider.getHeight() + 10;

				songProgressTipDialog.setLocation(x, y);
				SongProgressTipManage.showSongProgressTipDialog();
			}

			public void mouseDragged(MouseEvent e) {

			}
		});

		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				BaseData.volumeSize = volumeSlider.getValue();
				initVolumeButtonUI(volumeButton.getWidth() / 3 * 2);

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.PLAYERVOLUME);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		});
		//
		int vbWidth = mHeight / 2;
		int vbHeight = mHeight / 2;
		int vbX = volumeSlider.getX() - vbWidth - padding / 2;
		int vbY = (mHeight - vbHeight) / 2;
		volumeButton = new ImageButton(vbWidth, vbHeight);
		volumeButton.setBounds(vbX, vbY, vbWidth, vbHeight);
		volumeButton.setToolTipText("音量");
		volumeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (BaseData.volumeSize != 0) {
					BaseData.volumeSize = 0;
				} else {
					BaseData.volumeSize = 50;
				}
				volumeSlider.setValue(BaseData.volumeSize);
				initVolumeButtonUI(volumeButton.getWidth() / 3 * 2);
			}
		});
		initVolumeButtonUI(vbWidth / 3 * 2);

		//
		int pmWidth = mHeight / 2;
		int pmHeight = mHeight / 2;
		int pmX = volumeButton.getX() - pmWidth - padding;
		int pmY = (mHeight - pmHeight) / 2 + 5;

		// 单曲播放
		singleButton = new ImageButton(pmWidth, pmHeight, "01_1.png",
				"02_1.png", "03_1.png");
		singleButton.setBounds(pmX, pmY, pmWidth, pmHeight);
		singleButton.setToolTipText("单曲播放");
		singleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
				playModel = 4;
				BaseData.playModel = playModel;
				initPlayModel();
			}
		});

		// 单曲循环
		singleRepeatButton = new ImageButton(pmWidth, pmHeight, "01_2.png",
				"02_2.png", "03_2.png");
		singleRepeatButton.setBounds(pmX, pmY, pmWidth, pmHeight);
		singleRepeatButton.setToolTipText("单曲循环");
		singleRepeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
				playModel = 0;
				BaseData.playModel = playModel;
				initPlayModel();
			}
		});

		// 顺序播放
		sequenceButton = new ImageButton(pmWidth, pmHeight, "01_3.png",
				"02_3.png", "03_3.png");
		sequenceButton.setBounds(pmX, pmY, pmWidth, pmHeight);
		sequenceButton.setToolTipText("顺序播放");
		sequenceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
				playModel = 1;
				BaseData.playModel = playModel;
				initPlayModel();
			}
		});

		// 列表循环
		listRepeatButton = new ImageButton(pmWidth, pmHeight, "01_4.png",
				"02_4.png", "03_4.png");
		listRepeatButton.setBounds(pmX, pmY, pmWidth, pmHeight);
		listRepeatButton.setToolTipText("列表循环");
		listRepeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
				playModel = 3;
				BaseData.playModel = playModel;
				initPlayModel();
			}
		});

		// 随机播放
		randomButton = new ImageButton(pmWidth, pmHeight, "01_5.png",
				"02_5.png", "03_5.png");
		randomButton.setBounds(pmX, pmY, pmWidth, pmHeight);
		randomButton.setToolTipText("随机播放");
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
				playModel = 2;
				BaseData.playModel = playModel;
				initPlayModel();
			}
		});
		// 初始化播放模式
		initPlayModel();

		//
		int snlWidth = 20 * 20;
		int snlHeight = mHeight / 3;
		int snlX = nextButton.getX() + nextButton.getWidth() + padding * 3;
		int snlY = 10;
		songNameLabel = new JLabel();
		songNameLabel.setText(Constants.APPTITLE);
		songNameLabel.setForeground(Color.white);
		songNameLabel.setBounds(snlX, snlY, snlWidth, snlHeight);

		//
		int sslWidth = 12 * 10;
		int sslHeight = mHeight / 3;
		int sslX = randomButton.getX() - sslWidth - padding;
		int sslY = 10;
		songProgressLabel = new JLabel();
		songProgressLabel.setForeground(Color.white);
		songProgressLabel.setBounds(sslX, sslY, sslWidth, sslHeight);
		songProgressLabel.setText("00:00/00:00");

		//
		int ssHeight = mHeight / 2;
		int ssX = songNameLabel.getX();
		int ssY = songProgressLabel.getHeight() + 12;
		int ssWidth = (randomButton.getX() - padding * 2)
				- songNameLabel.getX();
		songSlider = new BaseSlider();
		songSlider.setOpaque(false); // slider的背景透明
		songSlider.setFocusable(false);
		songSlider.setValue(0);
		songSlider.setMaximum(0);

		songSlider.setBounds(ssX, ssY, ssWidth, ssHeight);

		songSlider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				isStartTrackingTouch = true;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				final int x = e.getX();

				// // /* 获取鼠标的位置 */
				songSlider.setValue(songSlider.getMaximum() * x
						/ songSlider.getWidth());
				new Thread() {

					@Override
					public void run() {

						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.SEEKTOMUSIC);
						songMessage.setProgress(songSlider.getValue());
						ObserverManage.getObserver().setMessage(songMessage);

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						isStartTrackingTouch = false;

					}

				}.start();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SongProgressTipManage.hideSongProgressTipDialog();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		songSlider.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent e) {
				int progress = songSlider.getMaximum() * e.getX()
						/ songSlider.getWidth();

				String tip = "歌曲进度";

				if (songSlider.getMaximum() != 0)
					tip = TimeUtils.parseString(progress);

				ProgressTipDialog songProgressTipDialog = SongProgressTipManage
						.getSongInfoTipManage().getSongProgressTipDialog();

				songProgressTipDialog.getTipPanel().setTipString(tip);

				int dWidth = songProgressTipDialog.getTipPanel().getTextWidth() + 8;
				songProgressTipDialog.setSize(new Dimension(dWidth,
						BaseData.appFontSize + 8));
				int x = e.getXOnScreen();
				int y = songSlider.getLocationOnScreen().y
						+ songSlider.getHeight() + 10;

				songProgressTipDialog.setLocation(x, y);
				SongProgressTipManage.showSongProgressTipDialog();
			}

			public void mouseDragged(MouseEvent e) {

			}
		});

		songSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (isStartTrackingTouch) {
					songProgressLabel.setText(TimeUtils
							.parseString(((JSlider) e.getSource()).getValue())
							+ "/"
							+ TimeUtils.parseString(((JSlider) e.getSource())
									.getMaximum()));
				}
			}
		});

		//
		this.add(songSlider);
		this.add(songProgressLabel);
		this.add(songNameLabel);
		this.add(randomButton);
		this.add(listRepeatButton);
		this.add(sequenceButton);
		this.add(singleRepeatButton);
		this.add(singleButton);
		this.add(volumeButton);
		this.add(volumeSlider);

		this.add(mNoTranslateBtn);
		this.add(mShowTranslateBtn);
		this.add(mHideTranslateBtn);
		this.add(mHideTransliterationBtn);
		this.add(mShowTransliterationBtn);
		this.add(mNoTransliterationBtn);

		this.add(showDesktopLyricsButton);
		this.add(unShowDesktopLyricsButton);
		this.add(nextButton);
		this.add(pauseButton);
		this.add(playButton);
		this.add(preButton);
		this.add(makeLyricsButton);

	}

	/**
	 * 初始化播放模式
	 */
	private void initPlayModel() {
		// 0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
		switch (playModel) {
		case 0:
			sequenceButton.setVisible(true);
			randomButton.setVisible(false);
			listRepeatButton.setVisible(false);
			singleButton.setVisible(false);
			singleRepeatButton.setVisible(false);

			break;

		case 1:

			sequenceButton.setVisible(false);
			randomButton.setVisible(true);
			listRepeatButton.setVisible(false);
			singleButton.setVisible(false);
			singleRepeatButton.setVisible(false);

			break;

		case 2:

			sequenceButton.setVisible(false);
			randomButton.setVisible(false);
			listRepeatButton.setVisible(true);
			singleButton.setVisible(false);
			singleRepeatButton.setVisible(false);

			break;

		case 3:

			sequenceButton.setVisible(false);
			randomButton.setVisible(false);
			listRepeatButton.setVisible(false);
			singleButton.setVisible(true);
			singleRepeatButton.setVisible(false);

			break;
		case 4:

			sequenceButton.setVisible(false);
			randomButton.setVisible(false);
			listRepeatButton.setVisible(false);
			singleButton.setVisible(false);
			singleRepeatButton.setVisible(true);

			break;

		default:
			break;
		}

	}

	/**
	 * 初始化声音按钮的图标
	 * 
	 * @param
	 * @param baseButtonSize
	 */
	private void initVolumeButtonUI(int baseButtonSize) {
		int volumeSize = BaseData.volumeSize;
		String baseIconPath = Constants.PATH_ICON + File.separator;
		String sound_normalPath = "Sound_normal_";
		String sound_hotPath = "Sound_hot_";
		String sound_downPath = "Sound_down_";
		if (volumeSize == 0) {
			sound_normalPath = sound_normalPath + "05.png";
			sound_hotPath = sound_hotPath + "05.png";
			sound_downPath = sound_downPath + "05.png";

			volumeButton.setToolTipText("音量开");

		} else if (0 < volumeSize && volumeSize <= 25) {
			sound_normalPath = sound_normalPath + "01.png";
			sound_hotPath = sound_hotPath + "01.png";
			sound_downPath = sound_downPath + "01.png";

			volumeButton.setToolTipText("静音");
		} else if (20 < volumeSize && volumeSize <= 50) {
			sound_normalPath = sound_normalPath + "02.png";
			sound_hotPath = sound_hotPath + "02.png";
			sound_downPath = sound_downPath + "02.png";

			volumeButton.setToolTipText("静音");
		} else if (50 < volumeSize && volumeSize <= 75) {
			sound_normalPath = sound_normalPath + "03.png";
			sound_hotPath = sound_hotPath + "03.png";
			sound_downPath = sound_downPath + "03.png";

			volumeButton.setToolTipText("静音");
		} else if (75 < volumeSize && volumeSize <= 100) {
			sound_normalPath = sound_normalPath + "04.png";
			sound_hotPath = sound_hotPath + "04.png";
			sound_downPath = sound_downPath + "04.png";

			volumeButton.setToolTipText("静音");
		}

		ImageIcon icon = new ImageIcon(baseIconPath + sound_normalPath);
		icon.setImage(icon.getImage().getScaledInstance(baseButtonSize * 3 / 2,
				baseButtonSize * 3 / 2, Image.SCALE_SMOOTH));
		volumeButton.setIcon(icon);

		ImageIcon hotIcon = new ImageIcon(baseIconPath + sound_hotPath);
		hotIcon.setImage(hotIcon.getImage().getScaledInstance(
				baseButtonSize * 3 / 2, baseButtonSize * 3 / 2,
				Image.SCALE_SMOOTH));
		volumeButton.setRolloverIcon(hotIcon);

		ImageIcon dowmIcon = new ImageIcon(baseIconPath + sound_downPath);
		dowmIcon.setImage(dowmIcon.getImage().getScaledInstance(
				baseButtonSize * 3 / 2, baseButtonSize * 3 / 2,
				Image.SCALE_SMOOTH));
		volumeButton.setPressedIcon(dowmIcon);
	}

	@Override
	public void update(Observable o, Object data) {
		updateUI(data);
	}

	/**
	 * 
	 * @param data
	 */
	protected void updateUI(Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.INITMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPLAYMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC
					|| songMessage.getType() == SongMessage.ERRORMUSIC
					|| songMessage.getType() == SongMessage.SERVICEERRORMUSIC) {
				refreshUI(songMessage);
			}
		} else if (data instanceof MessageIntent) {
			MessageIntent messageIntent = (MessageIntent) data;
			if (messageIntent.getAction().equals(MessageIntent.CLOSEDESLRC)) {
				initDesktopLrc();
			}
		}
	}

	/**
	 * 初始化桌面歌词窗口
	 */
	private void initDesktopLrc() {
		if (BaseData.showDesktopLyrics) {
			unShowDesktopLyricsButton.setVisible(false);
			showDesktopLyricsButton.setVisible(true);
		} else {
			unShowDesktopLyricsButton.setVisible(true);
			showDesktopLyricsButton.setVisible(false);
		}
	}

	/**
	 * 刷新ui
	 * 
	 * @param songMessage
	 */
	private void refreshUI(SongMessage songMessage) {

		SongInfo mSongInfo = songMessage.getSongInfo();
		if (mSongInfo != null) {
			if (songMessage.getType() == SongMessage.INITMUSIC) {

				if (MediaManage.PLAYING == MediaManage.getMediaManage()
						.getPlayStatus()) {
					playButton.setVisible(false);
					pauseButton.setVisible(true);

					desLrcDialog.getDesOperatePanel().getPlayButton()
							.setVisible(false);
					desLrcDialog.getDesOperatePanel().getPauseButton()
							.setVisible(true);
				} else {
					playButton.setVisible(true);
					pauseButton.setVisible(false);

					desLrcDialog.getDesOperatePanel().getPlayButton()
							.setVisible(true);
					desLrcDialog.getDesOperatePanel().getPauseButton()
							.setVisible(false);
				}

				songNameLabel.setText(mSongInfo.getDisplayName());
				songSlider.setValue(0);
				songSlider.setMaximum((int) mSongInfo.getDuration());
				songProgressLabel.setText(TimeUtils.parseString(0) + "/"
						+ TimeUtils.parseString((int) mSongInfo.getDuration()));

			} else if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {
				playButton.setVisible(false);
				pauseButton.setVisible(true);

				desLrcDialog.getDesOperatePanel().getPlayButton()
						.setVisible(false);
				desLrcDialog.getDesOperatePanel().getPauseButton()
						.setVisible(true);

			} else if (songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

				if (!isStartTrackingTouch) {
					songSlider.setValue((int) mSongInfo.getPlayProgress());
					songProgressLabel.setText(TimeUtils
							.parseString((int) mSongInfo.getPlayProgress())
							+ "/"
							+ TimeUtils.parseString((int) mSongInfo
									.getDuration()));
				}

			} else if (songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC) {
				playButton.setVisible(true);
				pauseButton.setVisible(false);

				desLrcDialog.getDesOperatePanel().getPlayButton()
						.setVisible(true);
				desLrcDialog.getDesOperatePanel().getPauseButton()
						.setVisible(false);

				songSlider.setValue((int) mSongInfo.getPlayProgress());
				songProgressLabel.setText(TimeUtils.parseString((int) mSongInfo
						.getPlayProgress())
						+ "/"
						+ TimeUtils.parseString((int) mSongInfo.getDuration()));

			}
		} else {
			songNameLabel.setText(Constants.APPTITLE);

			songSlider.setValue(0);
			songSlider.setMaximum(0);
			songProgressLabel.setText(TimeUtils.parseString(0));
			playButton.setVisible(true);
			pauseButton.setVisible(false);

			desLrcDialog.getDesOperatePanel().getPlayButton().setVisible(true);
			desLrcDialog.getDesOperatePanel().getPauseButton()
					.setVisible(false);

		}
	}
	
	public ExtraLyricsListener getExtraLyricsListener() {
		return mExtraLyricsListener;
	}

}

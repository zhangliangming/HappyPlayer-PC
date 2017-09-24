package com.happy.widget.panel.des;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.model.MessageIntent;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.button.DesOperateButton;
import com.happy.widget.dialog.DesLrcDialog;
import com.happy.widget.label.DesOperateLabel;
import com.happy.widget.panel.des.DesLrcColorParentPanel.DesLrcEvent;
import com.happy.widget.panel.lrc.FloatLyricsView;
import com.happy.widget.panel.lrc.FloatLyricsView.ExtraLyricsListener;

/**
 * 桌面操作面板
 * 
 * @author zhangliangming
 * 
 */
public class DesOperatePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mHeight = 0;

	private int mWidth = 0;
	/**
	 * 
	 */
	private int padding = 10;

	/**
	 * 播放按钮
	 */
	private DesOperateButton playButton;
	/**
	 * 暂停按钮
	 */
	private DesOperateButton pauseButton;
	/**
	 * 上一首
	 */
	private DesOperateButton preButton;
	/**
	 * 下一首
	 */
	private DesOperateButton nextButton;
	/**
	 * 桌面歌词颜色面板
	 */
	private DesLrcColorParentPanel[] lrcColorPanels;

	/**
	 * 基本图标路径
	 */
	private String iconPath = Constants.PATH_ICON + File.separator;
	/**
	 * 判断是否进入
	 */
	private boolean isEnter = false;
	/**
	 * 桌面窗口事件
	 */
	private MouseInputListener desLrcDialogMouseListener;
	private DesLrcDialog mDesLrcDialog;

	private MouseListener mouseListener = new MouseListener();

	/**
	 * 歌词颜色索引
	 */
	private int lrcColorIndex = BaseData.desktopLrcIndex;

	// 翻译歌词
	private DesOperateButton mNoTranslateBtn;
	private DesOperateButton mHideTranslateBtn;
	private DesOperateButton mShowTranslateBtn;
	// 音译歌词
	private DesOperateButton mNoTransliterationBtn;
	private DesOperateButton mHideTransliterationBtn;
	private DesOperateButton mShowTransliterationBtn;

	//
	private ExtraLyricsListener mExtraLyricsListener;

	public DesOperatePanel(int width, int height,
			MouseInputListener desLrcDialogMouseListener,
			DesLrcDialog desLrcDialog) {
		this.mDesLrcDialog = desLrcDialog;
		this.desLrcDialogMouseListener = desLrcDialogMouseListener;
		this.mWidth = width;
		this.mHeight = height;
		// 初始化组件
		initComponent();
		initLockEvent();

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

	private void initLockEvent() {

		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);

	}

	private void initComponent() {
		this.setLayout(null);

		int buttonSize = mHeight;

		String iconButtonBaseIconPath = iconPath + "icon_def.png";
		String iconButtonOverIconPath = iconPath + "icon_hot.png";
		String iconButtonPressedIconPath = iconPath + "icon_down.png";

		DesOperateButton iconButton = new DesOperateButton(
				iconButtonBaseIconPath, iconButtonOverIconPath,
				iconButtonPressedIconPath, (buttonSize - 5), (buttonSize - 5),
				desLrcDialogMouseListener, this);
		iconButton.setBounds(padding, (mHeight - (buttonSize - 5)) / 2 + 1,
				(buttonSize - 5), (buttonSize - 5));

		iconButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.FRAME_NORMAL);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		});

		// 上一首
		String preButtonBaseIconPath = iconPath + "desPre_def.png";
		String preButtonOverIconPath = iconPath + "desPre_hot.png";
		String preButtonPressedIconPath = iconPath + "desPre_down.png";

		preButton = new DesOperateButton(preButtonBaseIconPath,
				preButtonOverIconPath, preButtonPressedIconPath, buttonSize,
				buttonSize, desLrcDialogMouseListener, this);

		preButton.setBounds(
				iconButton.getX() + iconButton.getWidth() + padding, 0,
				buttonSize, buttonSize);
		preButton.setToolTipText("上一首");

		preButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PREMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		// 播放按钮

		String playButtonBaseIconPath = iconPath + "desPlay_def.png";
		String playButtonOverIconPath = iconPath + "desPlay_hot.png";
		String playButtonPressedIconPath = iconPath + "desPlay_down.png";

		playButton = new DesOperateButton(playButtonBaseIconPath,
				playButtonOverIconPath, playButtonPressedIconPath, buttonSize,
				buttonSize, desLrcDialogMouseListener, this);

		playButton.setBounds(preButton.getX() + preButton.getWidth() + padding,
				0, buttonSize, buttonSize);
		playButton.setToolTipText("播放");

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		// 暂停按钮
		String pauseButtonBaseIconPath = iconPath + "desPause_def.png";
		String pauseButtonOverIconPath = iconPath + "desPause_hot.png";
		String pauseButtonPressedIconPath = iconPath + "desPause_down.png";

		pauseButton = new DesOperateButton(pauseButtonBaseIconPath,
				pauseButtonOverIconPath, pauseButtonPressedIconPath,
				buttonSize, buttonSize, desLrcDialogMouseListener, this);

		pauseButton.setBounds(
				preButton.getX() + preButton.getWidth() + padding, 0,
				buttonSize, buttonSize);
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

		String nextButtonBaseIconPath = iconPath + "desNext_def.png";
		String nextButtonOverIconPath = iconPath + "desNext_hot.png";
		String nextButtonPressedIconPath = iconPath + "desNext_down.png";

		nextButton = new DesOperateButton(nextButtonBaseIconPath,
				nextButtonOverIconPath, nextButtonPressedIconPath, buttonSize,
				buttonSize, desLrcDialogMouseListener, this);

		nextButton.setBounds(pauseButton.getX() + pauseButton.getWidth()
				+ padding, 0, buttonSize, buttonSize);
		nextButton.setToolTipText("下一首");

		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.NEXTMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});

		// 增加按钮
		String increaseButtonBaseIconPath = iconPath + "desIncrease_def.png";
		String increaseButtonOverIconPath = iconPath + "desIncrease_hot.png";
		String increaseButtonPressedIconPath = iconPath
				+ "desIncrease_down.png";

		// 增加按钮
		DesOperateButton increaseButton = new DesOperateButton(
				increaseButtonBaseIconPath, increaseButtonOverIconPath,
				increaseButtonPressedIconPath, buttonSize, buttonSize,
				desLrcDialogMouseListener, this);

		increaseButton.setBounds(nextButton.getX() + nextButton.getWidth()
				+ padding, 0, buttonSize, buttonSize);
		increaseButton.setToolTipText("字体增大");
		increaseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int fontSize = BaseData.desktopLrcFontSize;
				if (fontSize == BaseData.desktopLrcFontMaxSize) {
					return;
				} else {
					fontSize = fontSize + 5;
					if (fontSize > BaseData.desktopLrcFontMaxSize)
						fontSize = BaseData.desktopLrcFontMaxSize;
				}
				BaseData.desktopLrcFontSize = fontSize;
				// 更新歌词大小
				mDesLrcDialog.getFloatLyricsView().refreshLrcFontSize();
			}
		});

		// 减少按钮
		String decreaseButtonBaseIconPath = iconPath + "desDecrease_def.png";
		String decreaseButtonOverIconPath = iconPath + "desDecrease_hot.png";
		String decreaseButtonPressedIconPath = iconPath
				+ "desDecrease_down.png";
		// 减少按钮
		DesOperateButton decreaseButton = new DesOperateButton(
				decreaseButtonBaseIconPath, decreaseButtonOverIconPath,
				decreaseButtonPressedIconPath, buttonSize, buttonSize,
				desLrcDialogMouseListener, this);

		decreaseButton.setBounds(
				increaseButton.getX() + increaseButton.getWidth() + padding, 0,
				buttonSize, buttonSize);
		decreaseButton.setToolTipText("字体减少");
		decreaseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int fontSize = BaseData.desktopLrcFontSize;
				if (fontSize == BaseData.desktopLrcFontMinSize) {
					return;
				} else {
					fontSize = fontSize - 5;
					if (fontSize < BaseData.desktopLrcFontMinSize)
						fontSize = BaseData.desktopLrcFontMinSize;
				}
				BaseData.desktopLrcFontSize = fontSize;
				// 更新歌词大小
				mDesLrcDialog.getFloatLyricsView().refreshLrcFontSize();
			}

		});

		lrcColorPanels = new DesLrcColorParentPanel[BaseData.DESLRCNOREADCOLORFRIST.length];
		//
		int x = (decreaseButton.getX() + decreaseButton.getWidth() + padding);
		for (int i = 0; i < lrcColorPanels.length; i++) {
			lrcColorPanels[i] = new DesLrcColorParentPanel(buttonSize,
					buttonSize, BaseData.DESLRCNOREADCOLORFRIST[i],
					desLrcDialogMouseListener, this, i, lrcEvent);

			lrcColorPanels[i].setBounds(x, 0, buttonSize, buttonSize);
			this.add(lrcColorPanels[i]);

			x = x + buttonSize + padding;
		}

		lrcColorPanels[lrcColorIndex].setSelect(true);

		// 翻译歌词
		// 无翻译歌词
		mNoTranslateBtn = new DesOperateButton(iconPath + "bqm.png", iconPath
				+ "bqm.png", iconPath + "bqm.png", buttonSize - padding / 2,
				buttonSize - padding / 2, desLrcDialogMouseListener, this);
		mNoTranslateBtn.setBounds(x, 0, buttonSize, buttonSize);
		mNoTranslateBtn.setToolTipText("无翻译歌词");

		// 隐藏翻译歌词
		mHideTranslateBtn = new DesOperateButton(iconPath + "bql.png", iconPath
				+ "bql.png", iconPath + "bql.png", buttonSize - padding / 2,
				buttonSize - padding / 2, desLrcDialogMouseListener, this);
		mHideTranslateBtn.setBounds(x, 0, buttonSize, buttonSize);
		mHideTranslateBtn.setToolTipText("隐藏翻译歌词");
		mHideTranslateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mHideTranslateBtn.setVisible(false);
				mShowTranslateBtn.setVisible(true);

				mDesLrcDialog.getFloatLyricsView().setExtraLrcStatus(
						FloatLyricsView.NOSHOWEXTRALRC);
			}
		});

		// 显示翻译歌词
		mShowTranslateBtn = new DesOperateButton(iconPath + "bqm1.png",
				iconPath + "bqm1.png", iconPath + "bqm1.png", buttonSize
						- padding / 2, buttonSize - padding / 2,
				desLrcDialogMouseListener, this);
		mShowTranslateBtn.setBounds(x, 0, buttonSize, buttonSize);
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
				mDesLrcDialog.getFloatLyricsView().setExtraLrcStatus(
						FloatLyricsView.SHOWTRANSLATELRC);

			}
		});

		// 音译歌词
		int htbX = mHideTranslateBtn.getX() + mHideTranslateBtn.getWidth()
				+ padding;
		// 无音译歌词
		mNoTransliterationBtn = new DesOperateButton(iconPath + "bqo.png",
				iconPath + "bqo.png", iconPath + "bqo.png", buttonSize
						- padding / 2, buttonSize - padding / 2,
				desLrcDialogMouseListener, this);
		mNoTransliterationBtn.setBounds(htbX, 0, buttonSize, buttonSize);
		mNoTransliterationBtn.setToolTipText("无音译歌词");

		// 隐藏音译歌词
		mHideTransliterationBtn = new DesOperateButton(iconPath + "bqn.png",
				iconPath + "bqn.png", iconPath + "bqn.png", buttonSize
						- padding / 2, buttonSize - padding / 2,
				desLrcDialogMouseListener, this);
		mHideTransliterationBtn.setBounds(htbX, 0, buttonSize, buttonSize);
		mHideTransliterationBtn.setToolTipText("隐藏音译歌词");
		mHideTransliterationBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// 音译歌词
				mHideTransliterationBtn.setVisible(false);
				mShowTransliterationBtn.setVisible(true);

				mDesLrcDialog.getFloatLyricsView().setExtraLrcStatus(
						FloatLyricsView.NOSHOWEXTRALRC);

			}
		});

		// 显示音译歌词
		mShowTransliterationBtn = new DesOperateButton(iconPath + "bqo1.png",
				iconPath + "bqo1.png", iconPath + "bqo1.png", buttonSize
						- padding / 2, buttonSize - padding / 2,
				desLrcDialogMouseListener, this);
		mShowTransliterationBtn.setBounds(htbX, 0, buttonSize, buttonSize);
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

				mDesLrcDialog.getFloatLyricsView().setExtraLrcStatus(
						FloatLyricsView.SHOWTRANSLITERATIONLRC);

			}
		});

		int mlbX = mHideTransliterationBtn.getX()
				+ mHideTransliterationBtn.getWidth() + padding;
		// 制作歌词
		String makeLrcButtonBaseIconPath = iconPath + "desMakeLrc_def.png";
		String makeLrcButtonOverIconPath = iconPath + "desMakeLrc_rollover.png";
		String makeLrcButtonPressedIconPath = iconPath
				+ "desMakeLrc_pressed.png";
		// 制作歌词按钮
		DesOperateButton makeLrcButton = new DesOperateButton(
				makeLrcButtonBaseIconPath, makeLrcButtonOverIconPath,
				makeLrcButtonPressedIconPath, buttonSize * 2, buttonSize,
				mouseListener, this, true);
		makeLrcButton.setBounds(mlbX, 0, buttonSize * 2, buttonSize);
		makeLrcButton.setToolTipText("制作歌词");
		makeLrcButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (BaseData.playInfoID.equals("-1")) {
					JOptionPane.showMessageDialog(null, "请选择歌曲", "提示",
							JOptionPane.ERROR_MESSAGE);
				} else {

					//
					BaseData.showDesktopLyrics = false;

					MessageIntent messageIntentT = new MessageIntent();
					messageIntentT.setAction(MessageIntent.CLOSEDESLRC);
					ObserverManage.getObserver().setMessage(messageIntentT);

					try {
						Thread.sleep(100);
						MessageIntent messageIntent = new MessageIntent();
						messageIntent
								.setAction(MessageIntent.OPEN_MADELRCDIALOG);
						ObserverManage.getObserver().setMessage(messageIntent);
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			}
		});

		// 锁
		String lockButtonBaseIconPath = iconPath + "desLock_def.png";
		String lockButtonOverIconPath = iconPath + "desLock_hot.png";
		String lockButtonPressedIconPath = iconPath + "desLock_down.png";
		DesOperateButton lockButton = new DesOperateButton(
				lockButtonBaseIconPath, lockButtonOverIconPath,
				lockButtonPressedIconPath, buttonSize, buttonSize,
				desLrcDialogMouseListener, this);
		lockButton.setBounds(makeLrcButton.getX() + makeLrcButton.getWidth()
				+ padding, 0, buttonSize, buttonSize);
		lockButton.setToolTipText("锁定歌词");

		lockButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!BaseData.desLrcIsLock) {
					BaseData.desLrcIsLock = true;
					MessageIntent messageIntent = new MessageIntent();
					messageIntent.setAction(MessageIntent.LOCKDESLRC);
					ObserverManage.getObserver().setMessage(messageIntent);
				}
			}
		});

		// 关闭按钮
		String closeButtonBaseIconPath = iconPath + "desClose_def.png";
		String closeButtonOverIconPath = iconPath + "desClose_hot.png";
		String closeButtonPressedIconPath = iconPath + "desClose_down.png";

		DesOperateButton closeButton = new DesOperateButton(
				closeButtonBaseIconPath, closeButtonOverIconPath,
				closeButtonPressedIconPath, buttonSize, buttonSize,
				desLrcDialogMouseListener, this);
		closeButton.setBounds(lockButton.getX() + lockButton.getWidth()
				+ padding, 0, buttonSize, buttonSize);
		closeButton.setToolTipText("关闭");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				BaseData.showDesktopLyrics = false;

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.CLOSEDESLRC);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		});

		DesOperateLabel bgl = new DesOperateLabel(mWidth, mHeight,
				desLrcDialogMouseListener, this);
		bgl.setBounds(0, 0, mWidth, mHeight);

		this.add(iconButton);
		this.add(preButton);
		this.add(playButton);
		this.add(pauseButton);
		this.add(nextButton);
		this.add(increaseButton);
		this.add(decreaseButton);
		this.add(mNoTranslateBtn);
		this.add(mShowTranslateBtn);
		this.add(mHideTranslateBtn);
		this.add(mHideTransliterationBtn);
		this.add(mShowTransliterationBtn);
		this.add(mNoTransliterationBtn);
		this.add(makeLrcButton);
		this.add(lockButton);
		this.add(closeButton);
		this.add(bgl);
	}

	public ExtraLyricsListener getExtraLyricsListener() {
		return mExtraLyricsListener;
	}

	private DesLrcEvent lrcEvent = new DesLrcEvent() {

		@Override
		public void select(int index) {
			if (index != lrcColorIndex) {
				lrcColorPanels[lrcColorIndex].setSelect(false);
				lrcColorIndex = index;
				BaseData.desktopLrcIndex = index;
				lrcColorPanels[lrcColorIndex].setSelect(true);

				MessageIntent messageIntent = new MessageIntent();
				messageIntent.setAction(MessageIntent.DESMANYLINELRCCOLOR);
				ObserverManage.getObserver().setMessage(messageIntent);
			}
		}
	};

	public boolean getEnter() {
		return isEnter;
	}

	public void setEnter(boolean isEnter) {
		this.isEnter = isEnter;
		repaint();
	}

	public DesOperateButton getPlayButton() {
		return playButton;
	}

	public DesOperateButton getPauseButton() {
		return pauseButton;
	}

	private class MouseListener implements MouseInputListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mouseClicked(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mouseReleased(e);
			// setCursor(null);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (!BaseData.desLrcIsLock) {
				isEnter = true;
				// repaint();

				desLrcDialogMouseListener.mouseEntered(e);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!BaseData.desLrcIsLock) {
				isEnter = false;
				// repaint();
				desLrcDialogMouseListener.mouseExited(e);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// /* 鼠标左键托动事件 */
			// if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			// setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			// }
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mouseDragged(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mouseMoved(e);
		}

	}
}

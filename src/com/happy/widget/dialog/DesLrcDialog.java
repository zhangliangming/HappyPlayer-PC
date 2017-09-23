package com.happy.widget.dialog;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.model.MessageIntent;
import com.happy.observable.ObserverManage;
import com.happy.widget.panel.FloatLyricsView;
import com.happy.widget.panel.des.DesOperatePanel;
import com.sun.awt.AWTUtilities;

/**
 * 桌面歌词窗口
 * 
 * @author zhangliangming
 * 
 */
public class DesLrcDialog extends JDialog implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度
	 */
	private int mHeight = 0;
	/**
	 * 宽度
	 */
	private int mWidth = 0;

	private int mY = 0;

	private int maxY = 0;

	private int g_mouseX = 0, g_mouseY = 0; /* 鼠标的坐标 */
	/**
	 * 操作面板
	 */
	private DesOperatePanel desOperatePanel;

	private DesLrcDialogMouseListener desLrcDialogMouseListener = new DesLrcDialogMouseListener();

	/**
	 * 歌词面板
	 */
	private FloatLyricsView floatLyricsView;

	public DesLrcDialog() {

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		mHeight = screenDimension.height / 6 - 10;
		mWidth = screenDimension.width;
		mY = screenDimension.height;

		init();
		initComponent();

		this.setSize(mWidth, mHeight);
		this.setMinimumSize(new Dimension(mWidth / 3 * 2, mHeight));
		this.setMaximumSize(new Dimension(mWidth, mHeight));
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		AWTUtilities.setWindowOpaque(this, false);// 关键代码！ 设置窗体透明

		this.addMouseListener(desLrcDialogMouseListener);
		this.addMouseMotionListener(desLrcDialogMouseListener);
		ObserverManage.getObserver().addObserver(this);
	}

	private void init() {

		// 获取屏幕边界
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				getGraphicsConfiguration());
		// 取得底部边界高度，即任务栏高度
		int taskHeight = screenInsets.bottom;
		maxY = mY - mHeight - taskHeight;

	}

	private void initComponent() {
		this.getContentPane().setLayout(null);

		int width = mHeight / 5 * 15 + 10 * 15;

		desOperatePanel = new DesOperatePanel(width, mHeight / 5,
				desLrcDialogMouseListener);
		desOperatePanel.setBounds((mWidth - width) / 2, 0, width, mHeight / 5);
		desOperatePanel.setVisible(false);

		floatLyricsView = new FloatLyricsView(mWidth, mHeight - mHeight / 5,
				desLrcDialogMouseListener);
		floatLyricsView.setBounds(0,
				desOperatePanel.getY() + desOperatePanel.getHeight(), mWidth,
				mHeight - mHeight / 5);

		this.getContentPane().add(desOperatePanel);
		this.getContentPane().add(floatLyricsView);
	}

	public int getmHeight() {
		return mHeight;
	}

	public int getmWidth() {
		return mWidth;
	}

	public int getmY() {
		return mY;
	}

	public DesOperatePanel getDesOperatePanel() {
		return desOperatePanel;
	}

	public FloatLyricsView getFloatLyricsView() {
		return floatLyricsView;
	}

	private boolean isDragged = false;

	/**
	 * 初始化桌面歌词锁事件
	 */
	private void initLock() {
		if (!BaseData.desLrcIsLock) {
		} else {
			desOperatePanel.setVisible(false);
			floatLyricsView.setShow(false);
			desOperatePanel.repaint();
		}

	}

	/**
	 * 窗口鼠标事件
	 * 
	 * @author Administrator
	 * 
	 */
	private class DesLrcDialogMouseListener implements MouseInputListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			/* 获取鼠标的位置 */
			g_mouseX = e.getX();
			g_mouseY = e.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			setCursor(null);
			// if (!floatLyricsView.getEnter() && !desOperatePanel.getEnter()) {
			// desOperatePanel.setVisible(false);
			// floatLyricsView.setEnter(false);
			// desOperatePanel.setEnter(false);
			// }
			isDragged = false;
			mouseExited(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// desOperatePanel.setVisible(true);
			// floatLyricsView.setEnter(true);
			if (floatLyricsView.getEnter()) {
				desOperatePanel.setVisible(true);
			} else if (desOperatePanel.getEnter()) {
				desOperatePanel.setVisible(true);
				floatLyricsView.setShow(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!isDragged) {
				// desOperatePanel.setVisible(false);
				// floatLyricsView.setEnter(false);
				// desOperatePanel.setEnter(false);
				if (floatLyricsView.getEnter() == false) {
					if (desOperatePanel.getEnter()) {
						desOperatePanel.setVisible(true);
						floatLyricsView.setShow(true);
					} else {
						desOperatePanel.setVisible(false);
						floatLyricsView.setShow(false);
					}
				} else if (desOperatePanel.getEnter() == false) {
					if (floatLyricsView.getEnter()) {
						desOperatePanel.setVisible(true);
						floatLyricsView.setShow(true);
					} else {
						desOperatePanel.setVisible(false);
						floatLyricsView.setShow(false);
					}
				} else {
					desOperatePanel.setVisible(false);
					floatLyricsView.setShow(false);
				}
			} else {
				desOperatePanel.setVisible(true);
				floatLyricsView.setShow(true);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {

			/* 鼠标左键托动事件 */
			if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				// /* 重新定义窗口的位置 */
				if (getY() + e.getY() - g_mouseY < 0)
					setLocation(getX(), 0);
				else if (getY() + e.getY() - g_mouseY > maxY)

					setLocation(getX(), maxY);
				else
					setLocation(getX(), getY() + e.getY() - g_mouseY);
			}

			isDragged = true;

		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}

	}

	@Override
	public void update(Observable o, final Object data) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (data instanceof MessageIntent) {
					MessageIntent messageIntent = (MessageIntent) data;
					if (messageIntent.getAction().equals(
							MessageIntent.LOCKDESLRC)) {
						initLock();
					}
				}
			}
		});
	}
}
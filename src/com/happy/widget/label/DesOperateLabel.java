package com.happy.widget.label;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.widget.panel.des.DesOperatePanel;

/**
 * 歌词操作面板背景图片
 * 
 * @author Administrator
 * 
 */
public class DesOperateLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 桌面窗口事件
	 */
	private MouseInputListener desLrcDialogMouseListener;
	private MouseListener mouseListener = new MouseListener();

	private DesOperatePanel desOperatePanel;

	private String backgroundPath;

	public DesOperateLabel(int width, int height,
			MouseInputListener desLrcDialogMouseListener,
			DesOperatePanel desOperatePanel) {

		this.desLrcDialogMouseListener = desLrcDialogMouseListener;
		this.desOperatePanel = desOperatePanel;

		initLockEvent();
		initSkin();

	}

	private void initSkin() {
		backgroundPath = Constants.PATH_SKIN + File.separator
				+ BaseData.bGroundFileName;
	}

	protected void paintComponent(Graphics g) {

		ImageIcon icon = new ImageIcon(backgroundPath);
		Image img = icon.getImage();
		g.drawImage(img, 0, 0, icon.getIconWidth(), icon.getIconHeight(),
				icon.getImageObserver());
	}

	private void initLockEvent() {
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);

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
				desOperatePanel.setEnter(true);
				desLrcDialogMouseListener.mouseEntered(e);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!BaseData.desLrcIsLock) {
				desOperatePanel.setEnter(false);
				desLrcDialogMouseListener.mouseExited(e);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
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

package com.happy.widget.panel.des;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;

/**
 * 桌面歌词颜色面板
 * 
 * @author Administrator
 * 
 */
public class DesLrcColorParentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width = 0;
	private int height = 0;
	private Color color;

	/**
	 * 桌面窗口事件
	 */
	private MouseInputListener desLrcDialogMouseListener;
	private MouseListener mouseListener = new MouseListener();

	private DesOperatePanel desOperatePanel;

	private DesLrcColorPanel desLrcColorPanel;

	private JLabel jstatusLabel;

	/**
	 * 
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param color
	 *            面板显示的颜色
	 * @param index
	 * @param lrcEvent
	 */
	public DesLrcColorParentPanel(int width, int height, Color color,
			MouseInputListener desLrcDialogMouseListener,
			DesOperatePanel desOperatePanel, int index, DesLrcEvent lrcEvent) {

		this.width = width;
		this.height = height;
		this.color = color;

		this.setOpaque(false);

		this.setLayout(null);
		//
		desLrcColorPanel = new DesLrcColorPanel(width / 3 * 2, width / 3 * 2,
				color, desLrcDialogMouseListener, desOperatePanel, index,
				lrcEvent);
		int x = (width - width / 3 * 2) / 2;
		int y = (height - width / 3 * 2) / 2;
		desLrcColorPanel.setBounds(x, y, width / 3 * 2, width / 3 * 2);

		//

		String statusIconFilePath = Constants.PATH_ICON + File.separator
				+ "skin_selected_bg_tip.png";
		int sWidth = width / 3 * 2 - 2;
		int sHeight = width / 3 * 2 - 2;
		jstatusLabel = new JLabel(getBackgroundImageIcon(sWidth, sHeight,
				statusIconFilePath));
		jstatusLabel.setBounds(x, y, sWidth, sHeight);
		jstatusLabel.setVisible(false);

		this.add(jstatusLabel);
		this.add(desLrcColorPanel);

		this.desLrcDialogMouseListener = desLrcDialogMouseListener;
		this.desOperatePanel = desOperatePanel;
		initLockEvent();
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

	public void setSelect(boolean isSelect) {
		if (isSelect) {
			jstatusLabel.setVisible(true);
		} else {
			jstatusLabel.setVisible(false);
		}
	}

	interface DesLrcEvent {
		public void select(int index);
	}

	/**
	 * 获取背景图片
	 * 
	 * @return
	 */
	private ImageIcon getBackgroundImageIcon(int width, int height, String path) {
		ImageIcon background = new ImageIcon(path);// 背景图片
		background.setImage(background.getImage().getScaledInstance(width,
				height, Image.SCALE_SMOOTH));
		return background;
	}
}

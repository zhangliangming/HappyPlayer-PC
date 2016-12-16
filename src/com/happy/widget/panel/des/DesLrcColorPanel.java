package com.happy.widget.panel.des;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.widget.panel.des.DesLrcColorParentPanel.DesLrcEvent;

/**
 * 歌词颜色面板
 * 
 * @author Administrator
 * 
 */
public class DesLrcColorPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 鼠标是否进入
	 */
	private boolean isEnter = false;

	/**
	 * 鼠标是否已经选择
	 */
	private boolean isSelected = false;

	private int width = 0;
	private int height = 0;
	private Color color;

	/**
	 * 桌面窗口事件
	 */
	private MouseInputListener desLrcDialogMouseListener;
	private MouseListener mouseListener = new MouseListener();

	private DesOperatePanel desOperatePanel;

	private int colorIndex = 0;

	private DesLrcEvent desLrcEvent;

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
	public DesLrcColorPanel(int width, int height, Color color,
			MouseInputListener desLrcDialogMouseListener,
			DesOperatePanel desOperatePanel, int index, DesLrcEvent desLrcEvent) {

		this.desLrcEvent = desLrcEvent;
		this.colorIndex = index;
		this.width = width;
		this.height = height;
		this.color = color;
		this.desLrcDialogMouseListener = desLrcDialogMouseListener;
		this.desOperatePanel = desOperatePanel;

		this.setBackground(color);

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
			if (!BaseData.desLrcIsLock) {
				desLrcDialogMouseListener.mousePressed(e);
				if (desLrcEvent != null) {
					desLrcEvent.select(colorIndex);
				}
			}
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
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				desOperatePanel.setEnter(true);
				desLrcDialogMouseListener.mouseEntered(e);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!BaseData.desLrcIsLock) {
				setCursor(null);
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

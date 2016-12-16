package com.happy.event;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

/**
 * 
 * @author Administrator 窗口移动事件
 * 
 */
public class PanelMoveFrame {
	/**
	 * 操作面板
	 * 
	 * @param moveframe
	 * @param panel
	 */
	private JPanel linePanel;
	/**
	 * 要移动的窗口
	 */
	private JFrame MoveFrame;

	public PanelMoveFrame(JPanel panel, JFrame moveframe) {
		this.MoveFrame = moveframe;
		this.linePanel = panel;
		// 鼠标事件处理类
		MouseEventListener mouseListener = new MouseEventListener(MoveFrame);
		linePanel.addMouseListener(mouseListener);
		linePanel.addMouseMotionListener(mouseListener);
	}

	/**
	 * 鼠标事件处理
	 * 
	 */
	class MouseEventListener implements MouseInputListener {

		Point origin;
		// 鼠标拖拽想要移动的目标组件
		JFrame frame;

		public MouseEventListener(JFrame frame) {
			this.frame = frame;
			origin = new Point();
		}

		public void mouseClicked(MouseEvent e) {
		}

		/**
		 * 记录鼠标按下时的点
		 */
		public void mousePressed(MouseEvent e) {
			origin.x = e.getX();
			origin.y = e.getY();
		}

		public void mouseReleased(MouseEvent e) {
			MoveFrame.setCursor(null);
		}

		/**
		 * 鼠标移进标题栏时，设置鼠标图标为移动图标
		 */
		public void mouseEntered(MouseEvent e) {
			// this.frame
			// .setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}

		/**
		 * 鼠标移出标题栏时，设置鼠标图标为默认指针
		 */
		public void mouseExited(MouseEvent e) {
			// this.frame.setCursor(Cursor
			// .getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		/**
		 * 鼠标在标题栏拖拽时，设置窗口的坐标位置 窗口新的坐标位置 = 移动前坐标位置+（鼠标指针当前坐标-鼠标按下时指针的位置）
		 */
		public void mouseDragged(MouseEvent e) {
			MoveFrame.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			Point p = this.frame.getLocation();
			this.frame.setLocation(p.x + (e.getX() - origin.x), p.y
					+ (e.getY() - origin.y));
		}

		public void mouseMoved(MouseEvent e) {
		}

	}

}

package com.happy.event;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * 面板移动窗口事件
 * 
 * @author Administrator
 * 
 */
public class PanelMoveDialog {

	private int g_mouseX = 0, g_mouseY = 0; /* 鼠标的坐标 */
	/**
	 * 要移动的窗口
	 */
	private JDialog MoveFrame;
	/**
	 * 操作面板
	 * 
	 * @param moveframe
	 * @param panel
	 */
	private JPanel linePanel;

	public PanelMoveDialog(JDialog moveframe, JPanel listOpePanel) {
		this.MoveFrame = moveframe;
		this.linePanel = listOpePanel;
		linePanel.addMouseMotionListener(new MouseMotionAdapter() {
			/* 添加鼠标托动事件 */
			public void mouseDragged(MouseEvent e) {
				/* 鼠标左键托动事件 */
				if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
					MoveFrame.setCursor(Cursor
							.getPredefinedCursor(Cursor.MOVE_CURSOR));
					// /* 重新定义窗口的位置 */
					// if (MoveFrame.getY() + e.getY() - g_mouseY < 40)
					// MoveFrame.setLocation(MoveFrame.getX() + e.getX()
					// - g_mouseX, 0);
					// else if (MoveFrame.getX() + e.getX() - g_mouseX < 40)
					//
					// MoveFrame.setLocation(0, MoveFrame.getY() + e.getY()
					// - g_mouseY);
					// else
					MoveFrame.setLocation(MoveFrame.getX() + e.getX()
							- g_mouseX, MoveFrame.getY() + e.getY() - g_mouseY);
				}
			}
		});
		linePanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				/* 获取鼠标的位置 */
				g_mouseX = e.getX();
				g_mouseY = e.getY();
			}

			public void mouseReleased(MouseEvent e) {
				MoveFrame.setCursor(null);
			}
		});
	}
}

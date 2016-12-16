package com.happy.widget.button;

import java.awt.Cursor;

import javax.swing.JButton;

/**
 * 
 * @author Administrator
 * 
 */
public class DefButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DefButton(int width, int height) {

		this.setBorderPainted(false);
		this.setFocusPainted(false);
		// this.setContentAreaFilled(false);
		this.setDoubleBuffered(false);
		this.setOpaque(false);
		this.setFocusable(false);
		// 设置鼠标的图标
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}

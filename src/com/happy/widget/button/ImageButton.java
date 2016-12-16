package com.happy.widget.button;

import java.awt.Cursor;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.happy.common.Constants;

/**
 * 
 * @author Administrator
 * 
 */
public class ImageButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param width
	 * @param height
	 * @param defIconName
	 *            默认图标名称
	 * @param rolloverIconName
	 *            鼠标经过的图标名称
	 * @param pressedIconName
	 *            点击后的图标名称
	 */
	public ImageButton(int width, int height, String defIconName,
			String rolloverIconName, String pressedIconName) {

		String baseIconPath = Constants.PATH_ICON + File.separator;
		if (defIconName != null && !defIconName.equals("")) {
			ImageIcon icon = new ImageIcon(baseIconPath + defIconName);
			icon.setImage(icon.getImage().getScaledInstance(width, height,
					Image.SCALE_SMOOTH));
			this.setIcon(icon);
		}
		if (rolloverIconName != null && !rolloverIconName.equals("")) {
			ImageIcon rolloverIcon = new ImageIcon(baseIconPath
					+ rolloverIconName);
			rolloverIcon.setImage(rolloverIcon.getImage().getScaledInstance(
					width, height, Image.SCALE_SMOOTH));
			this.setRolloverIcon(rolloverIcon);
		}

		if (pressedIconName != null && !pressedIconName.equals("")) {
			ImageIcon pressedIcon = new ImageIcon(baseIconPath
					+ pressedIconName);
			pressedIcon.setImage(pressedIcon.getImage().getScaledInstance(
					width, height, Image.SCALE_SMOOTH));
			this.setPressedIcon(pressedIcon);
		}

		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);
		this.setDoubleBuffered(false);
		this.setOpaque(false);
		this.setFocusable(false);
		// 设置鼠标的图标
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public ImageButton(int width, int height) {

		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);
		this.setDoubleBuffered(false);
		this.setOpaque(false);
		this.setFocusable(false);
		// 设置鼠标的图标
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}

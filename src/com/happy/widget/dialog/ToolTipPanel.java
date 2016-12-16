package com.happy.widget.dialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.happy.common.BaseData;
import com.happy.util.FontsUtil;

/**
 * 提示文本面板
 * 
 * @author Administrator
 * 
 */
public class ToolTipPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 提示字符串
	 */
	private String tipString = "";

	private int textWidth = 0;

	public ToolTipPanel() {

	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// 以达到边缘平滑的效果

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		g2d.setFont(FontsUtil.getBaseFont(BaseData.appFontSize));

		g2d.setColor(new Color(255, 255, 225));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(2));
		g2d.drawRect(0, 0, this.getWidth(), this.getHeight());

		FontMetrics fm = g2d.getFontMetrics();
		Rectangle2D rc = fm.getStringBounds(tipString, g2d);

		textWidth = (int) rc.getWidth();
		int textHeight = fm.getHeight();

		int X = (int) ((this.getWidth() - textWidth) / 2);
		int Y = (this.getHeight() + textHeight) / 2;
		g2d.setColor(Color.black);
		g2d.drawString(tipString, X, Y - (fm.getDescent() + fm.getLeading()));
	}

	public void setTipString(String tipString) {
		this.tipString = tipString;
		repaint();
	}

	public int getTextWidth() {
		if (textWidth == 0) {
			FontMetrics fm = getFontMetrics(FontsUtil
					.getBaseFont(BaseData.appFontSize));
			textWidth = (int) fm.stringWidth(tipString);
		}
		return textWidth;
	}

}

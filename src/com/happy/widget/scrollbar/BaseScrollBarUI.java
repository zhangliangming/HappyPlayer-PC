package com.happy.widget.scrollbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.happy.widget.button.ImageButton;

/**
 * 
 * @author zhangliangming
 * 
 */
public class BaseScrollBarUI extends BasicScrollBarUI {

	private ImageButton decreaseButton;
	private ImageButton increaseButton;

	private int width = 10, height = 1;

	private int alpha = 255;

	public BaseScrollBarUI(int alpha) {
		this.alpha = alpha;
		init();

		decreaseButton = new ImageButton(width, height,
				"vscrollbtn1normalimage1.png", "vscrollbtn1hotimage1.png",
				"vscrollbtn1pushedimage1.png");
		decreaseButton.setPreferredSize(new Dimension(width, height));

		increaseButton = new ImageButton(width, height,
				"vscrollbtn2normalimage1.png", "vscrollbtn2hotimage1.png",
				"vscrollbtn2pushedimage1.png");
		increaseButton.setPreferredSize(new Dimension(width, height));
	}

	private void init() {
		this.thumbColor = new Color(151, 148, 150, alpha);
		this.thumbHighlightColor = new Color(240, 240, 240, alpha);
		this.trackColor = new Color(255, 255, 255, 0);
		this.trackHighlightColor = new Color(255, 255, 255, 0);
	}

	public Dimension getMaximumSize(JComponent c) {
		return new Dimension(width, height);
	}

	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(width, height);
	}

	protected JButton createDecreaseButton(int orientation) {

		return decreaseButton;
	}

	protected JButton createIncreaseButton(int orientation) {
		return increaseButton;
	}

	protected Dimension getMaximumThumbSize() {
		Dimension di = super.getMaximumThumbSize();
		return new Dimension(di.width, di.height);
	}

	protected Dimension getMinimumThumbSize() {
		Dimension di = super.getMinimumThumbSize();
		return new Dimension(di.width, di.height);
	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		init();
		Graphics2D g2d = (Graphics2D) g;
		int arc = (int) (thumbBounds.getWidth() < thumbBounds.getHeight() ? thumbBounds
				.getWidth() / 3 : thumbBounds.getHeight() / 3);
		if (this.isThumbRollover())
			g2d.setColor(thumbDarkShadowColor);
		else
			g2d.setColor(thumbColor);
		g2d.translate(thumbBounds.x, thumbBounds.y);
		g2d.fillRoundRect(0, 0, (int) (thumbBounds.getWidth() - 1),
				(int) (thumbBounds.getHeight() - 1), arc, arc);
	}

	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		init();
		// super.paintTrack(g, c, trackBounds);
		Graphics2D g2d = (Graphics2D) g;

		if (this.isThumbRollover())
			g2d.setColor(trackHighlightColor);
		else
			g2d.setColor(trackColor);
		// g2d.translate(trackBounds.x, trackBounds.y);
		g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width - 1,
				trackBounds.height);
	}

	public void paint(Graphics g, JComponent c) {
		paintTrack(g, c, getTrackBounds());
		Rectangle thumbBounds = getThumbBounds();
		if (thumbBounds.intersects(g.getClipBounds())) {
			paintThumb(g, c, thumbBounds);
		}
		// super.paint(g, c);
	}

	protected void paintDecreaseHighlight(Graphics g) {
		super.paintDecreaseHighlight(g);
	}

	protected void paintIncreaseHighlight(Graphics g) {
		super.paintIncreaseHighlight(g);
	}
}
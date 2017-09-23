package com.happy.widget.panel.madetransliterationlrc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.util.FontsUtil;

/**
 * 原歌词内容面板
 * 
 * @author zhangliangming
 * 
 */
public class TransliterationLineComPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3285370418675829685L;

	private LyricsLineInfo lyricsLineInfo;
	private int width;
	private int height;

	/**
	 * 字体
	 */
	private Font baseFont;
	/**
	 * 歌词
	 */
	private String lrcComText = "";
	/**
	 * 歌词数组
	 */
	private String[] lrcComTexts;

	/**
	 * 歌词索引
	 */
	private int lrcIndex = -1;
	/**
	 * 歌词长度大小
	 */
	private int lrcSize = -1;

	public TransliterationLineComPanel(LyricsLineInfo lyricsLineInfo,
			int lWidth, int bHeight) {

		this.lyricsLineInfo = lyricsLineInfo;
		this.width = lWidth;
		this.height = bHeight;

		lrcComTexts = lyricsLineInfo.getLyricsWords();
		lrcSize = lrcComTexts.length;
		for (int i = 0; i < lrcSize; i++) {
			lrcComText += lrcComTexts[i];
		}

		baseFont = FontsUtil.getBaseFont((int) height / 2).deriveFont(
				Font.BOLD, (int) height / 2);

	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// 以达到边缘平滑的效果

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		g2d.setFont(baseFont);
		FontMetrics fm = g2d.getFontMetrics();
		int textWidth = 0;
		if (lrcIndex < 0) {
		} else {
			String temp = "";
			for (int i = 0; i < lrcComTexts.length; i++) {
				temp += lrcComTexts[i];
				if (i == lrcIndex) {
					break;
				}

			}
			//System.out.println("当前歌词长度：" + temp);
			
			Rectangle2D rc = fm.getStringBounds(temp, g2d);
			textWidth = (int) rc.getWidth();
		}

		int textHeight = fm.getHeight();

		int textX = 10;
		int heightY = (height + textHeight) / 2;

		// 这里不知为何还要减去fm.getDescent() + fm.getLeading() 绘画时才能把全文字绘画完整
		int clipY = heightY - textHeight + (fm.getDescent() + fm.getLeading());

		g2d.setPaint(Color.black);
		g2d.drawString(lrcComText, textX, heightY);

		g2d.setClip(textX, clipY, textWidth, textHeight);

		g2d.setPaint(Color.RED);
		g2d.drawString(lrcComText, textX, heightY);

	}

	/**
	 * 设置歌词的索引
	 * 
	 * @param lrcIndex
	 */
	public void setLrcIndex(int lrcIndex) {
		this.lrcIndex = lrcIndex;
		repaint();
	}

	/**
	 * 获取该行的歌曲数据
	 * 
	 * @return
	 */
	public LyricsLineInfo getKscLyricsLineInfo() {

		return null;
	}

	/**
	 * 重置
	 */
	public void reset() {
		lrcIndex = -1;
		repaint();
	}

}

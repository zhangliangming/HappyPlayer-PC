package com.happy.widget.panel.makelrc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JPanel;

import com.happy.common.Constants;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.model.LrcEventIntent;
import com.happy.observable.ObserverManage;
import com.happy.util.FontsUtil;

/**
 * 制作歌词的歌词内容面板
 * 
 * @author zhangliangming
 * 
 */
public class LrcComPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3285370418675829685L;

	private boolean isEnter = false;
	private boolean isSelect = false;
	private LyricsLineInfo lyricsLineInfo;
	private int width;
	private int height;
	/**
	 * 面板鼠标事件
	 */
	private PanelMouseListener panelMouseListener = new PanelMouseListener();

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

	private MakeLrcZhiZuoPanel makeLrcZhiZuoPanel;
	/**
	 * 统一字体路径
	 */
	String fontFilePath = Constants.PATH_FONTS + File.separator + "方正黑体简体.TTF";
	/**
	 * 内容面板
	 */
	private JPanel comPanel;
	/**
	 * 歌词索引
	 */
	private int lrcIndex = -2;
	/**
	 * 歌词长度大小
	 */
	private int lrcSize = -1;
	/**
	 * 
	 */
	private TreeMap<Integer, WordDisInterval> wordDisIntervals;

	/**
	 * 是否完成
	 */
	private boolean isFinish = false;
	/**
	 * 是否完全完成
	 */
	private boolean isComFinish = false;
	/**
	 * 歌词所在行数
	 */
	private int lyricsLineNum = -1;

	public LrcComPanel(int lyricsLineNum, JPanel comPanel,
			MakeLrcZhiZuoPanel makeLrcZhiZuoPanel,
			LyricsLineInfo lyricsLineInfo, int lWidth, int bHeight) {
		this.lyricsLineNum = lyricsLineNum;
		this.comPanel = comPanel;
		this.lyricsLineInfo = lyricsLineInfo;
		this.makeLrcZhiZuoPanel = makeLrcZhiZuoPanel;
		this.width = lWidth;
		this.height = bHeight;

		lrcComTexts = lyricsLineInfo.getLyricsWords();
		lrcSize = lrcComTexts.length;
		wordDisIntervals = new TreeMap<Integer, WordDisInterval>();
		for (int i = 0; i < lrcSize; i++) {
			lrcComText += lrcComTexts[i];
		}

		this.addMouseListener(panelMouseListener);
		baseFont = FontsUtil.getFontByFile(fontFilePath, Font.BOLD,
				(int) height / 2);
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
		if (isComFinish) {
			g2d.setColor(new Color(138, 1, 226));
			g2d.drawRect(0, 0, width, height);
		} else if (isSelect) {
			g2d.setColor(Color.red);

			g2d.drawRect(0, 0, width, height);
		} else if (isEnter) {
			g2d.setColor(Color.red);
			g2d.drawRect(0, 0, width, height);
		}

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
		if (isComFinish) {
			g2d.setPaint(new Color(138, 1, 226));
		} else {
			g2d.setPaint(Color.red);

		}
		g2d.drawString(lrcComText, textX, heightY);

	}

	/**
	 * 面板鼠标事件
	 * 
	 * @author zhangliangming
	 * 
	 */
	class PanelMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (isSelect) {
				return;
			}
			LrcEventIntent lrcEventIntent = new LrcEventIntent();
			lrcEventIntent.setLrcIndex(lyricsLineNum);
			ObserverManage.getObserver().setMessage(lrcEventIntent);
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			isEnter = true;
			comPanel.repaint();
			makeLrcZhiZuoPanel.repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			isEnter = false;
			comPanel.repaint();
			makeLrcZhiZuoPanel.repaint();
		}

	}

	/**
	 * 设置是否被选中或者完成
	 * 
	 * @param isSelect
	 */
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
		if (!isSelect) {
			if (!isComFinish)
				reset();
		}
		comPanel.repaint();
		makeLrcZhiZuoPanel.repaint();
	}

	/**
	 * 设置歌词的索引
	 * 
	 * @param lrcIndex
	 */
	public void setLrcIndex(int lrcIndex) {
		this.lrcIndex = lrcIndex;
	}

	/**
	 * 判断该行歌词是否完成
	 * 
	 * @return
	 */
	public boolean isFinish() {
		return isFinish;
	}

	/**
	 * 判断是否完全完成
	 * 
	 * @return
	 */
	public boolean isComFinish() {
		int index = lrcSize - 1;
		if (wordDisIntervals.containsKey(index)) {
			WordDisInterval wordDisInterval = wordDisIntervals.get(index);
			if (wordDisInterval != null) {
				if (wordDisInterval.getEndTime() != -1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 设置下一个歌词的索引
	 * 
	 * @param msec
	 *            当前时间
	 */
	public void setNextLrcIndex(int msec) {
		lrcIndex++;
		if (lrcIndex < 0) {
			return;
		}
		comPanel.repaint();
		makeLrcZhiZuoPanel.repaint();
		int preLrcIndex = lrcIndex - 1;
		if (wordDisIntervals.containsKey(preLrcIndex)) {
			WordDisInterval wordDisInterval = wordDisIntervals.get(preLrcIndex);
			wordDisInterval.setEndTime(msec);
			wordDisIntervals.put(preLrcIndex, wordDisInterval);
		}
		WordDisInterval wordDisInterval = new WordDisInterval();
		wordDisInterval.setStartTime(msec);
		wordDisIntervals.put(lrcIndex, wordDisInterval);
		if (lrcIndex == lrcSize - 1) {
			isFinish = true;
		}
	}

	/**
	 * 设置上一个歌词的索引
	 * 
	 * @param msec
	 */
	public void setPreLrcIndex(int msec) {
		isFinish = false;
		isComFinish = false;
		if (lrcIndex == -1 || lrcIndex == -2) {
			lrcIndex = 0;
		}
		lrcIndex--;
		comPanel.repaint();
		makeLrcZhiZuoPanel.repaint();
		int nextLrcIndex = lrcIndex + 1;
		if (wordDisIntervals.containsKey(nextLrcIndex)) {
			wordDisIntervals.remove(nextLrcIndex);
		}
		if (wordDisIntervals.containsKey(lrcIndex)) {
			WordDisInterval wordDisInterval = wordDisIntervals.get(lrcIndex);
			wordDisInterval.setEndTime(-1);
			wordDisIntervals.put(lrcIndex, wordDisInterval);
		}
	}

	/**
	 * 设置最后一个歌词的时间
	 * 
	 * @param msec
	 */
	public void setLastLrcIndex(int msec) {
		if (lrcIndex == lrcSize - 1) {
			int preLrcIndex = lrcIndex;
			if (wordDisIntervals.containsKey(preLrcIndex)) {
				WordDisInterval wordDisInterval = wordDisIntervals
						.get(preLrcIndex);
				wordDisInterval.setEndTime(msec);
				wordDisIntervals.put(preLrcIndex, wordDisInterval);
			}
			isComFinish = true;
			comPanel.repaint();
			makeLrcZhiZuoPanel.repaint();
		}
	}

	/**
	 * 获取该行的歌曲数据
	 * 
	 * @return
	 */
	public LyricsLineInfo getKscLyricsLineInfo() {
		if (isComFinish) {

			int startTime = 0;
			String startTimeStr = "";
			int endTime = 0;
			String endTimeStr = "";
			int[] wDisIntervals = new int[wordDisIntervals.size()];
			for (int j = 0; j < wordDisIntervals.size(); j++) {
				WordDisInterval wordDisInterval = wordDisIntervals.get(j);
				if (j == 0) {
					startTime = wordDisInterval.getStartTime();
					startTimeStr = getTimeByMesc(startTime);
				}
				if (j == wordDisIntervals.size() - 1) {
					endTime = wordDisInterval.getEndTime();
					endTimeStr = getTimeByMesc(endTime);
				}
				int time = wordDisInterval.getEndTime()
						- wordDisInterval.getStartTime();
				wDisIntervals[j] = time;
			}
			lyricsLineInfo.setStartTime(startTime);
			lyricsLineInfo.setEndTime(endTime);
			lyricsLineInfo.setWordsDisInterval(wDisIntervals);
			return lyricsLineInfo;
		}
		return null;
	}

	/**
	 * 重置
	 */
	public void reset() {
		lrcIndex = -1;
		isFinish = false;
		isComFinish = false;
		wordDisIntervals.clear();
		comPanel.repaint();
		makeLrcZhiZuoPanel.repaint();
	}

	/***
	 * 根据时间获取时间的字符串
	 * 
	 * @param mesc
	 * @return
	 */
	private String getTimeByMesc(int ms) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;
		return String.format("%02d:%02d.%03d", minute, second, milliSecond);
	}

	/**
	 * 单个歌词实体类
	 * 
	 * @author zhangliangming
	 * 
	 */
	class WordDisInterval {
		/**
		 * 开始时间
		 */
		int startTime = -1;
		/**
		 * 结束时间
		 */
		int endTime = -1;

		public int getStartTime() {
			return startTime;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public int getEndTime() {
			return endTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}

	}

}

package com.happy.widget.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.model.MessageIntent;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.FontsUtil;
import com.happy.util.LyricsParserUtil;

/**
 * 多行歌词面板
 * 
 * @author zhangliangming
 * 
 */
public class ManyLineLyricsView extends JPanel implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3285370418675829685L;

	private int width = 0;

	/**
	 * 视图高度
	 */
	private int height = 0;
	/**
	 * 歌词进入渐变透明度的区域高度大小，默认取视图高度的1/4
	 */
	private int shadeHeight = 0;
	/**
	 * 渐变最大透明度
	 */
	private int maxAlphaValue = 255;
	/**
	 * 渐变最小透明度
	 */
	private int minAlphaValue = 100;
	/**
	 * 渐变梯度值
	 */
	private int dAlphaValue = 0;

	/**
	 * 是否有歌词
	 */
	private boolean hasLrc = false;
	/**
	 * 是否画时间线
	 * 
	 * **/
	private boolean mIsDrawTimeLine = false;

	/**
	 * 显示放大缩小的歌词文字的大小值
	 */
	private int SCALEIZEWORDDEF = 15;
	/**
	 * 默认字体大小
	 */
	private int SIZEWORD = SCALEIZEWORDDEF;
	/**
	 * 高亮字体大小
	 */
	private int SIZEWORDHL = 0;

	/**
	 * 歌词每行的间隔
	 */
	private int INTERVAL = 30;
	/**
	 * 歌词解析
	 */
	private LyricsParserUtil lyricsParser;

	/**
	 * 歌词列表
	 */
	private TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap;

	/**
	 * 当前歌词的所在行数
	 */
	private int lyricsLineNum = -1;
	/**
	 * 上一行歌词,方便缩小字体
	 */
	private int oldLyricsLineNum = -1;
	/**
	 * 当前歌词的第几个字
	 */
	private int lyricsWordIndex = -1;

	/**
	 * 当前歌词第几个字 已经播放的时间
	 */
	private int lyricsWordHLEDTime = 0;

	/**
	 * 当前歌词第几个字 已经播放的长度
	 */
	private float lineLyricsHLWidth = 0;

	/** 高亮歌词当前的其实x轴绘制坐标 **/
	private float highLightLrcMoveX;

	/** 控制文字缩放的因子 **/
	private float mCurFraction = 1.0f;

	/**
	 * 歌词在Y轴上的偏移量
	 */
	private float offsetY = 0;
	/**
	 * 歌词在Y轴上的上一次偏移量
	 */
	private float oldOffsetY = 0;

	/**
	 * 歌词滑动的最大进度
	 */
	private int scrollMaxYProgress;
	/**
	 * 是否正在滑动
	 */
	private boolean blScroll = false;
	/***
	 * 播放进度
	 */
	private int progress = 0;

	/**
	 * 字体大小缩放比例
	 */
	private int fontSizeScale = 0;
	/**
	 * 字体大小缩放比例
	 */
	private int oldFontSizeScale = 0;

	/**
	 * 默认高亮未读画笔
	 */
	private Color paintHLDEF;
	/**
	 * 高亮已读画笔
	 */
	private Color paintHLED;
	/**
	 * 歌词滚动监听器
	 */
	private LrcScrollListener lrcScrollListener;

	/**
	 * 鼠标右键事件
	 */
	private MetaDownListener metaDownListener;

	/**
	 * 基本字体
	 */
	private Font baseFont;

	public ManyLineLyricsView(int width, int height, boolean hasLrcEvent) {
		this.width = width;
		this.height = height;

		shadeHeight = height / 4;
		//
		dAlphaValue = shadeHeight / (maxAlphaValue - minAlphaValue);
		baseFont = FontsUtil.getBaseFont(BaseData.appFontSize);

		initSizeWord();
		initColor();
		ObserverManage.getObserver().addObserver(this);
		this.setOpaque(false);
		if (hasLrcEvent) {
			lrcScrollListener = new LrcScrollListener();
			this.addMouseListener(lrcScrollListener);
			this.addMouseMotionListener(lrcScrollListener);
		}

	}

	/***
	 * 被始化颜色
	 */
	private void initColor() {
		paintHLDEF = Color.white;
		paintHLED = BaseData.lrcColorStr[BaseData.lrcColorIndex];

	}

	/**
	 * 初始化字体大小
	 */
	private void initSizeWord() {
		// String tip = Constants.APPTIPTITLE;
		// Constants.lrcFontSize = width / tip.length();
		// Constants.lrcFontMinSize = width / tip.length();
		// Constants.lrcFontMaxSize = width / tip.length() + 20;
		fontSizeScale = BaseData.lrcFontSize + 100;
		SCALEIZEWORDDEF = (int) ((float) fontSizeScale / 100 * SIZEWORD);
		SIZEWORDHL = SCALEIZEWORDDEF + 10;

	}

	// 绘制组件
	public void paintComponent(Graphics g) {
		// long begin = System.currentTimeMillis();
		Graphics2D g2d = (Graphics2D) g;
		// 以达到边缘平滑的效果

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		// 画时间线和时间线
		if (mIsDrawTimeLine) {
			drawTimeLine(g2d);
		}

		if (!hasLrc) {
			drawDefText(g2d);
		} else {
			drawLrcText(g2d);
		}
		// long time = System.currentTimeMillis() - begin;
		// System.out.println("窗口耗时" + time);
	}

	/**
	 * 画时间线
	 * 
	 * @param g2d
	 */
	private void drawTimeLine(Graphics2D g2d) {
		g2d.setFont(baseFont.deriveFont( Font.BOLD, SIZEWORDHL));
		String timeStr = TimeUtils.parseString(progress);

		FontMetrics fm = g2d.getFontMetrics();
		int textHeight = fm.getHeight();
		float y = height / 2;
		g2d.setPaint(paintHLED);
		g2d.drawString(timeStr, 0, y + textHeight);
		g2d.setPaint(Color.WHITE);
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.drawLine(0, (int) y, width, (int) y);
	}

	/**
	 * 画默认提示
	 * 
	 * @param g2d
	 */
	private void drawDefText(Graphics2D g2d) {
		String tip = Constants.APPTIPTITLE;
		g2d.setFont(baseFont.deriveFont( Font.BOLD, SIZEWORDHL));
		g2d.setPaint(paintHLDEF);

		FontMetrics fm = g2d.getFontMetrics();
		Rectangle2D rc = fm.getStringBounds(tip, g2d);

		int textWidth = (int) rc.getWidth();
		int textHeight = fm.getHeight();

		int leftX = (int) ((width - textWidth) / 2);
		int heightY = (height + textHeight) / 2;

		// 这里不知为何还要减去fm.getDescent() + fm.getLeading() 绘画时才能把全文字绘画完整
		int clipY = heightY - textHeight + (fm.getDescent() + fm.getLeading());

		g2d.drawString(tip, leftX, heightY);

		g2d.setClip(leftX, clipY, textWidth / 2, textHeight);

		g2d.setPaint(paintHLED);
		g2d.drawString(tip, leftX, heightY);

	}

	/**
	 * 绘画歌词
	 * 
	 * @param g2d
	 */
	private void drawLrcText(Graphics2D g2d) {
		// 画当前歌词之前的歌词
		for (int i = lyricsLineNum - 1; i >= 0; i--) {
			if (offsetY + (SCALEIZEWORDDEF + INTERVAL) * i < (SCALEIZEWORDDEF + SCALEIZEWORDDEF / 2)) {
				break;
			}

			if (i == oldLyricsLineNum) {
				// 因为有缩放效果，有需要动态设置歌词的字体大小
				float textSize = SIZEWORDHL - (SIZEWORDHL - SCALEIZEWORDDEF)
						* mCurFraction;
				g2d.setFont(baseFont.deriveFont( Font.BOLD, (int) textSize));
			} else {// 画其他的歌词
				g2d.setFont(baseFont.deriveFont( Font.BOLD, (int) SCALEIZEWORDDEF));
			}

			String text = lyricsLineTreeMap.get(i).getLineLyrics();

			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D rc = fm.getStringBounds(text, g2d);

			int textWidth = (int) rc.getWidth();
			float textX = (width - textWidth) / 2;

			// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
			textX = Math.max(textX, 10);

			//
			if (offsetY + (SCALEIZEWORDDEF + INTERVAL) * i < shadeHeight) {
				// 进入了渐变区域
				int alphaValue = (shadeHeight - (int) (offsetY + (SCALEIZEWORDDEF + INTERVAL)
						* i))
						* dAlphaValue;
				g2d.setPaint(new Color(255, 255, 255, 255 - alphaValue));
			} else {
				g2d.setPaint(new Color(255, 255, 255, 255));
			}

			g2d.drawString(text, textX, offsetY + (SCALEIZEWORDDEF + INTERVAL)
					* i);

		}

		// 画当前歌词之后的歌词
		for (int i = lyricsLineNum + 1; i < lyricsLineTreeMap.size(); i++) {
			if (offsetY + (SCALEIZEWORDDEF + INTERVAL) * i > height
					- (SCALEIZEWORDDEF + SCALEIZEWORDDEF / 2)) {
				break;
			}

			if (i == oldLyricsLineNum) {
				// 因为有缩放效果，有需要动态设置歌词的字体大小
				float textSize = SIZEWORDHL - (SIZEWORDHL - SCALEIZEWORDDEF)
						* mCurFraction;
				g2d.setFont(baseFont.deriveFont( Font.BOLD, (int) textSize));
			} else {// 画其他的歌词
				g2d.setFont(baseFont.deriveFont( Font.BOLD, (int) SCALEIZEWORDDEF));
			}

			String text = lyricsLineTreeMap.get(i).getLineLyrics();

			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D rc = fm.getStringBounds(text, g2d);

			int textWidth = (int) rc.getWidth();
			float textX = (width - textWidth) / 2;

			// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
			textX = Math.max(textX, 10);

			if (offsetY + (SCALEIZEWORDDEF + INTERVAL) * i > (height - shadeHeight)) {
				// 进入了渐变区域
				int alphaValue = ((int) (offsetY + (SCALEIZEWORDDEF + INTERVAL)
						* i) - (height - shadeHeight))
						* dAlphaValue;
				g2d.setPaint(new Color(255, 255, 255, 255 - alphaValue));
			} else {
				g2d.setPaint(new Color(255, 255, 255, 255));
			}

			g2d.drawString(text, textX, offsetY + (SCALEIZEWORDDEF + INTERVAL)
					* i);

		}

		// 画当前高亮的歌词行
		if (lyricsLineNum != -1) {

			// 因为有缩放效果，有需要动态设置歌词的字体大小
			float textSize = SCALEIZEWORDDEF + (SIZEWORDHL - SCALEIZEWORDDEF)
					* mCurFraction;

			g2d.setFont(baseFont.deriveFont( Font.BOLD, (int) textSize));

			LyricsLineInfo kscLyricsLineInfo = lyricsLineTreeMap
					.get(lyricsLineNum);
			// 整行歌词
			String lineLyrics = kscLyricsLineInfo.getLineLyrics();
			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D rc = fm.getStringBounds(lineLyrics, g2d);
			float lineLyricsWidth = (int) rc.getWidth();

			// ktv歌词
			if (lyricsWordIndex == -1) {
				lineLyricsHLWidth = lineLyricsWidth;
			} else {
				String lyricsWords[] = kscLyricsLineInfo.getLyricsWords();
				int wordsDisInterval[] = kscLyricsLineInfo
						.getWordsDisInterval();
				// 当前歌词之前的歌词
				String lyricsBeforeWord = "";
				for (int i = 0; i < lyricsWordIndex; i++) {
					lyricsBeforeWord += lyricsWords[i];
				}
				// 当前歌词
				String lyricsNowWord = lyricsWords[lyricsWordIndex].trim();// 去掉空格

				Rectangle2D rcBeforeWord = fm.getStringBounds(lyricsBeforeWord,
						g2d);
				// 当前歌词之前的歌词长度
				float lyricsBeforeWordWidth = (float) rcBeforeWord.getWidth();

				Rectangle2D rcNowWord = fm.getStringBounds(lyricsNowWord, g2d);
				// 当前歌词长度
				float lyricsNowWordWidth = (float) rcNowWord.getWidth();

				float len = lyricsNowWordWidth
						/ wordsDisInterval[lyricsWordIndex]
						* lyricsWordHLEDTime;
				lineLyricsHLWidth = lyricsBeforeWordWidth + len;
			}

			float textX = 0;
			if (lineLyricsWidth > width) {
				if (lineLyricsHLWidth >= width / 2) {
					if ((lineLyricsWidth - lineLyricsHLWidth) >= width / 2) {
						highLightLrcMoveX = (width / 2 - lineLyricsHLWidth);
					} else {
						highLightLrcMoveX = width - lineLyricsWidth - 10;
					}
				} else {
					highLightLrcMoveX = 10;
				}
				// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
				textX = highLightLrcMoveX;
			} else {
				// 如果歌词宽度小于view的宽
				textX = (width - lineLyricsWidth) / 2;
			}

			g2d.setPaint(paintHLDEF);

			// 画当前歌词
			g2d.drawString(lineLyrics, textX, offsetY
					+ (SCALEIZEWORDDEF + INTERVAL) * lyricsLineNum);

			// ktv过渡效果

			int height = fm.getHeight();

			// 这里不知为何还要减去fm.getDescent() + fm.getLeading() 绘画时才能把全文字绘画完整
			int clipY = (int) (offsetY + (SCALEIZEWORDDEF + INTERVAL)
					* lyricsLineNum - height + (fm.getDescent() + fm
					.getLeading()));

			g2d.setClip((int) textX, clipY, (int) lineLyricsHLWidth, height);

			g2d.setPaint(paintHLED);
			// 画当前歌词
			g2d.drawString(lineLyrics, textX, offsetY
					+ (SCALEIZEWORDDEF + INTERVAL) * lyricsLineNum);

		}
	}

	public boolean getBlScroll() {
		return blScroll;
	}

	public boolean getHasLrc() {
		return hasLrc;
	}

	public void setHasLrc(boolean hasLrc) {
		this.hasLrc = hasLrc;
		// if (hasLrc && !isRemoveListener) {
		// this.addMouseListener(lrcScrollListener);
		// this.addMouseMotionListener(lrcScrollListener);
		// } else {
		// this.removeMouseListener(lrcScrollListener);
		// this.removeMouseMotionListener(lrcScrollListener);
		// }

		repaint();
	}

	private Toolkit tk = Toolkit.getDefaultToolkit();

	private Cursor draggedCursor = null;

	private Cursor pressedCursor = null;

	/**
	 * 当触摸歌词View时，保存为当前触点的Y轴坐标
	 * 
	 * 滑动的进度
	 */
	private float touchY = 0;

	private class LrcScrollListener implements MouseInputListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {

			if (e.isMetaDown()) {// 检测鼠标右键单击
				if (metaDownListener != null) {
					metaDownListener.MetaDown(e);
				}
			} else {

				if (!hasLrc)
					return;

				if (draggedCursor == null) {
					String imagePath = Constants.PATH_ICON + File.separator
							+ "cursor_drag.png";
					Image image = new ImageIcon(imagePath).getImage();
					draggedCursor = tk.createCustomCursor(image, new Point(10,
							10), "norm");
				}
				// 设置鼠标的图标
				setCursor(draggedCursor);

				float tt = e.getY();
				touchY = tt;
				mIsDrawTimeLine = true;

				repaint();

			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isMetaDown()) {// 检测鼠标右键单击

			} else {
				if (!hasLrc)
					return;

				if (pressedCursor == null) {
					String imagePath = Constants.PATH_ICON + File.separator
							+ "cursor_pressed.png";
					Image image = new ImageIcon(imagePath).getImage();
					pressedCursor = tk.createCustomCursor(image, new Point(10,
							10), "norm");
				}
				// 设置鼠标的图标
				setCursor(pressedCursor);

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.SEEKTOMUSIC);
				songMessage.setProgress(progress);
				ObserverManage.getObserver().setMessage(songMessage);

				mIsDrawTimeLine = false;

				//
				float tt = e.getY();
				touchY = tt;

				repaint();

				// 延迟刷新歌词，避免歌词出现闪烁
				new Thread() {

					@Override
					public void run() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						blScroll = false;
					}

				}.start();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {

			if (pressedCursor == null) {
				String imagePath = Constants.PATH_ICON + File.separator
						+ "cursor_pressed.png";
				Image image = new ImageIcon(imagePath).getImage();
				pressedCursor = tk.createCustomCursor(image, new Point(10, 10),
						"norm");
			}
			// 设置鼠标的图标
			setCursor(pressedCursor);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setCursor(null);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.isMetaDown()) {// 检测鼠标右键单击

			} else {
				if (!hasLrc)
					return;

				if (draggedCursor == null) {
					String imagePath = Constants.PATH_ICON + File.separator
							+ "cursor_drag.ico";
					Image image = new ImageIcon(imagePath).getImage();
					draggedCursor = tk.createCustomCursor(image, new Point(10,
							10), "norm");
				}
				// 设置鼠标的图标
				setCursor(draggedCursor);

				float tt = e.getY();
				blScroll = true;

				touchY = tt - touchY;

				// 每滑动1单位，当前进度加100ms

				progress = (int) (progress - touchY * 100);
				if (progress < 0) {
					progress = 0;
				}
				if (progress > scrollMaxYProgress) {
					progress = scrollMaxYProgress;
				}

				showLrc(progress);
				touchY = tt;

				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}

	}

	@Override
	public void update(Observable arg0, final Object data) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				if (data instanceof MessageIntent) {
					MessageIntent messageIntent = (MessageIntent) data;
					if (messageIntent.getAction().equals(
							MessageIntent.MANYLINELRCCOLOR)) {
						initColor();
						repaint();
					} else if (messageIntent.getAction().equals(
							MessageIntent.MANYLINEFONTSIZE)) {
						//
						initSizeWord();
						showLrc(progress);
						repaint();
					}
				}
			}
		});
	}

	/**
	 * * @param scrollMaxYProgress 最大滑动进度
	 * 
	 * @param lyricsLineTreeMap
	 */
	public void init(int scrollMaxYProgress, LyricsParserUtil lyricsParser) {

		this.scrollMaxYProgress = scrollMaxYProgress;
		this.lyricsParser = lyricsParser;
		lyricsLineTreeMap = lyricsParser.getLyricsLineTreeMap();
		offsetY = height / 2 + (SCALEIZEWORDDEF + INTERVAL);
		oldLyricsLineNum = -1;
		lyricsLineNum = -1;
		lyricsWordIndex = -1;
		lineLyricsHLWidth = 0;
		lyricsWordHLEDTime = 0;
		highLightLrcMoveX = 0;
		mCurFraction = 1.0f;

		repaint();
	}

	/**
	 * 
	 * @param playProgress
	 */
	public void showLrc(int playProgress) {
		// System.out.println("playProgress = " + playProgress);
		// 非滑动情况下，保存当前的播放进度
		if (!blScroll) {
			// System.out.println("scrollPlayProgress = " + progress);
			this.progress = playProgress;
		}
		if (lyricsParser == null)
			return;
		int newLyricsLineNum = lyricsParser
				.getLineNumberFromCurPlayingTime(playProgress);
		if (newLyricsLineNum == -1) {
			offsetY = height / 2 + (SCALEIZEWORDDEF + INTERVAL);
			// 进度为0，初始化相关的数据
			lyricsLineNum = -1;
			lyricsWordIndex = -1;
			lineLyricsHLWidth = 0;
			lyricsWordHLEDTime = 0;
			highLightLrcMoveX = 0;
			mCurFraction = 1.0f;
		} else {
			// 往上下移动的总距离,字体大小改变后，要修改oldOffsetY的位置
			int sy = (SCALEIZEWORDDEF + INTERVAL);
			if (lyricsLineNum != newLyricsLineNum
					|| oldFontSizeScale != fontSizeScale) {
				lyricsLineNum = newLyricsLineNum;
				oldOffsetY = height / 2 - (SCALEIZEWORDDEF + INTERVAL)
						* lyricsLineNum + sy;
			}
			// 另一行歌词，所以把之前设置的高亮移动显示的x坐标设置为0
			highLightLrcMoveX = 0;
			// 每次view刷新时移动往上下移动的距离,设置时间，就会有动画的效果
			float dy = lyricsParser.getOffsetDYFromCurPlayingTime(
					lyricsLineNum, playProgress, sy);
			offsetY = oldOffsetY - dy;

			if (newLyricsLineNum > lyricsLineNum) {
				oldLyricsLineNum = newLyricsLineNum + 1;
			} else {
				oldLyricsLineNum = newLyricsLineNum - 1;
			}
			mCurFraction = dy / sy;
		}

		lyricsWordIndex = lyricsParser.getDisWordsIndexFromCurPlayingTime(
				lyricsLineNum, playProgress);

		lyricsWordHLEDTime = lyricsParser.getLenFromCurPlayingTime(
				lyricsLineNum, playProgress);

		if (!blScroll && oldFontSizeScale == fontSizeScale) {
			// 字体在切换时，不进行刷新，免得会出现闪屏的问题
			repaint();
		}
		if (oldFontSizeScale != fontSizeScale) {
			oldFontSizeScale = fontSizeScale;
		}
	}

	public void setWidth(int width) {
		this.width = width;
		repaint();
	}

	public void setMetaDownListener(MetaDownListener metaDownListener) {
		this.metaDownListener = metaDownListener;
	}

	/**
	 * 右键事件
	 * 
	 * @author zhangliangming
	 * 
	 */
	public interface MetaDownListener {
		public void MetaDown(MouseEvent e);
	}
}

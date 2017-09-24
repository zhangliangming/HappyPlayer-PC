package com.happy.widget.panel.lrc;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.util.FontsUtil;
import com.happy.util.LyricsUtil;

/**
 * 桌面双行歌词:支持翻译歌词和音译歌词。因为之前一直都是用Graphics2D和clip来实现动感歌词效果，
 * 但是在翻译歌词和音译歌词上面，再用clip来实现是比较麻烦的，所以，这里使用image来实现
 * 动感歌词效果；其实image内部也是使用Graphics2D和clip来实现动感歌词效果，并生成一个文本图片，
 * 然后再把该图片绘画到视图上面，从而实现动感歌词效果。
 * 
 * @author zhangliangming
 * 
 */
public class FloatLyricsView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 默认提示文本
	 */
	private String mDefText;

	/**
	 * 默认高亮未读画笔
	 */
	private GradientPaint mPaint;
	/**
	 * 高亮已读画笔
	 */
	private GradientPaint mPaintHL;

	/**
	 * 空行高度
	 */
	private int mSpaceLineHeight = 0;
	/**
	 * 歌词字体大小
	 */
	private int mFontSize = 0;

	/**
	 * 歌词解析
	 */
	private LyricsUtil mLyricsUtil;

	/**
	 * 歌词列表
	 */
	private TreeMap<Integer, LyricsLineInfo> mLyricsLineTreeMap;
	/**
	 * 翻译行歌词列表
	 */
	private List<LyricsLineInfo> mTranslateLrcLineInfos;
	/**
	 * 音译歌词行
	 */
	private List<LyricsLineInfo> mTransliterationLrcLineInfos;
	/**
	 * 当前歌词的所在行数
	 */
	private int mLyricsLineNum = 0;

	/**
	 * 当前歌词的第几个字
	 */
	private int mLyricsWordIndex = -1;

	/**
	 * 当前歌词第几个字 已经播放的时间
	 */
	private float mLyricsWordHLTime = 0;

	/***
	 * 播放进度
	 */
	private int mProgress = 0;

	/**
	 * 额外歌词监听事件
	 */
	private ExtraLyricsListener mExtraLyricsListener;
	// ////////////////////////////////////翻译和音译歌词变量//////////////////////////////////////////////

	public static final int SHOWTRANSLATELRC = 0;
	public static final int SHOWTRANSLITERATIONLRC = 1;
	public static final int NOSHOWEXTRALRC = 2;
	/**
	 * 额外歌词状态
	 */
	private int mExtraLrcStatus = NOSHOWEXTRALRC;

	/**
	 * 当前音译歌词的所在行数
	 */
	private int mExtraLyricsLineNum = 0;

	/**
	 * 当前音译歌词的第几个字
	 */
	private int mExtraLyricsWordIndex = -1;

	/**
	 * 额外当前歌词第几个字 已经播放的时间
	 */
	private float mExtraLyricsWordHLTime = 0;

	/**
	 * 判断歌词集合是否在重构
	 */
	private boolean isReconstruct = false;

	// ///////////////////////////////////////////////////

	// 统一字体
	String mFontFilePath = Constants.PATH_FONTS + File.separator
			+ "Arial-Unicode-Bold.ttf";
	/**
	 * 字体
	 */
	private Font mBaseFont;
	// //////////////////////////////////////////////////////

	private int mWidth = 0;
	private int mHeight = 0;

	private MouseListener mMouseListener = new MouseListener();
	/**
	 * 判断是否进入
	 */
	private boolean isEnter = false;
	/**
	 * 是否显示
	 */
	private boolean isShow = false;
	/**
	 * 桌面窗口事件
	 */
	private MouseInputListener desLrcDialogMouseListener;

	public FloatLyricsView(int width, int height,
			MouseInputListener desLrcDialogMouseListener) {
		this.desLrcDialogMouseListener = desLrcDialogMouseListener;
		this.mWidth = width;
		this.mHeight = height;
		initSizeWord();
		this.setOpaque(false);

		init();
		initLockEvent();
	}

	private void init() {
		mDefText = Constants.APPTIPTITLE;
	}

	/**
	 * 初始化字体高度
	 */
	private void initSizeWord() {
		mFontSize = BaseData.desktopLrcFontSize;
		mBaseFont = FontsUtil
				.getFontByFile(mFontFilePath, Font.BOLD, mFontSize);
	}

	/**
	 * 初始化默认字体的渐变颜色
	 * 
	 */
	private void initPaintHLDEFColor(float x, float y, int height) {
		mPaint = new GradientPaint(x, y - height / 2,
				BaseData.DESLRCNOREADCOLORFRIST[BaseData.desktopLrcIndex], x, y
						+ height,
				BaseData.DESLRCNOREADCOLORSECOND[BaseData.desktopLrcIndex],
				true);
	}

	/**
	 * 初始化高亮字体的渐变颜色
	 * 
	 */
	private void initPaintHLEDColor(float x, float y, int height) {

		mPaintHL = new GradientPaint(x, y - height / 2,
				BaseData.DESLRCREADEDCOLORFRIST[BaseData.desktopLrcIndex], x, y
						+ height,
				BaseData.DESLRCREADEDCOLORSECOND[BaseData.desktopLrcIndex],
				true);
	}

	/**
	 *
	 */
	protected void initLockEvent() {
		this.addMouseListener(mMouseListener);
		this.addMouseMotionListener(mMouseListener);
	}

	// 绘制组件
	public void paint(Graphics g) {

		// long begin = System.currentTimeMillis();
		Graphics2D g2d = (Graphics2D) g;
		// 以达到边缘平滑的效果

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		//
		g2d.setFont(mBaseFont);

		if (isEnter || isShow) {
			g2d.setPaint(new Color(0, 0, 0, 150));
			g2d.fillRect(0, 0, mWidth, mHeight);
		}

		if (mLyricsLineTreeMap == null || mLyricsLineTreeMap.size() == 0) {
			drawDefText(g2d);
		} else {
			// 计算空行高度
			mSpaceLineHeight = (mHeight - 2 * getTextHeight(g2d)) / 3;

			// 判断是否需要绘画额外歌词
			if (mExtraLrcStatus == SHOWTRANSLATELRC
					|| mExtraLrcStatus == SHOWTRANSLITERATIONLRC) {

				List<LyricsLineInfo> extraLrcList = null;
				if (mExtraLrcStatus == SHOWTRANSLATELRC) {
					extraLrcList = mTranslateLrcLineInfos;
				} else {
					extraLrcList = mTransliterationLrcLineInfos;
				}

				// 绘画额外歌词
				drawExtraLrcText(g2d, extraLrcList);

			} else {
				// 绘画默认双行歌词
				drawTwoLrcText(g2d);
			}
		}

		super.paint(g2d);

	}

	/**
	 * 绘画额外歌词
	 * 
	 * @param g2d
	 * @param extraLrcList
	 *            额外歌词集合
	 */
	private void drawExtraLrcText(Graphics2D g2d,
			List<LyricsLineInfo> extraLrcList) {

		LyricsLineInfo lyricsLineInfo = mLyricsLineTreeMap.get(mLyricsLineNum);
		int textY = getLineHeight(g2d);
		// 画当前行歌词
		drawDGLrcText(g2d, lyricsLineInfo, mPaint, mPaintHL, textY,
				mLyricsWordIndex, mLyricsWordHLTime);

		// 画额外歌词
		LyricsLineInfo extraLyricsLineInfo = extraLrcList
				.get(mExtraLyricsLineNum);
		textY = getLineHeight(g2d) * 2;
		drawDGLrcText(g2d, extraLyricsLineInfo, mPaint, mPaintHL, textY,
				mExtraLyricsWordIndex, mExtraLyricsWordHLTime);
	}

	/**
	 * 画动感歌词行
	 * 
	 * @param g2d
	 * 
	 * @param lyricsLineInfo
	 * @param mPaint
	 * @param mPaintHL
	 * @param textY
	 * @param mLyricsWordIndex
	 * @param mLyricsWordHLTime
	 */
	private void drawDGLrcText(Graphics2D g2d, LyricsLineInfo lyricsLineInfo,
			GradientPaint mPaint, GradientPaint mPaintHL, int textY,
			int mLyricsWordIndex, float mLyricsWordHLTime) {
		// 当行歌词
		String currentLyrics = lyricsLineInfo.getLineLyrics();
		int currentTextWidth = getTextWidth(g2d, currentLyrics);
		float lineLyricsHLWidth = 0;
		if (mLyricsWordIndex != -1) {
			String lyricsWords[] = lyricsLineInfo.getLyricsWords();
			int wordsDisInterval[] = lyricsLineInfo.getWordsDisInterval();
			// 当前歌词之前的歌词
			String lyricsBeforeWord = "";
			for (int i = 0; i < mLyricsWordIndex; i++) {
				lyricsBeforeWord += lyricsWords[i];
			}
			// 当前歌词
			String lyricsNowWord = lyricsWords[mLyricsWordIndex].trim();// 去掉空格

			// 当前歌词之前的歌词长度
			int lyricsBeforeWordWidth = getTextWidth(g2d, lyricsBeforeWord);

			// 当前歌词长度
			float lyricsNowWordWidth = getTextWidth(g2d, lyricsNowWord);

			float len = lyricsNowWordWidth / wordsDisInterval[mLyricsWordIndex]
					* mLyricsWordHLTime;
			lineLyricsHLWidth = lyricsBeforeWordWidth + len;
		} else {
			// 整行歌词
			lineLyricsHLWidth = currentTextWidth;
		}

		// 当前歌词行的x坐标
		float textX = 0;
		float lrcTextMoveX = 0;
		if (currentTextWidth > mWidth) {
			if (lineLyricsHLWidth >= mWidth) {
				if ((currentTextWidth - lineLyricsHLWidth) >= mWidth) {
					lrcTextMoveX = (mWidth - lineLyricsHLWidth);
				} else {
					lrcTextMoveX = mWidth - currentTextWidth
							- (mWidth - currentTextWidth) / 2;
				}
			} else {
				lrcTextMoveX = (mWidth - currentTextWidth) / 2;
			}
			// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
			textX = lrcTextMoveX;
		} else {
			// 如果歌词宽度小于view的宽
			textX = (mWidth - currentTextWidth) / 2;
		}

		drawDGBufferedImage(g2d, textX, textY, currentLyrics, lineLyricsHLWidth);

	}

	/**
	 * 绘画双行歌词
	 * 
	 * @param g2d
	 */
	private void drawTwoLrcText(Graphics2D g2d) {

		// 先设置当前歌词，之后再根据索引判断是否放在左边还是右边

		LyricsLineInfo lyricsLineInfo = mLyricsLineTreeMap.get(mLyricsLineNum);
		// 当行歌词
		String currentLyrics = lyricsLineInfo.getLineLyrics();

		int currentTextWidth = getTextWidth(g2d, currentLyrics);
		float lineLyricsHLWidth = 0;

		if (mLyricsWordIndex != -1) {

			String lyricsWords[] = lyricsLineInfo.getLyricsWords();
			int wordsDisInterval[] = lyricsLineInfo.getWordsDisInterval();
			// 当前歌词之前的歌词
			String lyricsBeforeWord = "";
			for (int i = 0; i < mLyricsWordIndex; i++) {
				lyricsBeforeWord += lyricsWords[i];
			}
			// 当前歌词
			String lyricsNowWord = lyricsWords[mLyricsWordIndex].trim();// 去掉空格

			// 当前歌词之前的歌词长度
			int lyricsBeforeWordWidth = getTextWidth(g2d, lyricsBeforeWord);

			// 当前歌词长度
			float lyricsNowWordWidth = getTextWidth(g2d, lyricsNowWord);

			float len = lyricsNowWordWidth / wordsDisInterval[mLyricsWordIndex]
					* mLyricsWordHLTime;
			lineLyricsHLWidth = lyricsBeforeWordWidth + len;
		} else {
			// 整行歌词
			lineLyricsHLWidth = currentTextWidth;
		}
		// 当前歌词行的x坐标
		float textX = 0;
		float lrcTextMoveX = 0;
		// 当前歌词行的y坐标
		float textY = 0;
		if (mLyricsLineNum % 2 == 0) {

			if (currentTextWidth > mWidth) {
				if (lineLyricsHLWidth >= mWidth) {
					if ((currentTextWidth - lineLyricsHLWidth) >= mWidth) {
						lrcTextMoveX = (mWidth - lineLyricsHLWidth);
					} else {
						lrcTextMoveX = mWidth - currentTextWidth - 10;
					}
				} else {
					lrcTextMoveX = 10;
				}
				// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
				textX = lrcTextMoveX;
			} else {
				// 如果歌词宽度小于view的宽
				textX = 10;
			}

			// 画下一句的歌词
			if (mLyricsLineNum + 1 < mLyricsLineTreeMap.size()) {
				String lyricsRight = mLyricsLineTreeMap.get(mLyricsLineNum + 1)
						.getLineLyrics();

				int lyricsRightWidth = getTextWidth(g2d, lyricsRight);
				float textRightX = mWidth - lyricsRightWidth - 10;
				// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
				textRightX = Math.max(textRightX, 10);
				drawBackground(g2d, lyricsRight, textRightX,
						getLineHeight(g2d) * 2);

				int textHeight = getTextHeight(g2d);
				initPaintHLDEFColor(textRightX, getLineHeight(g2d) * 2,
						textHeight);
				g2d.setPaint(mPaint);
				g2d.drawString(lyricsRight, textRightX, getLineHeight(g2d) * 2);
			}

			textY = getLineHeight(g2d);

		} else {

			if (currentTextWidth > mWidth) {
				if (lineLyricsHLWidth >= mWidth) {
					if ((currentTextWidth - lineLyricsHLWidth) >= mWidth) {
						lrcTextMoveX = (mWidth - lineLyricsHLWidth);
					} else {
						lrcTextMoveX = mWidth - currentTextWidth - 10;
					}
				} else {
					lrcTextMoveX = 10;
				}
				// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
				textX = lrcTextMoveX;
			} else {
				// 如果歌词宽度小于view的宽
				textX = mWidth - currentTextWidth - 10;
			}

			// 画下一句的歌词
			if (mLyricsLineNum + 1 != mLyricsLineTreeMap.size()) {
				String lyricsLeft = mLyricsLineTreeMap.get(mLyricsLineNum + 1)
						.getLineLyrics();

				drawBackground(g2d, lyricsLeft, 10, getLineHeight(g2d));

				int textHeight = getTextHeight(g2d);
				initPaintHLDEFColor(10, getLineHeight(g2d), textHeight);
				g2d.setPaint(mPaint);
				g2d.drawString(lyricsLeft, 10, getLineHeight(g2d));
			}

			textY = getLineHeight(g2d) * 2;

		}
		// 画动感歌词
		drawDGBufferedImage(g2d, textX, textY, currentLyrics, lineLyricsHLWidth);
	}

	/**
	 * 画动感歌词
	 * 
	 * @param g2d
	 * @param textX
	 * @param textY
	 * @param text
	 * @param lineLyricsHLWidth
	 */
	private void drawDGBufferedImage(Graphics2D origG2d, float textX,
			float textY, String text, float lineLyricsHLWidth) {

		FontMetrics fm = origG2d.getFontMetrics();
		Rectangle2D rc = fm.getStringBounds(text, origG2d);
		int width = (int) rc.getWidth();
		int height = (int) rc.getHeight() + fm.getDescent() * 2;

		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		// 获取Graphics2D
		Graphics2D g2d = bufferedImage.createGraphics();
		// ---------- 增加下面的代码使得背景透明 -----------------
		bufferedImage = g2d.getDeviceConfiguration().createCompatibleImage(
				width, height, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = bufferedImage.createGraphics();
		// ---------- 背景透明代码结束 -----------------

		// 设置“抗锯齿”的属性
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);

		//
		g2d.setFont(mBaseFont);
		int dgTextX = 0;
		int dgTextY = getLineHeight(g2d);
		drawBackground(g2d, text, dgTextX, dgTextY);
		int textHeight = getTextHeight(g2d);
		initPaintHLDEFColor(dgTextX, dgTextY, textHeight);

		g2d.setPaint(mPaint);
		// 画当前歌词
		g2d.drawString(text, dgTextX, dgTextY);

		// 这里不知为何还要减去fm.getDescent() + fm.getLeading() 绘画时才能把全文字绘画完整
		int clipY = (int) (dgTextY - getRealTextHeight(g2d))
				+ getAdjustLrcHeight(g2d);

		g2d.setClip(dgTextX, clipY, (int) lineLyricsHLWidth,
				getRealTextHeight(g2d));

		initPaintHLEDColor(dgTextX, dgTextY, textHeight);
		g2d.setPaint(mPaintHL);
		g2d.drawString(text, dgTextX, dgTextY);

		g2d.dispose();

		origG2d.drawImage(bufferedImage, null, (int) textX, (int) textY
				- dgTextY);

	}

	/**
	 * 画默认文本
	 * 
	 * @param g2d
	 */
	private void drawDefText(Graphics2D g2d) {

		int textWidth = getTextWidth(g2d, mDefText);
		int textHeight = getTextHeight(g2d);

		int leftX = (mWidth - textWidth) / 2;
		float heightY = (mHeight + textHeight) / 2;

		int clipY = (int) (heightY - getRealTextHeight(g2d))
				+ getAdjustLrcHeight(g2d);

		drawBackground(g2d, mDefText, leftX, heightY);

		initPaintHLDEFColor(leftX, heightY, textHeight);
		g2d.setPaint(mPaint);
		g2d.drawString(mDefText, leftX, heightY);

		g2d.setClip(leftX, clipY, textWidth / 2, getRealTextHeight(g2d));

		drawBackground(g2d, mDefText, leftX, heightY);

		initPaintHLEDColor(leftX, heightY, textHeight);
		g2d.setPaint(mPaintHL);
		g2d.drawString(mDefText, leftX, heightY);

	}

	/**
	 * 描绘轮廓
	 * 
	 * @param canvas
	 * @param string
	 * @param x
	 * @param y
	 */
	private void drawBackground(Graphics2D g2d, String string, float x, float y) {
		g2d.setColor(new Color(0, 0, 0, 200));
		g2d.drawString(string, x - 1, y);
		g2d.drawString(string, x + 1, y);
		g2d.drawString(string, x, y + 1);
		g2d.drawString(string, x, y - 1);

	}

	/**
	 * 获取要微调的歌词高度
	 * 
	 * @param g2d
	 * @return
	 */
	private int getAdjustLrcHeight(Graphics2D g2d) {
		FontMetrics fm = g2d.getFontMetrics();
		return fm.getDescent() + fm.getLeading();
	}

	/**
	 * 获取每行高度
	 * 
	 * @param paint
	 * @return
	 */
	private int getLineHeight(Graphics2D g2d) {
		return getTextHeight(g2d) + mSpaceLineHeight;
	}

	/**
	 * 获取行歌词高度，用于绘画Y轴
	 * 
	 * @param paint
	 * @return
	 */
	private int getTextHeight(Graphics2D g2d) {
		FontMetrics fm = g2d.getFontMetrics();
		return fm.getHeight() - 2 * fm.getDescent();
	}

	/**
	 * 获取真实的歌词高度
	 * 
	 * @param g2d
	 * @return
	 */
	private int getRealTextHeight(Graphics2D g2d) {
		FontMetrics fm = g2d.getFontMetrics();
		return fm.getHeight();
	}

	/**
	 * 获取文本宽度
	 * 
	 * @param paint
	 * @param text
	 * @return
	 */
	private int getTextWidth(Graphics2D g2d, String text) {
		FontMetrics fm = g2d.getFontMetrics();
		Rectangle2D rc = fm.getStringBounds(text, g2d);
		return (int) rc.getWidth();
	}

	// ///////////////////////////////////////

	/**
	 * 更新歌词
	 */
	public void updateView(int playProgress) {
		this.mProgress = playProgress;
		if (mLyricsUtil == null || isReconstruct)
			return;
		//

		int newLyricsLineNum = mLyricsUtil.getLineNumber(mLyricsLineTreeMap,
				playProgress);
		if (newLyricsLineNum != mLyricsLineNum) {

			mLyricsLineNum = newLyricsLineNum;
		}
		// 获取歌词字索引
		mLyricsWordIndex = mLyricsUtil.getDisWordsIndex(mLyricsLineTreeMap,
				mLyricsLineNum, playProgress);
		mLyricsWordHLTime = mLyricsUtil.getDisWordsIndexLenTime(
				mLyricsLineTreeMap, mLyricsLineNum, playProgress);

		// 判断是否显示额外歌词
		if (mExtraLrcStatus == SHOWTRANSLITERATIONLRC
				|| mExtraLrcStatus == SHOWTRANSLATELRC) {

			mExtraLyricsLineNum = mLyricsLineNum;

			if (mExtraLrcStatus == SHOWTRANSLITERATIONLRC
					&& mTransliterationLrcLineInfos != null
					&& mTransliterationLrcLineInfos.size() > 0) {

				mExtraLyricsWordIndex = mLyricsUtil.getDisWordsIndex(
						mTransliterationLrcLineInfos, mExtraLyricsLineNum,
						playProgress);

				mExtraLyricsWordHLTime = mLyricsUtil.getDisWordsIndexLenTime(
						mTransliterationLrcLineInfos, mLyricsLineNum,
						playProgress);

			} else if (mExtraLrcStatus == SHOWTRANSLATELRC
					&& mTranslateLrcLineInfos != null
					&& mTranslateLrcLineInfos.size() > 0) {
				mExtraLyricsWordIndex = mLyricsUtil.getDisWordsIndex(
						mTranslateLrcLineInfos, mExtraLyricsLineNum,
						playProgress);

				mExtraLyricsWordHLTime = mLyricsUtil.getDisWordsIndexLenTime(
						mTranslateLrcLineInfos, mLyricsLineNum, playProgress);
			}
		}

		repaint();
	}

	/**
	 * 设置歌词
	 * 
	 * @param mLyricsUtil
	 */
	public void setLyricsUtil(LyricsUtil mLyricsUtil) {
		this.mLyricsUtil = mLyricsUtil;

		if (mLyricsUtil != null) {

			mLyricsLineTreeMap = mLyricsUtil.getDefLyricsLineTreeMap();
			mTranslateLrcLineInfos = mLyricsUtil.getTranslateLrcLineInfos();
			mTransliterationLrcLineInfos = mLyricsUtil
					.getTransliterationLrcLineInfos();

		} else {
			mLyricsLineTreeMap = null;
		}
		// 额外歌词类型回调
		extraLrcTypeCallBack();
		resetData();

		repaint();
	}

	/**
	 * 重置数据
	 */
	private void resetData() {

		//
		mLyricsLineNum = 0;
		mLyricsWordIndex = -1;
		mLyricsWordHLTime = 0;

		//
		mExtraLyricsLineNum = 0;
		mExtraLyricsWordIndex = -1;
		mExtraLrcStatus = NOSHOWEXTRALRC;

	}

	/**
	 * 额外歌词类型回调
	 */
	private void extraLrcTypeCallBack() {
		if (mLyricsUtil != null && mExtraLyricsListener != null) {
			int extraLrcType = mLyricsUtil.getExtraLrcType();
			if (extraLrcType == LyricsUtil.TRANSLATE_AND_TRANSLITERATION_LRC) {
				// 有翻译歌词和音译歌词
				if (mExtraLyricsListener != null) {
					mExtraLyricsListener
							.hasTranslateAndTransliterationLrcCallback();
				}
			} else if (extraLrcType == LyricsUtil.TRANSLATE_LRC) {
				// 有翻译歌词
				if (mExtraLyricsListener != null) {
					mExtraLyricsListener.hasTranslateLrcCallback();
				}
			} else if (extraLrcType == LyricsUtil.TRANSLITERATION_LRC) {
				// 音译歌词
				if (mExtraLyricsListener != null) {
					mExtraLyricsListener.hasTransliterationLrcCallback();
				}
			} else {
				// 无翻译歌词和音译歌词
				mExtraLyricsListener.noExtraLrcCallback();
			}
		} else {
			if (mExtraLyricsListener != null) {
				mExtraLyricsListener.noExtraLrcCallback();
			}
		}
		mExtraLrcStatus = NOSHOWEXTRALRC;
	}

	/**
	 * 更新歌词字体颜色
	 */
	public synchronized void refreshLrcFontColor() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();
			}
		});
	}

	/**
	 * 设置歌词字体大小
	 */
	public synchronized void refreshLrcFontSize() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initSizeWord();
				updateView(mProgress);
				repaint();
			}
		});
	}

	/**
	 * 设置额外歌词的状态
	 * 
	 * @param mExtraLrcStatus
	 */
	public void setExtraLrcStatus(int mExtraLrcStatus) {
		this.mExtraLrcStatus = mExtraLrcStatus;
		isReconstruct = true;
		// 更新歌词行索引
		int newLyricsLineNum = mLyricsUtil.getLineNumber(mLyricsLineTreeMap,
				mProgress);
		if (newLyricsLineNum != mLyricsLineNum) {
			mLyricsLineNum = newLyricsLineNum;
		}
		isReconstruct = false;
		updateView(mProgress);
	}

	// ///////////////////////////////////////

	/**
	 * 额外歌词事件
	 */
	public interface ExtraLyricsListener {

		/**
		 * 有翻译歌词回调
		 */
		void hasTranslateLrcCallback();

		/**
		 * 有音译歌词回调
		 */
		void hasTransliterationLrcCallback();

		/**
		 * 有翻译歌词和音译歌词回调
		 */
		void hasTranslateAndTransliterationLrcCallback();

		/**
		 * 无翻译和音译歌词回调
		 */
		void noExtraLrcCallback();
	}

	private class MouseListener implements MouseInputListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mouseClicked(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (!BaseData.desLrcIsLock)
				desLrcDialogMouseListener.mousePressed(e);
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
				isEnter = true;
				repaint();

				desLrcDialogMouseListener.mouseEntered(e);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!BaseData.desLrcIsLock) {
				isEnter = false;
				repaint();
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

	public void setExtraLyricsListener(ExtraLyricsListener mExtraLyricsListener) {
		this.mExtraLyricsListener = mExtraLyricsListener;
	}

	public boolean getShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
		repaint();
	}

	public void setEnter(boolean isEnter) {
		this.isEnter = isEnter;
		repaint();
	}

	public boolean getEnter() {
		return isEnter;
	}

	public LyricsUtil getLyricsUtil() {
		return mLyricsUtil;
	}

	public TreeMap<Integer, LyricsLineInfo> getLyricsLineTreeMap() {
		return mLyricsLineTreeMap;
	}

}
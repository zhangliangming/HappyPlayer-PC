package com.happy.widget.panel.lrc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.manage.MediaManage;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.tween.ValueAnimator;
import com.happy.tween.ValueAnimator.AnimationListener;
import com.happy.tween.ValueAnimator.AnimatorUpdateListener;
import com.happy.util.FontsUtil;
import com.happy.util.LyricsUtil;

/**
 * 多行歌词面板:支持翻译歌词和音译歌词。因为之前一直都是用Graphics2D和clip来实现动感歌词效果，
 * 但是在翻译歌词和音译歌词上面，再用clip来实现是比较麻烦的，所以，这里使用image来实现
 * 动感歌词效果；其实image内部也是使用Graphics2D和clip来实现动感歌词效果，并生成一个文本图片，
 * 然后再把该图片绘画到视图上面，从而实现动感歌词效果。
 * 
 * @author zhangliangming
 * 
 */
public class ManyLineLyricsView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 默认提示文本
	 */
	private String mDefText;

	private int mWidth = 0;

	/**
	 * 视图高度
	 */
	private int mHeight = 0;
	/**
	 * 歌词进入渐变透明度的区域高度大小，默认取视图高度的1/4
	 */
	private int mShadeHeight = 0;
	/**
	 * 颜色渐变梯度
	 */
	private int mMaxAlphaValue = 255;
	private int mMinAlphaValue = 50;
	private int mAlphaValue = 0;

	/**
	 * Y轴移动的时间
	 */
	private int mDuration = 250;

	/**
	 * 歌词在Y轴上的偏移量
	 */
	private float mOffsetY = 0;

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
	 * 当前歌词第几个字 已经播放的长度
	 */
	private float mLineLyricsHLWidth = 0;
	/**
	 * 当前歌词第几个字 已经播放的时间
	 */
	private float mLyricsWordHLTime = 0;

	/**
	 * 空行高度
	 */
	private int mSpaceLineHeight = 30;

	/***
	 * 播放进度
	 */
	private int mProgress = 0;

	/**
	 * 额外歌词监听事件
	 */
	private ExtraLyricsListener mExtraLyricsListener;

	private MetaDownListener mMetaDownListener;
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
	 * 额外歌词行空行高度
	 */
	private int mExtraLrcSpaceLineHeight = 20;

	/**
	 * 判断歌词集合是否在重构
	 */
	private boolean isReconstruct = false;

	/**
	 * 字体
	 */
	private Font mBaseFont;

	// ///////////////////////////////////////

	/**
	 * 默认高亮未读画笔
	 */
	private Color mPaint;
	/**
	 * 高亮已读画笔
	 */
	private Color mPaintHL;

	/**
	 * 歌词解析
	 */
	private LyricsUtil mLyricsUtil;

	// ///////////////////////////////////////
	/**
	 * 动画
	 */
	private ValueAnimator mValueAnimator;

	/**
	 * 正在拖动
	 */
	private boolean isTouchMove = false;

	public ManyLineLyricsView(int width, int height, boolean hasLrcEvent) {
		this.mWidth = width;
		this.mHeight = height;

		mShadeHeight = height / 4;
		//
		mAlphaValue = mShadeHeight / (mMaxAlphaValue - mMinAlphaValue);
		mBaseFont = FontsUtil.getBaseFont(BaseData.appFontSize);
		mDefText = Constants.APPTIPTITLE;

		initSizeWord();
		initColor();

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
		mPaint = Color.white;
		mPaintHL = BaseData.lrcColorStr[BaseData.lrcColorIndex];

	}

	/**
	 * 初始化字体大小
	 */
	private void initSizeWord() {
		mBaseFont = mBaseFont.deriveFont(Font.TRUETYPE_FONT,
				BaseData.lrcFontSize);
	}

	/**
	 * 初始化颜色透明度区域大小
	 */
	private void initShadeHeight() {
		mShadeHeight = mHeight / 4;
	}

	// 绘制组件
	public void paint(Graphics g) {
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

		// 画时间线和时间线
		if (isTouchMove) {
			drawTimeLine(g2d);
		}

		if (mLyricsLineTreeMap == null || mLyricsLineTreeMap.size() == 0) {
			drawDefText(g2d);
		} else {
			// 绘画多行歌词
			drawManyLineLrcText(g2d);
		}
	}

	/**
	 * 画时间线
	 * 
	 * @param g2d
	 */
	private void drawTimeLine(Graphics2D g2d) {
		g2d.setPaint(Color.WHITE);
		g2d.setStroke(new BasicStroke(2f));

		float y = mHeight / 2;
		// 画当前时间
		int scrollLrcLineNum = getScrollLrcLineNum(g2d, mOffsetY);
		int startTime = mLyricsLineTreeMap.get(scrollLrcLineNum).getStartTime();
		String timeString = TimeUtils.parseString(startTime);

		int textHeight = getTextHeight(g2d);
		g2d.drawString(timeString, 0, y - textHeight);
		g2d.drawLine(0, (int) y, mWidth, (int) y);
	}

	/**
	 * 绘画多行歌词
	 * 
	 * @param g2d
	 */
	private void drawManyLineLrcText(Graphics2D g2d) {
		float centreY = (mHeight + getTextHeight(g2d)) * 0.5f
				+ getCurLineScollHeight(g2d, mLyricsLineNum) - mOffsetY;

		// 获取要透明度要渐变的高度大小
		if (mShadeHeight == 0) {
			initShadeHeight();
		}
		centreY = drawDGLineLrc(g2d, centreY);

		// 画下面歌词
		drawDownLineLrc(g2d, centreY);

		// 画上面歌词
		drawUpLineLrc(g2d, centreY);
	}

	/**
	 * 绘画上面歌词
	 * 
	 * @param g2d
	 * @param centreY
	 */
	private void drawUpLineLrc(Graphics2D g2d, float centreY) {
		for (int i = mLyricsLineNum - 1; i >= 0; i--) {

			// 判断是否需要显示音译或者翻译歌词
			if (mExtraLrcStatus == SHOWTRANSLATELRC
					|| mExtraLrcStatus == SHOWTRANSLITERATIONLRC) {
				List<LyricsLineInfo> extraLrcList = null;
				if (mExtraLrcStatus == SHOWTRANSLATELRC) {
					extraLrcList = mTranslateLrcLineInfos;
				} else {
					extraLrcList = mTransliterationLrcLineInfos;
				}
				if (i == (mLyricsLineNum - 1)) {
					centreY = centreY - getLineHeight(g2d) - getLineHeight(g2d)
							+ (mSpaceLineHeight - mExtraLrcSpaceLineHeight);
				} else {
					centreY = centreY - getLineHeight(g2d);
				}

				drawLineLrc(g2d, centreY, extraLrcList.get(i));
				centreY = centreY
						+ (mSpaceLineHeight - mExtraLrcSpaceLineHeight);
			}

			centreY = centreY - getLineHeight(g2d);
			LyricsLineInfo lyricsLineInfo = mLyricsLineTreeMap.get(i);
			drawLineLrc(g2d, centreY, lyricsLineInfo);

		}
	}

	/**
	 * 绘画下面歌词
	 * 
	 * @param g2d
	 * @param centreY
	 */
	private void drawDownLineLrc(Graphics2D g2d, float centreY) {
		// 画当前歌词之后的歌词
		for (int i = mLyricsLineNum + 1; i < mLyricsLineTreeMap.size(); i++) {

			centreY = centreY + getLineHeight(g2d);

			LyricsLineInfo lyricsLineInfo = mLyricsLineTreeMap.get(i);
			drawLineLrc(g2d, centreY, lyricsLineInfo);

			// 判断是否需要显示音译或者翻译歌词
			if (mExtraLrcStatus == SHOWTRANSLATELRC
					|| mExtraLrcStatus == SHOWTRANSLITERATIONLRC) {
				List<LyricsLineInfo> extraLrcList = null;
				if (mExtraLrcStatus == SHOWTRANSLATELRC) {
					extraLrcList = mTranslateLrcLineInfos;
				} else {
					extraLrcList = mTransliterationLrcLineInfos;
				}

				centreY = centreY + getExtraLrcLineHeight(g2d);
				drawLineLrc(g2d, centreY, extraLrcList.get(i));
			}

		}

	}

	/**
	 * 绘画额外行歌词
	 * 
	 * @param g2d
	 * @param centreY
	 * @param lyricsLineInfo
	 */
	private void drawLineLrc(Graphics2D g2d, float centreY,
			LyricsLineInfo lyricsLineInfo) {

		String text = lyricsLineInfo.getLineLyrics();
		int textWidth = getTextWidth(g2d, text);
		float textX = (mWidth - textWidth) / 2;

		// 如果计算出的textX为负数，将textX置为0(实现：如果歌词宽大于view宽，则居左显示，否则居中显示)
		textX = Math.max(textX, 10);
		g2d.setPaint(mPaint);
		// 画当前歌词
		g2d.drawString(text, textX, centreY);

	}

	/**
	 * 绘画当前歌词行
	 * 
	 * @param g2d
	 * @param centreY
	 * @return
	 */
	private float drawDGLineLrc(Graphics2D g2d, float centreY) {
		// 画当前行歌词
		LyricsLineInfo lyricsLineInfo = mLyricsLineTreeMap.get(mLyricsLineNum);
		// 画当前行歌词
		drawDGLrcText(g2d, lyricsLineInfo, mPaint, mPaintHL, (int) centreY,
				mLyricsWordIndex, mLyricsWordHLTime);

		//
		if (mExtraLrcStatus == SHOWTRANSLATELRC
				|| mExtraLrcStatus == SHOWTRANSLITERATIONLRC) {

			centreY += getExtraLrcLineHeight(g2d);

			List<LyricsLineInfo> extraLrcList = null;
			if (mExtraLrcStatus == SHOWTRANSLATELRC) {
				extraLrcList = mTranslateLrcLineInfos;
			} else {
				extraLrcList = mTransliterationLrcLineInfos;
			}

			// 绘画额外歌词
			drawExtraLrcText(g2d, extraLrcList, centreY);

		}

		return centreY;
	}

	/**
	 * 绘画额外歌词行
	 * 
	 * @param g2d
	 * @param extraLrcList
	 * @param centreY
	 * @return
	 */
	private void drawExtraLrcText(Graphics2D g2d,
			List<LyricsLineInfo> extraLrcList, float textY) {
		// 画额外歌词
		LyricsLineInfo extraLyricsLineInfo = extraLrcList
				.get(mExtraLyricsLineNum);
		drawDGLrcText(g2d, extraLyricsLineInfo, mPaint, mPaintHL, (int) textY,
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
			Paint mPaint, Paint mPaintHL, int textY, int mLyricsWordIndex,
			float mLyricsWordHLTime) {
		// 当行歌词
		String currentLyrics = lyricsLineInfo.getLineLyrics();
		int lineLyricsWidth = getTextWidth(g2d, currentLyrics);
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
			lineLyricsHLWidth = lineLyricsWidth;
		}

		// 当前歌词行的x坐标
		float textX = 0;
		float lrcTextMoveX = 0;
		if (lineLyricsWidth > mWidth) {
			if (lineLyricsHLWidth >= mWidth / 2) {
				if ((lineLyricsWidth - lineLyricsHLWidth) >= mWidth / 2) {
					lrcTextMoveX = (mWidth / 2 - lineLyricsHLWidth);
				} else {
					lrcTextMoveX = mWidth - lineLyricsWidth - 20;
				}
			} else {
				lrcTextMoveX = 10;
			}
			// 如果歌词宽度大于view的宽，则需要动态设置歌词的起始x坐标，以实现水平滚动
			textX = lrcTextMoveX;
		} else {
			// 如果歌词宽度小于view的宽
			textX = (mWidth - lineLyricsWidth) / 2;
		}

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
		int height = (int) rc.getHeight() + fm.getDescent() * 2
				+ mSpaceLineHeight;
		width = Math.max(width, 1);
		height = Math.max(height, 1);

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

		g2d.setPaint(mPaint);
		// 画当前歌词
		g2d.drawString(text, dgTextX, dgTextY);

		// 这里不知为何还要减去fm.getDescent() + fm.getLeading() 绘画时才能把全文字绘画完整
		int clipY = (int) (dgTextY - getRealTextHeight(g2d))
				+ getAdjustLrcHeight(g2d);

		g2d.setClip(dgTextX, clipY, (int) lineLyricsHLWidth,
				getRealTextHeight(g2d));

		g2d.setPaint(mPaintHL);
		g2d.drawString(text, dgTextX, dgTextY);

		g2d.dispose();

		origG2d.drawImage(bufferedImage, null, (int) textX, (int) textY
				- dgTextY);

	}

	/**
	 * 绘画默认文本
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

		g2d.setPaint(mPaint);
		g2d.drawString(mDefText, leftX, heightY);

		g2d.setClip(leftX, clipY, textWidth / 2, getRealTextHeight(g2d));

		g2d.setPaint(mPaintHL);
		g2d.drawString(mDefText, leftX, heightY);
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
	 * 获取额外歌词行高度。用于y轴位置计算
	 * 
	 * @param paint
	 * @return
	 */
	public int getExtraLrcLineHeight(Graphics2D g2d) {
		return getTextHeight(g2d) + mExtraLrcSpaceLineHeight;
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

	/**
	 * 获取当前行所需的滑动高度
	 * 
	 * @param lyricsLineNum
	 * @return
	 */
	private float getCurLineScollHeight(Graphics2D g2d, int lyricsLineNum) {
		int scrollHeight = 0;

		for (int i = 0; i < lyricsLineNum; i++) {
			scrollHeight += getLineHeight(g2d);
			// 判断是否有翻译歌词或者音译歌词
			if (mExtraLrcStatus == SHOWTRANSLATELRC) {
				if (mTranslateLrcLineInfos != null
						&& mTranslateLrcLineInfos.size() > 0) {
					scrollHeight += getExtraLrcLineHeight(g2d);
				}
			} else if (mExtraLrcStatus == SHOWTRANSLITERATIONLRC) {
				if (mTransliterationLrcLineInfos != null
						&& mTransliterationLrcLineInfos.size() > 0) {
					scrollHeight += getExtraLrcLineHeight(g2d);
				}
			}
		}
		return scrollHeight;
	}

	/**
	 * 获取滑动的当前行
	 * 
	 * @return
	 */
	private int getScrollLrcLineNum(Graphics2D g2d, float offsetY) {
		if (mLyricsLineTreeMap == null || mLyricsLineTreeMap.size() == 0) {
			return 0;
		}

		int scrollLrcLineNum = -1;
		int lineHeight = 0;
		for (int i = 0; i < mLyricsLineTreeMap.size(); i++) {
			lineHeight += getLineHeight(g2d);

			// 判断是否有翻译歌词或者音译歌词
			if (mExtraLrcStatus == SHOWTRANSLATELRC) {
				if (mTranslateLrcLineInfos != null
						&& mTranslateLrcLineInfos.size() > 0) {
					lineHeight += getExtraLrcLineHeight(g2d);
				}
			} else if (mExtraLrcStatus == SHOWTRANSLITERATIONLRC) {
				if (mTransliterationLrcLineInfos != null
						&& mTransliterationLrcLineInfos.size() > 0) {
					lineHeight += getExtraLrcLineHeight(g2d);
				}
			}

			if (lineHeight > offsetY) {
				scrollLrcLineNum = i;
				break;
			}
		}
		if (scrollLrcLineNum == -1) {
			scrollLrcLineNum = mLyricsLineTreeMap.size() - 1;
		}
		return scrollLrcLineNum;
	}

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
			if (!isTouchMove)
				smoothScrollTo(newLyricsLineNum);
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
	 * 从旧行滚动到新行
	 * 
	 * @param lyricsLineNum
	 * @param newLyricsLineNum
	 */
	private void smoothScrollTo(int newLyricsLineNum) {
		if (mValueAnimator == null) {
			mValueAnimator = new ValueAnimator();
		} else {
			if (mValueAnimator.isRunning()) {
				mValueAnimator.cancel();
			}
			mValueAnimator = new ValueAnimator();
		}

		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.setFont(mBaseFont);

		float start = mOffsetY;
		float end = getCurLineScollHeight(g2d, newLyricsLineNum);

		mValueAnimator.ofFloat(start, end);
		mValueAnimator.setAnimatorUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(float curValue) {
				mOffsetY = curValue;
				repaint();

			}
		});
		mValueAnimator.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(float curValue) {

			}

			@Override
			public void onAnimationEnd(float curValue) {
			}
		});
		mValueAnimator.setDuration(mDuration);
		mValueAnimator.start();

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

		mOffsetY = 0;

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
				initColor();
				setExtraLrcStatus(mExtraLrcStatus);
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
				setExtraLrcStatus(mExtraLrcStatus);
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
		// 切换歌词时，直接设置offsety值
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.setFont(mBaseFont);
		mOffsetY = getCurLineScollHeight(g2d, mLyricsLineNum);
		//
		isReconstruct = false;
		updateView(mProgress);
	}

	private Toolkit tk = Toolkit.getDefaultToolkit();

	private Cursor draggedCursor = null;

	private Cursor pressedCursor = null;

	/**
	 * 歌词滚动监听器
	 */
	private LrcScrollListener lrcScrollListener;

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
				if (mMetaDownListener != null) {
					mMetaDownListener.MetaDown(e);
				}
			} else {

				if (mLyricsLineTreeMap != null
						&& mLyricsLineTreeMap.size() == 0)
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
				isTouchMove = true;

				repaint();

			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isMetaDown()) {// 检测鼠标右键单击

			} else {
				if (mLyricsLineTreeMap != null
						&& mLyricsLineTreeMap.size() == 0)
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

				//
				float tt = e.getY();
				touchY = tt;

				Graphics2D g2d = (Graphics2D) getGraphics();
				g2d.setFont(mBaseFont);
				// 获取当前滑动到的歌词播放行
				int scrollLrcLineNum = getScrollLrcLineNum(g2d, mOffsetY);

				if (mOffsetY < 0) {

					smoothScrollTo(0);
					isTouchMove = false;
					return;

				} else if (mOffsetY > getCurLineScollHeight(g2d,
						mLyricsLineTreeMap.size())) {

					smoothScrollTo(mLyricsLineTreeMap.size() - 1);
					isTouchMove = false;
					return;
				}

				int startTime = mLyricsLineTreeMap.get(scrollLrcLineNum)
						.getStartTime();

				int theFristWordTime = mLyricsLineTreeMap.get(scrollLrcLineNum)
						.getWordsDisInterval()[0];
				startTime += theFristWordTime;
				//
				if (MediaManage.getMediaManage().getSongInfo() != null) {
					if (startTime > MediaManage.getMediaManage().getSongInfo()
							.getDuration()) {
						startTime = (int) MediaManage.getMediaManage()
								.getSongInfo().getDuration();
					}
				}

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.SEEKTOMUSIC);
				songMessage.setProgress(startTime);
				ObserverManage.getObserver().setMessage(songMessage);

				// 延迟刷新歌词，避免歌词出现闪烁
				new Thread() {

					@Override
					public void run() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						isTouchMove = false;
						repaint();
					}

				}.start();

				repaint();
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
				if (mLyricsLineTreeMap != null
						&& mLyricsLineTreeMap.size() == 0)
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

				touchY = tt - touchY;

				Graphics2D g2d = (Graphics2D) getGraphics();
				g2d.setFont(mBaseFont);
				if (mOffsetY < 0) {
					touchY = touchY / 2;
				} else if (mOffsetY > getCurLineScollHeight(g2d,
						mLyricsLineTreeMap.size())) {
					touchY = touchY / 2;
				}

				mOffsetY -= touchY;
				touchY = tt;

				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}

	}

	public LyricsUtil getLyricsUtil() {
		return mLyricsUtil;
	}

	public TreeMap<Integer, LyricsLineInfo> getLyricsLineTreeMap() {
		return mLyricsLineTreeMap;
	}

	public void setMetaDownListener(MetaDownListener metaDownListener) {
		this.mMetaDownListener = metaDownListener;
	}

	public void setWidth(int width) {
		this.mWidth = width;
		repaint();
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

	public void setExtraLyricsListener(ExtraLyricsListener mExtraLyricsListener) {
		this.mExtraLyricsListener = mExtraLyricsListener;
	}

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

	// 定义一个三角形类
	class Triangle {
		private Point p1;
		private Point p2;
		private Point p3;

		private GeneralPath path;

		// 使用三个点构建一个三角形
		public Triangle(Point p1, Point p2, Point p3) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.path = buildPath();
		}

		// 绘制三角形边
		public void draw(Graphics2D g2d) {
			g2d.draw(path);
		}

		// 填充三角形
		public void fill(Graphics2D g2d) {
			g2d.fill(path);
		}

		// 创建三角形外形的路径
		private GeneralPath buildPath() {
			path = new GeneralPath();
			path.moveTo(p1.x, p1.y);
			path.lineTo(p2.x, p2.y);
			path.lineTo(p3.x, p3.y);
			path.closePath();

			return path;
		}
	}
}

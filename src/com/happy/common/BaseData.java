package com.happy.common;

import java.awt.Color;

/**
 * 基本数据
 * 
 * @author zhangliangming
 * 
 */
public class BaseData {

	/**
	 * app字体大小
	 */
	public static int appFontSize = 18;
	/**
	 * 标题图标
	 */
	public static String iconFileName = "ic_launcher.png";
	/**
	 * 桌面背景图片
	 */
	public static String bGroundFileName = "back.png";
	/**
	 * 播放列表界面透明度
	 */
	public static int listViewAlpha;
	/**
	 * 音量
	 */
	public static int volumeSize;
	/**
	 * 播放模式：0是 顺序播放 1是随机播放 2是循环播放 3是单曲播放 4单曲循环
	 */
	public static int playModel;

	/**
	 * 展开的列表
	 */
	public static String pLId = "-1";

	/**
	 * 歌曲索引
	 */
	public static String sId = "-1";
	/**
	 * 当前播放的歌曲索引
	 */
	public static String playInfoID = "-1";
	/**
	 * 当前播放的歌曲的播放列表索引
	 */
	public static String playInfoPID = "-1";

	/**
	 * 是否显示桌面歌词
	 */
	public static boolean showDesktopLyrics = false;

	/**
	 * 歌词颜色索引
	 */
	public static int lrcColorIndex = 0;

	/**
	 * 歌词颜色集合
	 */
	public static Color[] lrcColorStr = { new Color(250, 218, 131),
			new Color(93, 177, 240), new Color(139, 135, 150),
			new Color(225, 125, 179), new Color(157, 196, 0),
			new Color(255, 42, 45), new Color(138, 0, 226),
			new Color(228, 157, 0), new Color(23, 211, 190) };

	/**
	 * 歌词颜色提示
	 */
	public static String[] lrcColorTipStr = { "活力黄", "清爽蓝", "高雅灰", "可爱粉",
			"田野绿", "热血红", "魅力紫", "欢乐橙", "松柏青" };

	/**
	 * 歌词最小大小
	 */
	public static int lrcFontMinSize = 20;
	/**
	 * 歌词最大大小
	 */
	public static int lrcFontMaxSize = 50;
	/**
	 * 歌词大小
	 */
	public static int lrcFontSize = lrcFontMinSize;

	/**
	 * 字休大小提示
	 */
	public static String[] lrcSizeTip = new String[] { "最大", "较大", "中等", "较小",
			"最小" };
	/**
	 * 桌面歌词最小大小
	 */
	public static int desktopLrcFontMinSize = 0;
	/**
	 * 桌面歌词最大大小
	 */
	public static int desktopLrcFontMaxSize = 0;
	/**
	 * 桌面歌词大小
	 */
	public static int desktopLrcFontSize = desktopLrcFontMinSize;
	/**
	 * 未读歌词颜色
	 */
	public static Color DESLRCNOREADCOLORFRIST[] = { new Color(0, 52, 138),
			new Color(255, 255, 255), new Color(255, 172, 0),
			new Color(225, 225, 225), new Color(64, 0, 128) };

	public static Color DESLRCNOREADCOLORSECOND[] = { new Color(3, 202, 252),
			new Color(76, 166, 244), new Color(170, 0, 0), new Color(0, 0, 0),
			new Color(255, 128, 255) };
	/**
	 * 已读歌词颜色
	 */
	public static Color DESLRCREADEDCOLORFRIST[] = { new Color(130, 247, 253),
			new Color(255, 100, 26), new Color(255, 255, 0),
			new Color(0, 255, 255), new Color(255, 243, 146) };

	public static Color DESLRCREADEDCOLORSECOND[] = { new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 100, 26),
			new Color(255, 255, 255), new Color(255, 243, 134) };

	public static int desktopLrcIndex = 0;

	/**
	 * 制作歌词窗口是否打开
	 */
	public static boolean makeLrcDialogIsShow = false;
	/**
	 * 桌面歌词是否锁住
	 */
	public static boolean desLrcIsLock = false;
}

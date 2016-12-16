package com.happy.model;

/**
 * 消息传递类
 * 
 * @author zhangliangming
 * 
 */
public class MessageIntent {

	/**
	 * 打开制作歌词窗口
	 */
	public static final String OPEN_MAKELRCDIALOG = "com.happy.frame.openmakelrcdialog";
	/**
	 * 关闭制作歌词窗口
	 */
	public static final String CLOSE_MAKELRCDIALOG = "com.happy.frame.closemakelrcdialog";
	/**
	 * 默认
	 */
	public static final String FRAME_NORMAL = "com.happy.frame.normal";

	/**
	 * 多行歌词字体大小
	 */
	public static final String MANYLINEFONTSIZE = "com.hp.lyrics.fontsize";
	/**
	 * 多行歌词歌词颜色
	 */
	public static final String MANYLINELRCCOLOR = "com.hp.lyrics.color";

	/**
	 * 桌面歌词窗口
	 */
	public static final String OPENORCLOSEDESLRC = "com.happy.frame.openorclosedeslrc";
	/**
	 * 桌面歌词桌面关闭
	 */
	public static final String CLOSEDESLRC = "com.happy.frame.closedeslrc";

	/**
	 * 多行桌面歌词字体大小
	 */
	public static final String DESMANYLINEFONTSIZE = "com.hp.lyrics.des.fontsize";
	/**
	 * 多行桌面歌词歌词颜色
	 */
	public static final String DESMANYLINELRCCOLOR = "com.hp.lyrics.des.color";
	/**
	 * 播放器音量
	 */
	public static final String PLAYERVOLUME = "com.hp.player.Volume";

	/**
	 * 桌面歌词锁
	 */
	public static final String LOCKDESLRC = "com.happy.frame.locklyrics";
	private String action;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}

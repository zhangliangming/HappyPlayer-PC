package com.happy.model;

/**
 * 上一首，下一首，鼠标单击，双击时使用
 * 
 * @author zhangliangming
 * 
 */
public class EventIntent {
	/**
	 * 播放列表类型
	 */
	public static final int PLAYLIST = 0;
	/**
	 * 歌曲列表类型
	 */
	public static final int SONGLIST = 1;
	/**
	 * 单击
	 */
	public static final int SINGLECLICK = 0;
	/**
	 * 双击
	 */
	public static final int DOUBLECLICK = 1;
	/**
	 * 进入
	 */
	public static final int ENTERED = 2;
	/**
	 * 退出
	 */
	public static final int EXITED = 3;
	/**
	 * 还原
	 */
	public static final int RESET = 4;

	/**
	 * 事件类型
	 */
	private int eventType;
	/**
	 * 鼠标事件类型
	 */
	private int mouseType;

	/**
	 * 播放列表id
	 */
	private String pLId;

	/**
	 * 歌曲列表id
	 */
	private String sId;

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public int getMouseType() {
		return mouseType;
	}

	public void setMouseType(int mouseType) {
		this.mouseType = mouseType;
	}

	public String getpLId() {
		return pLId;
	}

	public void setpLId(String pLId) {
		this.pLId = pLId;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

}

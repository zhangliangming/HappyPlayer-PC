package com.happy.model;

import java.io.Serializable;

/**
 * 
 * @author zhangliangming
 * 
 */
public class SongInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***
	 * 本地歌曲
	 */
	public static int LOCALSONG = 0;

	/**
	 * 下载中
	 */
	public static final int DOWNLOADING = 0;
	/**
	 * 下载完成
	 */
	public static final int DOWNLOADED = 1;

	private String sid = ""; // 歌曲id
	private String displayName = "";// 歌曲显示名称
	private String title = ""; // 歌曲名称
	private String singer = ""; // 歌手名称
	private long duration; // 歌曲时长
	private String durationStr = "";// 歌曲时长
	private long size; // 文件大小
	private String sizeStr = ""; // 文件大小字符串
	private String filePath = "";// 文件路径
	private int type; // 歌曲类型 0是本地 1是网络
	private String categoryId = "";// 分类
	private String createTime = "";// 创建时间
	private String albumUrl = ""; // 专辑图片路径
	private String singerPIC = "";// 歌手写真图片路径
	private String lyricsUrl = "";// 歌词文件路径
	private String fileExt = ""; // 歌曲类型

	private long playProgress;// 播放的进度

	// /////////////华丽分隔线，用来区分网络歌曲和本地歌曲////////////////////////////

	private String downloadUrl = "";// 下载路径
	private long downloadProgress;// 下载进度
	private int downloadStatus; // 下载状态

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDurationStr() {
		return durationStr;
	}

	public void setDurationStr(String durationStr) {
		this.durationStr = durationStr;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSizeStr() {
		return sizeStr;
	}

	public void setSizeStr(String sizeStr) {
		this.sizeStr = sizeStr;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAlbumUrl() {
		return albumUrl;
	}

	public void setAlbumUrl(String albumUrl) {
		this.albumUrl = albumUrl;
	}

	public String getSingerPIC() {
		return singerPIC;
	}

	public void setSingerPIC(String singerPIC) {
		this.singerPIC = singerPIC;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public long getDownloadProgress() {
		return downloadProgress;
	}

	public void setDownloadProgress(long downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	public String getLyricsUrl() {
		return lyricsUrl;
	}

	public void setLyricsUrl(String lyricsUrl) {
		this.lyricsUrl = lyricsUrl;
	}

	public long getPlayProgress() {
		return playProgress;
	}

	public void setPlayProgress(long playProgress) {
		this.playProgress = playProgress;
	}

	public int getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

}

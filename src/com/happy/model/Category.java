package com.happy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表分类
 * 
 * @author zhangliangming
 * 
 */
public class Category {
	public static final String key = "CT-";

	private String cid;

	private String createTime;// 创建时间

	/**
	 * 分类名
	 */
	private String categoryName;
	/**
	 * 分类的内容
	 */
	private List<SongInfo> songInfos = new ArrayList<SongInfo>();

	public Category() {

	}

	public Category(String mCategroyName) {
		this.categoryName = mCategroyName;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<SongInfo> getSongInfos() {
		return songInfos;
	}

	public void setSongInfos(List<SongInfo> songInfos) {
		this.songInfos = songInfos;
	}

	public int getSongSize() {
		return songInfos.size();
	}
}
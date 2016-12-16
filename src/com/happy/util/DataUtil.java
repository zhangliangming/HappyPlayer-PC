package com.happy.util;

import java.io.InputStream;
import java.util.Properties;

import com.happy.common.BaseData;
import com.happy.db.BaseDataDB;
import com.happy.logger.LoggerManage;
import com.happy.manage.MediaManage;

/**
 * 数据处理
 * 
 * @author zhangliangming
 * 
 */
public class DataUtil {
	private static LoggerManage logger = LoggerManage.getZhangLogger();

	/**
	 * 初始化
	 */
	public static void init() {
		logger.info("开始加载初始数据.....");
		initBaseData();
		initSongListData();
		logger.info("初始数据加载完成.....");
	}

	/**
	 * 初始化歌曲列表信息
	 */
	private static void initSongListData() {
		MediaManage.getMediaManage().initPlayListData();
	}

	/***
	 * 初始化基本数据
	 */
	private static void initBaseData() {
		try {

			boolean flag = BaseDataDB.getBaseDataDB().init();
			if (flag)
				return;

			Properties prop = new Properties();// 属性集合对象
			InputStream in = DataUtil.class
					.getResourceAsStream("/data.properties");
			prop.load(in);// 将属性文件流装载到Properties对象中

			//
			BaseData.listViewAlpha = Integer.parseInt(prop.getProperty(
					"listViewAlpha", "50"));
			//
			BaseData.volumeSize = Integer.parseInt(prop.getProperty(
					"volumeSize", "50"));
			//
			BaseData.playModel = Integer.parseInt(prop.getProperty("playModel",
					"2"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 保存数据
	 */
	public static void saveData() {
		logger.info("保存数据");
		BaseDataDB.getBaseDataDB().add();
	}
}

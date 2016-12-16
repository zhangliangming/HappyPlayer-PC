package com.happy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.happy.logger.LoggerManage;
import com.happy.model.TabVersion;

/**
 * 数据库表版本信息表
 * 
 * @author zhangliangming
 * 
 */
public class TabVersionDB {
	private static LoggerManage logger = LoggerManage.getZhangLogger();

	/**
	 * 表名
	 */
	public static String TBL_NAME = "tabVersionTbl";
	/**
	 * 建表,不支持long型等
	 */
	public static String CREATE_TBL = "CREATE TABLE " + TBL_NAME + " ("
			+ "id VARCHAR(256),tabName VARCHAR(256),version int)";

	private static TabVersionDB _TabVersionDB;

	public TabVersionDB() {
		//
		try {
			boolean flag = DBUtils.isTableExist(TBL_NAME);
			if (!flag) {
				Connection connection = DBUtils.getConnection();
				connection.setAutoCommit(true);
				Statement stmt = connection.createStatement();
				stmt.executeUpdate(CREATE_TBL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 
	 * @return
	 */
	public static TabVersionDB getTabVersionDB() {
		if (_TabVersionDB == null) {
			_TabVersionDB = new TabVersionDB();
		}
		return _TabVersionDB;
	}

	/**
	 * 添加版本数据
	 * 
	 * @param tabVersion
	 */
	public void add(TabVersion tabVersion) {

		try {
			Connection connection = DBUtils.getConnection();
			String sql = "insert into " + TBL_NAME + " values(?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, tabVersion.getId());
			ps.setString(2, tabVersion.getTabName());
			ps.setInt(3, tabVersion.getVersion());

			int result = ps.executeUpdate();
			if (result <= 0)
				logger.error("插入" + TBL_NAME + "数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}

	}

	/**
	 * 获取版本数据
	 * 
	 * @param tabName
	 * @return
	 */
	public TabVersion getTabVersion(String tabName) {
		try {
			TabVersion tabVersion = new TabVersion();
			Connection connection = DBUtils.getConnection();
			String sql = "select * from " + TBL_NAME + " where tabName=?";

			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, tabName);

			ResultSet result = ps.executeQuery();
			if (result.next()) {

				tabVersion.setId(result.getString("id"));
				tabVersion.setTabName(result.getString("tabName"));
				tabVersion.setVersion(result.getInt("version"));
			}
			return tabVersion;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
		return null;
	}

	/**
	 * 更新版本数据
	 * 
	 * @param tabVersion
	 */
	public void update(TabVersion tabVersion) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "update " + TBL_NAME + " set version=? "
					+ "where id=?";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setInt(1, tabVersion.getVersion());
			ps.setString(2, tabVersion.getId());

			int result = ps.executeUpdate();// 返回行数或者0
			if (result <= 0)
				logger.error("更新" + TBL_NAME + "数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 该表数据已经存在
	 * 
	 * @return
	 */
	public boolean isExist(String id) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "select * from " + TBL_NAME + " where id=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
		return false;
	}
}

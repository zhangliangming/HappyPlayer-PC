package com.happy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.happy.common.BaseData;
import com.happy.logger.LoggerManage;
import com.happy.manage.MediaManage;
import com.happy.model.TabVersion;
import com.happy.util.IDGenerate;
import com.happy.util.ResultSetUtils;

/**
 * 基本数据
 * 
 * @author zhangliangming
 * 
 */
public class BaseDataDB {
	private static LoggerManage logger = LoggerManage.getZhangLogger();
	/**
	 * 固定id
	 */
	public static String id = "com.hp.bd.id";
	/**
	 * 表名
	 */
	public static String TBL_NAME = "baseDataTbl";
	// 当前版本
	private int version = 1;
	/**
	 * 建表,不支持long型等
	 */
	public static String CREATE_TBL = "CREATE TABLE " + TBL_NAME + " ("
			+ "id VARCHAR(256),listViewAlpha int,volumeSize int,playModel int,"
			+ "playInfoPID VARCHAR(256), playInfoID VARCHAR(256),"
			+ "showDesktopLyrics VARCHAR(256),lrcColorIndex int,"
			+ "lrcFontSize int,desktopLrcFontSize int,desktopLrcIndex int)";

	private static BaseDataDB _BaseDataDB;

	public BaseDataDB() {
		//
		try {
			boolean flag = DBUtils.isTableExist(TBL_NAME);
			if (!flag) {
				Connection connection = DBUtils.getConnection();
				connection.setAutoCommit(true);
				Statement stmt = connection.createStatement();
				stmt.executeUpdate(CREATE_TBL);

				// 添加表版本表数据
				TabVersion tabVersion = new TabVersion();
				tabVersion.setId(IDGenerate.getId(TabVersion.key));
				tabVersion.setTabName(TBL_NAME);
				tabVersion.setVersion(version);
				TabVersionDB.getTabVersionDB().add(tabVersion);

				//
			} else {
				// 表存在
				TabVersion tabVersion = TabVersionDB.getTabVersionDB()
						.getTabVersion(TBL_NAME);
				if (tabVersion != null) {
					// 数据库中保存的表的版本比现在的版本小，则更新表
					if (tabVersion.getVersion() < version) {
						// 更新表
						if (updateTab()) {
							tabVersion.setVersion(version);
							// 数据库中没有该记录
							if (tabVersion.getId() == null
									|| tabVersion.getTabName() == null) {
								tabVersion.setId(IDGenerate
										.getId(TabVersion.key));
								tabVersion.setTabName(TBL_NAME);
								TabVersionDB.getTabVersionDB().add(tabVersion);
							} else {
								// 更新数据库中的版本
								TabVersionDB.getTabVersionDB().update(
										tabVersion);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 更新表
	 * 
	 * @return
	 */
	private boolean updateTab() {
		try {
			List<String> valueList = new ArrayList<String>();
			valueList.add("playInfoPID VARCHAR(256)");
			valueList.add("playInfoID VARCHAR(256)");
			valueList.add("showDesktopLyrics VARCHAR(256)");
			valueList.add("lrcColorIndex int");
			valueList.add("lrcFontSize int");
			valueList.add("desktopLrcFontSize int");
			valueList.add("desktopLrcIndex int");

			String updateTabSql = "alter table " + TBL_NAME + " add ";
			for (int i = 0; i < valueList.size(); i++) {
				String sql = updateTabSql + valueList.get(i);
				try {
					Connection connection = DBUtils.getConnection();
					PreparedStatement ps = connection.prepareStatement(sql);
					ps.executeUpdate();

				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.toString());
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public static BaseDataDB getBaseDataDB() {
		if (_BaseDataDB == null) {
			_BaseDataDB = new BaseDataDB();
		}
		return _BaseDataDB;
	}

	/**
	 * 添加数据
	 * 
	 */
	public void add() {
		if (!isExist()) {
			addTab();
		} else {
			update();
		}
	}

	/**
	 * 
	 */
	public boolean init() {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "select * from " + TBL_NAME + " where id=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				BaseData.listViewAlpha = ResultSetUtils.getInt(result,
						"listViewAlpha");
				BaseData.playModel = ResultSetUtils.getInt(result, "playModel");
				BaseData.volumeSize = ResultSetUtils.getInt(result,
						"volumeSize");
				BaseData.playInfoPID = ResultSetUtils.getString(result,
						"playInfoPID", "-1");
				BaseData.playInfoID = ResultSetUtils.getString(result,
						"playInfoID", "-1");
				BaseData.showDesktopLyrics = ResultSetUtils.getBoolean(result,
						"showDesktopLyrics");
				BaseData.lrcColorIndex = ResultSetUtils.getInt(result,
						"lrcColorIndex");
				BaseData.lrcFontSize = ResultSetUtils.getInt(result,
						"lrcFontSize");
				BaseData.desktopLrcFontSize = ResultSetUtils.getInt(result,
						"desktopLrcFontSize");
				BaseData.desktopLrcIndex = ResultSetUtils.getInt(result,
						"desktopLrcIndex");

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

	/**
	 * 添加表
	 * 
	 */
	private void addTab() {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "insert into " + TBL_NAME
					+ " values(?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, id);
			ps.setInt(2, BaseData.listViewAlpha);
			ps.setInt(3, BaseData.volumeSize);
			ps.setInt(4, BaseData.playModel);
			ps.setString(5, BaseData.playInfoPID);
			ps.setString(6, BaseData.playInfoID);
			ps.setString(7, String.valueOf(BaseData.showDesktopLyrics));
			ps.setInt(8, BaseData.lrcColorIndex);
			ps.setInt(9, BaseData.lrcFontSize);
			ps.setInt(10, BaseData.desktopLrcFontSize);
			ps.setInt(11, BaseData.desktopLrcIndex);

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
	 * 更新
	 * 
	 */
	private void update() {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "update " + TBL_NAME
					+ " set listViewAlpha=?,volumeSize=?,playModel=?,"
					+ "playInfoPID=?,playInfoID=?,showDesktopLyrics=?,"
					+ "lrcColorIndex=?,lrcFontSize=?,desktopLrcFontSize=?,"
					+ "desktopLrcIndex=? " + "where id=?";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setInt(1, BaseData.listViewAlpha);
			ps.setInt(2, BaseData.volumeSize);
			ps.setInt(3, BaseData.playModel);

			ps.setString(4, BaseData.playInfoPID);
			if (BaseData.playInfoID.equals("-1")) {
				ps.setString(5, MediaManage.oldPlayInfoID);
			} else {
				ps.setString(5, BaseData.playInfoID);
			}

			ps.setString(6, String.valueOf(BaseData.showDesktopLyrics));
			ps.setInt(7, BaseData.lrcColorIndex);
			ps.setInt(8, BaseData.lrcFontSize);
			ps.setInt(9, BaseData.desktopLrcFontSize);
			ps.setInt(10, BaseData.desktopLrcIndex);

			ps.setString(11, id);
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
	private boolean isExist() {
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

package com.happy.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.happy.common.Constants;
import com.happy.logger.LoggerManage;

/**
 * 本地数据库处理
 * 
 * @author zhangliangming
 * 
 */
public class DBUtils {
	private static LoggerManage logger = LoggerManage.getZhangLogger();

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String url = "jdbc:derby:";
		String facetDir = Constants.PATH_DB + File.separator;
		Class.forName(driver).newInstance();
		Connection connection = DriverManager.getConnection(url + facetDir
				+ ";create=true");
		return connection;
	}

	/**
	 * 判断表是否存在
	 * 
	 * @param sTablename
	 * @return
	 * @throws Exception
	 */
	public static boolean isTableExist(String sTablename) throws Exception {
		Connection connection = getConnection();
		if (connection != null) {
			DatabaseMetaData dbmd = connection.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, sTablename.toUpperCase(),
					null);
			if (rs.next()) {
				logger.info(sTablename + " 表已经存在!不用创建!");
				return true;
			}
		}
		return false;
	}

	/**
	 * 关闭数据库
	 */
	public static void close() {
		// 内嵌模式数据库操作用完之后需要关闭数据库,这里没有执行数据库名称则全部关闭.
		try {
			String url = "jdbc:derby:";
			String facetDir = Constants.PATH_DB + File.separator;
			// DriverManager.getConnection("jdbc:derby:;shutdown=true");
			DriverManager.getConnection(url + facetDir + ";shutdown=true");
		} catch (SQLException e) {
			e.getMessage();
		}
	}
}

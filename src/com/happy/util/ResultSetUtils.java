package com.happy.util;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author zhangliangming
 * 
 */
public class ResultSetUtils {

	/**
	 * 
	 * @param resultSet
	 * @param key
	 * @return
	 */
	public static int getInt(ResultSet resultSet, String key) {
		try {
			return resultSet.getInt(key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param resultSet
	 * @param key
	 * @return
	 */
	public static String getString(ResultSet resultSet, String key, String def) {
		try {
			if (resultSet.getString(key) != null)
				return resultSet.getString(key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return def;
	}

	/**
	 * 
	 * @param resultSet
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(ResultSet resultSet, String key) {
		try {
			return Boolean.parseBoolean(resultSet.getString(key));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}

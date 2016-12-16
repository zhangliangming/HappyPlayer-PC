package com.happy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.happy.logger.LoggerManage;
import com.happy.model.Category;
import com.happy.model.TabVersion;
import com.happy.util.IDGenerate;

/**
 * 分类
 * 
 * @author zhangliangming
 * 
 */
public class CategoryDB {
	private static LoggerManage logger = LoggerManage.getZhangLogger();
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "categoryTbl";
	/**
	 * 默认版本
	 */
	private int version = 0;

	/**
	 * 建表语句,不支持long型等
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME + " ("
			+ "cid VARCHAR(256)," + "categoryName VARCHAR(256),"
			+ "createTime VARCHAR(256)" + ")";
	private static CategoryDB _CategoryDB;

	public CategoryDB() {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	public static CategoryDB getCategoryDB() {
		if (_CategoryDB == null)
			_CategoryDB = new CategoryDB();
		return _CategoryDB;
	}

	/**
	 * 添加分类
	 * 
	 * @param category
	 * @return
	 */
	public boolean add(Category category) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "insert into " + TBL_NAME + " values(?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setObject(1, category.getCid());
			ps.setObject(2, category.getCategoryName());
			ps.setObject(3, category.getCreateTime());
			int result = ps.executeUpdate();
			if (result <= 0)
				logger.error("插入列表分类数据失败!");
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
		return false;
	}

	/**
	 * 删除数据
	 * 
	 * @param categoryId
	 */
	public void delete(String categoryId) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "delete from " + TBL_NAME + " where cid=?";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, categoryId);
			int result = ps.executeUpdate();// 返回行数或者0
			if (result <= 0)
				logger.error("删除categoryId的分类数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 获取所有的分类数据
	 * 
	 * @return
	 */
	public List<Category> getAllCategory() {
		try {
			List<Category> categorys = new ArrayList<Category>();
			Connection connection = DBUtils.getConnection();
			String sql = "select * from " + TBL_NAME + " order by createTime";
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				Category category = getCategoryInfo(resultSet);
				if (category != null) {
					categorys.add(category);
				}
			}
			return categorys;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
		return null;
	}

	/**
	 * 
	 * @param resultSet
	 * @return
	 */
	private Category getCategoryInfo(ResultSet resultSet) {
		try {
			Category category = new Category();
			category.setCid(resultSet.getString("cid"));
			category.setCategoryName(resultSet.getString("categoryName"));
			category.setCreateTime(resultSet.getString("createTime"));

			return category;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return null;
	}
}

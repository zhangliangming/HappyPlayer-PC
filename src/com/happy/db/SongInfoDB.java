package com.happy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.happy.logger.LoggerManage;
import com.happy.model.SongInfo;
import com.happy.model.TabVersion;
import com.happy.util.IDGenerate;

public class SongInfoDB {
	private static LoggerManage logger = LoggerManage.getZhangLogger();
	/**
	 * 表名
	 */
	public static final String TBL_NAME = "songTbl";

	private int version = 0;
	/**
	 * 建表语句,不支持long型等
	 */
	public static final String CREATE_TBL = "create table " + TBL_NAME
			+ "(sid VARCHAR(256),displayName VARCHAR(256),"
			+ "title VARCHAR(256),singer VARCHAR(256),duration int,"
			+ "durationStr VARCHAR(256),size int,sizeStr VARCHAR(256),"
			+ "filePath VARCHAR(256),type int,categoryId VARCHAR(256),"
			+ "createTime VARCHAR(256),albumUrl VARCHAR(256),"
			+ "singerPIC VARCHAR(256),lyricsUrl VARCHAR(256),"
			+ "fileExt VARCHAR(256),downloadUrl VARCHAR(256),"
			+ "downloadProgress int,downloadStatus int)";

	private static SongInfoDB _SongInfoDB;

	public SongInfoDB() {
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

	public static SongInfoDB getSongInfoDB() {
		if (_SongInfoDB == null) {
			_SongInfoDB = new SongInfoDB();
		}
		return _SongInfoDB;
	}

	/**
	 * 添加歌曲数据
	 * 
	 * @param songInfo
	 */
	public void add(SongInfo songInfo) {

		List<Object> valuesList = new ArrayList<Object>();
		valuesList.add(songInfo.getSid());
		valuesList.add(songInfo.getDisplayName());
		valuesList.add(songInfo.getTitle());
		valuesList.add(songInfo.getSinger());
		valuesList.add((int) songInfo.getDuration());
		valuesList.add(songInfo.getDurationStr());
		valuesList.add((int) songInfo.getSize());
		valuesList.add(songInfo.getSizeStr());
		valuesList.add(songInfo.getFilePath());
		valuesList.add(songInfo.getType());
		valuesList.add(songInfo.getCategoryId());
		valuesList.add(songInfo.getCreateTime());
		valuesList.add(songInfo.getAlbumUrl());
		valuesList.add(songInfo.getSingerPIC());
		valuesList.add(songInfo.getLyricsUrl());
		valuesList.add(songInfo.getFileExt());
		valuesList.add(songInfo.getDownloadUrl());
		valuesList.add((int) songInfo.getDownloadProgress());
		valuesList.add(songInfo.getDownloadStatus());

		try {
			Connection connection = DBUtils.getConnection();
			String sql = "insert into " + TBL_NAME + " values(";

			for (int i = 0; i < valuesList.size(); i++) {
				if (i == 0) {
					sql += "?";
				} else {
					sql += ",?";
				}
			}
			sql += ")";

			PreparedStatement ps = connection.prepareStatement(sql);

			Object[] values = valuesList.toArray();
			for (int i = 0; i < values.length; i++) {
				ps.setObject((i + 1), values[i]);
			}

			int result = ps.executeUpdate();
			if (result <= 0)
				logger.error("插入歌曲数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 通过分类的id获取该分类下的所有歌曲列表
	 * 
	 * @param categoryId
	 * @return
	 */
	public List<SongInfo> getSongList(String categoryId) {
		try {
			List<SongInfo> songInfos = new ArrayList<SongInfo>();
			Connection connection = DBUtils.getConnection();
			String sql = "select * from " + TBL_NAME
					+ " where categoryId=? order by createTime";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, categoryId);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				SongInfo songInfo = getSongInfo(resultSet);
				if (songInfo != null) {
					songInfos.add(songInfo);
				}
			}
			return songInfos;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
		return null;
	}

	/**
	 * 通过ResultSet获取歌曲的数据
	 * 
	 * @param resultSet
	 * @return
	 */
	private SongInfo getSongInfo(ResultSet resultSet) {
		try {

			SongInfo song = new SongInfo();

			song.setSid(resultSet.getString("sid"));
			song.setDisplayName(resultSet.getString("displayName"));
			song.setTitle(resultSet.getString("title"));
			song.setSinger(resultSet.getString("singer"));
			song.setDuration(resultSet.getInt("duration"));
			song.setDurationStr(resultSet.getString("durationStr"));
			song.setFileExt(resultSet.getString("fileExt"));
			song.setSize(resultSet.getInt("size"));
			song.setSizeStr(resultSet.getString("sizeStr"));
			song.setFilePath(resultSet.getString("filePath"));
			song.setType(resultSet.getInt("type"));
			song.setCategoryId(resultSet.getString("categoryId"));
			song.setCreateTime(resultSet.getString("createTime"));
			song.setAlbumUrl(resultSet.getString("albumUrl"));
			song.setSingerPIC(resultSet.getString("singerPIC"));
			song.setLyricsUrl(resultSet.getString("lyricsUrl"));
			song.setDownloadUrl(resultSet.getString("downloadUrl"));
			song.setDownloadProgress(resultSet.getInt("downloadProgress"));
			song.setDownloadStatus(resultSet.getInt("downloadStatus"));

			return song;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return null;
	}

	/**
	 * 删除sid的相关数据
	 */
	public void delete(String sid) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "delete from " + TBL_NAME + " where sid=?";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, sid);
			int result = ps.executeUpdate();// 返回行数或者0
			if (result <= 0)
				logger.error("删除sid的歌曲数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 删除所有分类 id下的所有歌曲
	 * 
	 * @param categoryId
	 */
	public void deleteAllSongs(String categoryId) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "delete from " + TBL_NAME + " where categoryId=?";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, categoryId);
			int result = ps.executeUpdate();// 返回行数或者0
			if (result <= 0)
				logger.error("删除categoryId分类下的所有的歌曲数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}

	/**
	 * 更新歌曲的歌词路径
	 * 
	 * @param sid
	 * @param lyricsUrl
	 */
	public void updateSongLyricsUrl(String sid, String lyricsUrl) {
		try {
			Connection connection = DBUtils.getConnection();
			String sql = "update " + TBL_NAME + " set lyricsUrl=? where sid=?";
			PreparedStatement ps = connection.prepareStatement(sql);

			ps.setString(1, lyricsUrl);
			ps.setString(2, sid);
			int result = ps.executeUpdate();
			if (result <= 0)
				logger.error("更新歌词路径数据失败!");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		} finally {
			DBUtils.close();
		}
	}
}
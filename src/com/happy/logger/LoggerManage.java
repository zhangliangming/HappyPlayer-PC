package com.happy.logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.happy.common.Constants;

/**
 * 日志处理类
 * 
 * @author Administrator
 * 
 */
public class LoggerManage {
	/**
	 * 是否输出log
	 */
	private final static boolean logFlag = true;

	/**
	 * 应用名标签
	 */
	public final static String tag = "[" + Constants.APPNAME + "]";
	private String userName = null;
	/**
	 * 保存用户对应的Logger
	 */
	private static Map<String, LoggerManage> sLoggerTable = new HashMap<String, LoggerManage>();

	private static int SDCARD_LOG_FILE_SAVE_DAYS = 3;// 日志文件的最多保存天数
	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
	private static Logger logger;

	public LoggerManage(String userName) {
		this.userName = userName;
		if (logger == null) {
			logger = Logger.getLogger(tag);
			initLog();
		}
	}

	/**
	 * 初始化log的相关配置
	 */
	private void initLog() {

		String time = logfile.format(new Date());
		String fileName = time + ".log";
		String path = Constants.PATH_LOGCAT + File.separator + fileName;

		// 内容格式
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} <%m>%n";
		PatternLayout layout = new PatternLayout(pattern);

		RollingFileAppender rollingFileAppender = null;

		try {
			// 文件
			rollingFileAppender = new RollingFileAppender(layout, path, true);
			rollingFileAppender.setEncoding("utf-8");
			// 只有一个文件
			rollingFileAppender.setMaxBackupIndex(0);
			// 500kb
			rollingFileAppender.setMaximumFileSize(1024 * 500);
			// 控件台输出
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.addAppender(consoleAppender);
			logger.addAppender(rollingFileAppender);
			logger.setLevel((Level) Level.ALL);
			cleanOldLogFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 清除过期的log文件
	 */
	private void cleanOldLogFile() {
		File logFileParent = new File(Constants.PATH_LOGCAT);
		if (logFileParent.exists()) {
			String needDelTime = logfile.format(getDateBefore());
			File[] files = logFileParent.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					String fileName = files[i].getName();
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					if (needDelTime.compareTo(fileName) > 0) {
						files[i].delete();
					}
				}
			}
		}
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 * */
	private Date getDateBefore() {
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE)
				- SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}

	/**
	 * 
	 * @param userName
	 *            用戶名
	 * @return
	 */
	public static LoggerManage getLogger(String userName) {
		LoggerManage userLogger = (LoggerManage) sLoggerTable.get(userName);
		if (userLogger == null) {
			userLogger = new LoggerManage(userName);
			sLoggerTable.put(userName, userLogger);
		}
		return userLogger;

	}

	/**
	 * 创建zhangliangming用法
	 * 
	 * @return
	 */
	public static LoggerManage getZhangLogger() {
		String name = "zhangliangming";
		LoggerManage userLogger = (LoggerManage) sLoggerTable.get(name);
		if (userLogger == null) {
			userLogger = new LoggerManage(name);
			sLoggerTable.put(name, userLogger);
		}
		return userLogger;
	}

	/**
	 * 获取方法名
	 * 
	 * @return
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return "@" + userName + "@ " + "[" + st.getClassName() + ":"
					+ st.getLineNumber() + " " + st.getMethodName() + "]";
		}
		return null;
	}

	public void info(String str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				logger.info(name + " - " + str);
			} else {
				logger.info(str.toString());
			}
		}
	}

	public void debug(String str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				logger.debug(name + " - " + str);
			} else {
				logger.debug(str.toString());
			}
		}
	}

	public void fatal(String str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				logger.fatal(name + " - " + str);
			} else {
				logger.fatal(str.toString());
			}
		}
	}

	public void warn(String str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				logger.warn(name + " - " + str);
			} else {
				logger.warn(str.toString());
			}
		}
	}

	public void error(String str) {
		if (logFlag) {
			String name = getFunctionName();
			if (name != null) {
				logger.error(name + " - " + str);
			} else {
				logger.error(str.toString());
			}
		}
	}

}

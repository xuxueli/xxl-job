package com.xxl.job.executor.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.xxl.job.core.pojo.vo.LogResult;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

/**
 * 任务日志
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class JobLogUtils {

	private static String logBasePath = (StrUtil.endWith(SystemUtil.get(SystemUtil.USER_DIR), "/")
			? SystemUtil.get(SystemUtil.USER_DIR) :  SystemUtil.get(SystemUtil.USER_DIR) + "/") + "logs/xxl-job/job-handler";
	private static String glueSrcPath = logBasePath.concat("/glueSource");

	public static void initLogPath(String logPath){
		if (StrUtil.isNotBlank(logPath)) {
			logBasePath = logPath;
		}
		File logPathDir = new File(logBasePath);
		FileUtil.mkdir(logPathDir);
		logBasePath = logPathDir.getPath();

		File glueBaseDir = new File(logPathDir, "glueSource");
		FileUtil.mkdir(glueBaseDir);
		glueSrcPath = glueBaseDir.getPath();
	}
	public static String getLogPath() {
		return logBasePath;
	}
	public static String getGlueSrcPath() {
		return glueSrcPath;
	}

	/**
	 * log filename, like "logPath/yyyy-MM-dd/9999.log"
	 *
	 * @param triggerDate 触发日期
	 * @param logId       日志id
	 * @return {@link String}
	 */
	public static String makeLogFileName(Date triggerDate, long logId) {

		File logFilePath = new File(getLogPath(), DateUtil.formatDate(triggerDate));
		FileUtil.mkdir(logFilePath);

		return logFilePath.getPath()
				.concat(File.separator)
				.concat(String.valueOf(logId))
				.concat(".log");
	}

	/**
	 * 添加日志
	 *
	 * @param logFileName 日志文件名字
	 * @param appendLog   添加日志
	 */
	public static void appendLog(String logFileName, String appendLog) {

		if (StrUtil.isBlank(logFileName)) return;
		File logFile = new File(logFileName);
//		if (!logFile.exists()) {
//			try {
//				FileUtil.newFile()
//				logFile.createNewFile();
//			} catch (IOException e) {
//				log.error(e.getMessage(), e);
//				return;
//			}
//		}
		FileUtil.writeLines(Collections.singletonList(appendLog), logFile, CharsetUtil.UTF_8, Boolean.TRUE);
	}

	/**
	 * 读取日志
	 *
	 * @param logFileName 日志文件名字
	 * @param fromLineNum 从num行
	 * @return log content
	 */
	public static LogResult readLog(String logFileName, int fromLineNum){

		// valid log file
		if (StrUtil.isBlank(logFileName)) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		File logFile = new File(logFileName);

		if (!logFile.exists()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// read file
		StringBuffer logContentBuffer = new StringBuffer();
		int toLineNum = 0;
		LineNumberReader reader = null;
		try {
			//reader = new LineNumberReader(new FileReader(logFile));
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8));
			String line = null;

			while ((line = reader.readLine())!=null) {
				toLineNum = reader.getLineNumber();		// [from, to], start as 1
				if (toLineNum >= fromLineNum) {
					logContentBuffer.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IoUtil.close(reader);
		}

		// result
		return new LogResult(fromLineNum, toLineNum, logContentBuffer.toString(), Boolean.FALSE);
	}

	/**
	 * 读行
	 *
	 * @param logFile 日志文件
	 * @return log line content
	 */
	public static String readLines(File logFile){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			IoUtil.close(reader);
		}
		return null;
	}

}

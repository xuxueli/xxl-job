package com.xxl.job.core.log;

import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.core.StringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * store trigger log in each log-file
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender {
	private static final Logger logger = LoggerFactory.getLogger(XxlJobFileAppender.class);

	/**
	 * log base path
	 *
	 * strut like:
	 * 	---/
	 * 	---/gluesource/10_1514171108000.js
	 * 	---/callbacklogs/xxl-job-callback-1761412677119.log
	 * 	---/2017-12-25/639.log
	 * 	---/2017-12-25/821.log
	 *
	 */
	private static String logBasePath = "/data/applogs/xxl-job/jobhandler";
	private static String glueSrcPath = logBasePath.concat(File.separator).concat("gluesource");
	private static String callbackLogPath = logBasePath.concat(File.separator).concat("callbacklogs");
	public static void initLogPath(String logPath){
		// init
		if (StringTool.isNotBlank(logPath)) {
			logBasePath = logPath.trim();
		}
		// mk base dir
		File logPathDir = new File(logBasePath);
		if (!logPathDir.exists()) {
			logPathDir.mkdirs();
		}
		logBasePath = logPathDir.getPath();

		// mk glue dir
		File glueBaseDir = new File(logPathDir, "gluesource");
		if (!glueBaseDir.exists()) {
			glueBaseDir.mkdirs();
		}
		glueSrcPath = glueBaseDir.getPath();
	}
	public static String getLogPath() {
		return logBasePath;
	}
	public static String getGlueSrcPath() {
		return glueSrcPath;
	}
	public static String getCallbackLogPath() {
		return callbackLogPath;
	}

	/**
	 * log filename, like "logPath/yyyy-MM-dd/9999.log"
	 *
	 * @param triggerDate
	 * @param logId
	 * @return
	 */
	public static String makeLogFileName(Date triggerDate, long logId) {

		// filePath/yyyy-MM-dd
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	// avoid concurrent problem, can not be static
		File logFilePath = new File(getLogPath(), sdf.format(triggerDate));
		if (!logFilePath.exists()) {
			logFilePath.mkdir();
		}

		// filePath/yyyy-MM-dd/9999.log
		String logFileName = logFilePath.getPath()
				.concat(File.separator)
				.concat(String.valueOf(logId))
				.concat(".log");
		return logFileName;
	}

	/**
	 * append log
	 *
	 * @param logFileName	log file name
	 * @param appendLog		append log
	 */
	public static void appendLog(String logFileName, String appendLog) {

		// log file
		if (logFileName==null || logFileName.trim().isEmpty()) {
			return;
		}
		File logFile = new File(logFileName);
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return;
			}
		}

		// log
		if (appendLog == null) {
			appendLog = "";
		}
		appendLog += "\r\n";
		
		// append file content
        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            fos.write(appendLog.getBytes(StandardCharsets.UTF_8));
            fos.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
		
	}

	/**
	 * support read log-file
	 *
	 * @param logFileName	log file name
	 * @param fromLineNum	from line num
	 * @return log content
	 */
	public static LogResult readLog(String logFileName, int fromLineNum){

		// valid log file
		if (logFileName==null || logFileName.trim().isEmpty()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		File logFile = new File(logFileName);
		if (!logFile.exists()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// read file
		StringBuilder logContentBuilder = new StringBuilder();
		LineNumberReader reader = null;
		int toLineNum = 0;
		/*int readLineCount = 0;*/
		try {
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8));
			String line = null;
			while ((line = reader.readLine())!=null) {
				// skip before lineNum
				toLineNum = reader.getLineNumber();		// [from, to], start as 1
				if (toLineNum < fromLineNum) {
					continue;
				}

				// append log
				logContentBuilder.append(line).append("\n");
				// Limit return less than 1000 rows per query request	// todo
				/*if(++readLineCount >= 5) {
					break;
				}*/
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		// result
        return new LogResult(fromLineNum, toLineNum, logContentBuilder.toString(), false);
	}

}

package com.xxl.job.core.log;

import com.xxl.job.core.biz.model.LogResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * store trigger log in each log-file
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender {
	private static Logger logger = LoggerFactory.getLogger(XxlJobFileAppender.class);
	
	// for JobThread (support log for child thread of job handler)
	//public static ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	public static final InheritableThreadLocal<String> contextHolder = new InheritableThreadLocal<String>();
	public static String logPath = "/data/applogs/xxl-job/jobhandler/";

	/**
	 * log filename: yyyy-MM-dd/9999.log
	 *
	 * @param triggerDate
	 * @param logId
	 * @return
	 */
	public static String makeLogFileName(Date triggerDate, int logId) {

        // filePath/
        File filePathDir = new File(logPath);
        if (!filePathDir.exists()) {
            filePathDir.mkdirs();
        }

        // filePath/yyyy-MM-dd/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	// avoid concurrent problem, can not be static

        String nowFormat = sdf.format(new Date());
        File filePathDateDir = new File(filePathDir, nowFormat);
        if (!filePathDateDir.exists()) {
            filePathDateDir.mkdirs();
        }

        // filePath/yyyy-MM-dd/9999.log
		String logFileName = sdf.format(triggerDate).concat("/").concat(String.valueOf(logId)).concat(".log");
		return logFileName;
	}

	/**
	 * append log
	 *
	 * @param logFileName
	 * @param appendLog
	 */
	public static void appendLog(String logFileName, String appendLog) {

		// log
		if (appendLog == null) {
			appendLog = "";
		}
		appendLog += "\r\n";

		// log file
		if (logFileName==null || logFileName.trim().length()==0) {
			return;
		}
		File logFile = new File(logPath, logFileName);

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return;
			}
		}
		
		// append file content
		try {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(logFile, true);
				fos.write(appendLog.getBytes("utf-8"));
				fos.flush();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			} 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	/**
	 * support read log-file
	 *
	 * @param logFileName
	 * @return log content
	 */
	public static LogResult readLog(String logFileName, int fromLineNum){

		// valid log file
		if (logFileName==null || logFileName.trim().length()==0) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		File logFile = new File(logPath, logFileName);

		if (!logFile.exists()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// read file
		StringBuffer logContentBuffer = new StringBuffer();
		int toLineNum = 0;
		LineNumberReader reader = null;
		try {
			//reader = new LineNumberReader(new FileReader(logFile));
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
			String line = null;

			while ((line = reader.readLine())!=null) {
				toLineNum = reader.getLineNumber();		// [from, to], start as 1
				if (toLineNum >= fromLineNum) {
					logContentBuffer.append(line).append("\n");
				}
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
		LogResult logResult = new LogResult(fromLineNum, toLineNum, logContentBuffer.toString(), false);
		return logResult;

		/*
        // it will return the number of characters actually skipped
        reader.skip(Long.MAX_VALUE);
        int maxLineNum = reader.getLineNumber();
        maxLineNum++;	// 最大行号
        */
	}

	/**
	 * read log data
	 * @param logFile
	 * @return log line content
	 */
	public static String readLines(File logFile){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"));
			if (reader != null) {
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				return sb.toString();
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
		return null;
	}

}

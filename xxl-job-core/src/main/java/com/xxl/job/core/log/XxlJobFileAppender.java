package com.xxl.job.core.log;

import com.xxl.job.core.biz.model.LogResult;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * store trigger log in each log-file
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender extends AppenderSkeleton {
	
	// for JobThread
	public static ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	// trogger log file path
	public static volatile String filePath;
	public void setFilePath(String filePath) {
		XxlJobFileAppender.filePath = filePath;
	}

	/**
	 * log filename: yyyy-MM-dd/9999.log
	 *
	 * @param triggerDate
	 * @param logId
	 * @return
	 */
	public static String makeLogFileName(Date triggerDate, int logId) {

        // filePath/
        File filePathDir = new File(filePath);
        if (!filePathDir.exists()) {
            filePathDir.mkdirs();
        }

        // filePath/yyyy-MM-dd/
        String nowFormat = sdf.format(new Date());
        File filePathDateDir = new File(filePathDir, nowFormat);
        if (!filePathDateDir.exists()) {
            filePathDateDir.mkdirs();
        }

        // filePath/yyyy-MM-dd/9999.log
		String logFileName = XxlJobFileAppender.sdf.format(triggerDate).concat("/").concat(String.valueOf(logId)).concat(".log");
		return logFileName;
	}

	@Override
	protected void append(LoggingEvent event) {

		String logFileName = contextHolder.get();
		if (logFileName==null || logFileName.trim().length()==0) {
			return;
		}
		File logFile = new File(filePath, logFileName);

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
		// append file content
		try {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(logFile, true);
				fos.write(layout.format(event).getBytes("utf-8"));
				if (layout.ignoresThrowable()) {
					String[] throwableInfo = event.getThrowableStrRep();
					if (throwableInfo != null) {
						for (int i = 0; i < throwableInfo.length; i++) {
							fos.write(throwableInfo[i].getBytes("utf-8"));
							fos.write(Layout.LINE_SEP.getBytes("utf-8"));
						}
					}
				}
				fos.flush();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * support read log-file
	 * @param logFileName
	 * @return log content
	 */
	public static LogResult readLog(String logFileName, int fromLineNum){

		// valid log file
		if (logFileName==null || logFileName.trim().length()==0) {
            return new LogResult(fromLineNum, -1, "readLog fail, logFile not found", true);
		}
		File logFile = new File(filePath, logFileName);

		if (!logFile.exists()) {
            return new LogResult(fromLineNum, -1, "readLog fail, logFile not exists", true);
		}

		// read file
		StringBuffer logContentBuffer = new StringBuffer();
		int toLineNum = 0;
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(logFile));
			String line = null;

			while ((line = reader.readLine())!=null) {
				toLineNum++;
				if (reader.getLineNumber() >= fromLineNum) {
					logContentBuffer.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
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
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
		return null;
	}

}

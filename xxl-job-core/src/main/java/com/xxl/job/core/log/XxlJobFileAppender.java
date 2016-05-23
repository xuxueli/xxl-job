package com.xxl.job.core.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * store trigger log in each log-file
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender extends AppenderSkeleton {
	
	// for HandlerThread
	public static ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	// trogger log file path
	public static volatile String filePath;
	public void setFilePath(String filePath) {
		XxlJobFileAppender.filePath = filePath;
	}
	
	@Override
	protected void append(LoggingEvent event) {
		String trigger_log_id = contextHolder.get();
		if (trigger_log_id==null || trigger_log_id.trim().length()==0) {
			return;
		}
		
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
		String logFileName = trigger_log_id.concat(".log");
		File logFile = new File(filePathDateDir, logFileName);	
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
	 * @param triggerDate
	 * @param trigger_log_id
	 * @return
	 */
	public static String readLog(Date triggerDate, String trigger_log_id ){
		if (triggerDate==null || trigger_log_id==null || trigger_log_id.trim().length()==0) {
			return null;
		}
		
		// filePath/
		File filePathDir = new File(filePath);	
		if (!filePathDir.exists()) {
			filePathDir.mkdirs();
		}
		
		// filePath/yyyy-MM-dd/
		String nowFormat = sdf.format(triggerDate);
		File filePathDateDir = new File(filePathDir, nowFormat);	
		if (!filePathDateDir.exists()) {
			filePathDateDir.mkdirs();
		}
		
		// filePath/yyyy-MM-dd/9999.log
		String logFileName = trigger_log_id.concat(".log");
		File logFile = new File(filePathDateDir, logFileName);	
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		try {
			InputStream ins = null;
			BufferedReader reader = null;
			try {
				ins = new FileInputStream(logFile);
				reader = new BufferedReader(new InputStreamReader(ins, "utf-8"));
				if (reader != null) {
					String content = null;
					StringBuilder sb = new StringBuilder();
					while ((content = reader.readLine()) != null) {
						sb.append(content).append("\n");
					}
					return sb.toString();
				}
			} finally {
				if (ins != null) {
					try {
						ins.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

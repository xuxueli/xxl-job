package com.xxl.job.core.log;

import com.xxl.job.core.biz.model.LogResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * store trigger log in each log-file
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender {
	private static Logger logger = LoggerFactory.getLogger(XxlJobFileAppender.class);

	/**
	 * log base path
	 *
	 * strut like:
	 * 	---/
	 * 	---/gluesource/
	 * 	---/gluesource/10_1514171108000.js
	 * 	---/gluesource/10_1514171108000.js
	 * 	---/2017-12-25/
	 * 	---/2017-12-25/639.log
	 * 	---/2017-12-25/821.log
	 *
	 */
	private static String logBasePath = "/data/applogs/xxl-job/jobhandler";
	private static String glueSrcPath = logBasePath.concat("/gluesource");
	public static void initLogPath(String logPath){
		// init
		if (logPath!=null && logPath.trim().length()>0) {
			logBasePath = logPath;
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
		return logFilePath.getPath()
				.concat(File.separator)
				.concat(String.valueOf(logId))
				.concat(".log");
	}

	/**
	 * append log
	 *
	 * @param logFileName
	 * @param appendLog
	 */
	public static void appendLog(String logFileName, String appendLog) {

		// log file
		if (logFileName==null || logFileName.trim().length()==0) {
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
		try (final FileOutputStream fos = new FileOutputStream(logFile, true)) {
			fos.write(appendLog.getBytes(StandardCharsets.UTF_8));
			fos.flush();
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
		File logFile = new File(logFileName);

		if (!logFile.exists()) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

        // read file
        StringBuilder logContentBuffer = new StringBuilder();
        int toLineNum = 0;

        // Use `Files.newInputStream` to replace the original `new FileInputStream`
        // https://stackoverflow.com/questions/16977251/java-on-mac-os-filenotfound-if-path-contatins-non-latin-characters/17481204#17481204
        try (final LineNumberReader reader = new LineNumberReader(new InputStreamReader(Files.newInputStream(logFile.toPath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // [from, to], start as 1
                toLineNum = reader.getLineNumber();
                if (toLineNum >= fromLineNum) {
                    logContentBuffer.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

		// result
		return new LogResult(fromLineNum, toLineNum, logContentBuffer.toString(), false);

		/*
        // it will return the number of characters actually skipped
        reader.skip(Long.MAX_VALUE);
        int maxLineNum = reader.getLineNumber();
        maxLineNum++;	// 最大行号
        */
	}

    /**
     * read log data
     *
     * @param logFile
     * @return log line content
     */
    public static String readLines(File logFile) {
        // Use `Files.newInputStream` to replace the original `new FileInputStream`
        // https://stackoverflow.com/questions/16977251/java-on-mac-os-filenotfound-if-path-contatins-non-latin-characters/17481204#17481204
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(logFile.toPath()), StandardCharsets.UTF_8))) {
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}

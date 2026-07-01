package com.xxl.job.core.log;

import com.xxl.job.core.openapi.executor.dto.LogData;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.io.FileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * store trigger log in each log-file
 *
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender {
	private static final Logger logger = LoggerFactory.getLogger(XxlJobFileAppender.class);

	// ---------------------- log segment markers ----------------------

	public static final String LOG_SEGMENT_START_PREFIX = "=====[LOG_ID:";
	public static final String LOG_SEGMENT_START_SUFFIX = "][START]=====";
	public static final String LOG_SEGMENT_END_PREFIX = "=====[LOG_ID:";
	public static final String LOG_SEGMENT_END_SUFFIX = "][END]=====";

	/**
	 * log base path
	 *
	 * strut like:
	 * 	---/
	 * 	---/gluesource/10_1514171108000.js
	 * 	---/callbacklogs/xxl-job-callback-1761412677119.log
	 * 	---/2017-12-25/job-7.log
	 * 	---/2017-12-25/job-7.idx
	 *
	 */
	private static String logBasePath;
	private static String glueSrcPath;
	private static String callbackLogPath;
	private static boolean logIsolatedByAddress = false;
	private static String executorAddress;

	/**
	 * init log path
	 */
	public static void initLogPath(String logPath) throws IOException {
		// init
		if (StringTool.isBlank(logPath)) {
			throw new RuntimeException("xxl-job logPath cannot be empty");
		}
		logBasePath = logPath.trim();

		// mk base dir
		File logPathDir = new File(logBasePath);
        FileTool.createDirectories(logPathDir);
		logBasePath = logPathDir.getPath();

		// mk glue dir
		File glueBaseDir = new File(logPathDir, "gluesource");
        FileTool.createDirectories(glueBaseDir);
		glueSrcPath = glueBaseDir.getPath();

		// mk callback log dir
		File callbackBaseDir = new File(logPathDir, "callbacklogs");
        FileTool.createDirectories(callbackBaseDir);
		callbackLogPath = callbackBaseDir.getPath();
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
	 * set log isolation config
	 */
	public static void setLogIsolation(boolean isolated, String address) {
		logIsolatedByAddress = isolated;
		executorAddress = address;
	}

	// ---------------------- log file name ----------------------

	/**
	 * log filename (new format), like "logPath/yyyy-MM-dd/job-7.log"
	 * or with isolation: "logPath/yyyy-MM-dd/{address}/job-7.log"
	 *
	 * @param triggerDate trigger date
	 * @param jobId       job id
	 * @return log file name
	 */
	public static String makeLogFileName(Date triggerDate, int jobId) {
		// "filePath/yyyy-MM-dd" or "filePath/yyyy-MM-dd/{address}"
		File logFilePath = new File(getLogPath(), DateTool.formatDate(triggerDate));
		if (logIsolatedByAddress && executorAddress != null && !executorAddress.isEmpty()) {
			// extract ip_port from address (strip protocol and trailing slash)
			String addressDir = executorAddress;
			if (addressDir.contains("://")) {
				addressDir = addressDir.substring(addressDir.indexOf("://") + 3);
			}
			if (addressDir.endsWith("/")) {
				addressDir = addressDir.substring(0, addressDir.length() - 1);
			}
			addressDir = addressDir.replace(':', '_');
			logFilePath = new File(logFilePath, addressDir);
		}
		try {
			FileTool.createDirectories(logFilePath);
		} catch (IOException e) {
			throw new RuntimeException("XxlJobFileAppender makeLogFileName error, logFilePath:" + logFilePath.getPath(), e);
		}

		// filePath/yyyy-MM-dd/job-7.log
		return logFilePath.getPath()
				.concat(File.separator)
				.concat("job-")
				.concat(String.valueOf(jobId))
				.concat(".log");
	}

	/**
	 * log filename (legacy format), like "logPath/yyyy-MM-dd/9999.log"
	 *
	 * @param triggerDate trigger date
	 * @param logId       log id
	 * @return log file name
	 */
	public static String makeLogFileNameLegacy(Date triggerDate, long logId) {
		// "filePath/yyyy-MM-dd"
		File logFilePath = new File(getLogPath(), DateTool.formatDate(triggerDate));
		try {
			FileTool.createDirectories(logFilePath);
		} catch (IOException e) {
			throw new RuntimeException("XxlJobFileAppender makeLogFileNameLegacy error, logFilePath:" + logFilePath.getPath(), e);
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
	 * @param logFileName	log file name
	 * @param appendLog		append log
	 */
	public static void appendLog(String logFileName, String appendLog) {

		// valid
		if (StringTool.isBlank(logFileName) || appendLog == null) {
			return;
		}

		// append log
        try {
            FileTool.writeLines(logFileName, List.of(appendLog), true);
        } catch (IOException e) {
            throw new RuntimeException("XxlJobFileAppender appendLog error, logFileName:"+ logFileName, e);
        }
	}

	/**
	 * append START marker and write index entry
	 *
	 * @param logFileName log file name
	 * @param logId       log id
	 */
	public static void appendLogStart(String logFileName, long logId) {
		// 1. get current file size as byte offset for index
		File logFile = new File(logFileName);
		long offset = logFile.exists() ? logFile.length() : 0;

		// 2. write START marker to log file
		appendLog(logFileName, LOG_SEGMENT_START_PREFIX + logId + LOG_SEGMENT_START_SUFFIX);

		// 3. append index record to .idx file
		String idxFileName = logFileName.replace(".log", ".idx");
		try {
			FileTool.writeLines(idxFileName, List.of(logId + "," + offset), true);
		} catch (IOException e) {
			logger.error("XxlJobFileAppender appendLogStart write index error, idxFileName:{}", idxFileName, e);
		}
	}

	/**
	 * append END marker
	 *
	 * @param logFileName log file name
	 * @param logId       log id
	 */
	public static void appendLogEnd(String logFileName, long logId) {
		appendLog(logFileName, LOG_SEGMENT_END_PREFIX + logId + LOG_SEGMENT_END_SUFFIX);
	}

	// ---------------------- read log ----------------------

	/**
	 * read log by logId from merged log file, using index for fast seek
	 *
	 * @param logFileName log file name
	 * @param logId       log id to locate segment
	 * @param fromLineNum from line num (within the segment, start as 1)
	 * @return log data
	 */
	public static LogData readLogByLogId(String logFileName, long logId, int fromLineNum) {
		// valid
		if (StringTool.isBlank(logFileName)) {
			return new LogData(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		if (!FileTool.exists(logFileName)) {
			return new LogData(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// 1. lookup offset from index
		String idxFileName = logFileName.replace(".log", ".idx");
		long byteOffset = lookupOffset(idxFileName, logId);

		// 2. read log segment
		String startMarker = LOG_SEGMENT_START_PREFIX + logId + LOG_SEGMENT_START_SUFFIX;
		String endMarker = LOG_SEGMENT_END_PREFIX + logId + LOG_SEGMENT_END_SUFFIX;

		StringBuilder logContentBuilder = new StringBuilder();
		int segmentLineNum = 0;
		int toLineNum = 0;
		boolean foundStart = false;
		boolean isEnd = false;

		try (RandomAccessFile raf = new RandomAccessFile(logFileName, "r");
			 BufferedReader reader = new BufferedReader(
					 new InputStreamReader(new FileInputStream(raf.getFD()), StandardCharsets.UTF_8))) {

			// seek to offset if available
			if (byteOffset > 0) {
				raf.seek(byteOffset);
			}

			String line;
			while ((line = reader.readLine()) != null) {
				// look for START marker
				if (!foundStart) {
					if (line.equals(startMarker)) {
						foundStart = true;
					}
					continue;
				}

				// check END conditions
				if (line.equals(endMarker)) {
					// condition a) normal end - mark as end but continue reading until next START
					isEnd = true;
					continue;
				}
				if (line.startsWith(LOG_SEGMENT_START_PREFIX) && line.endsWith(LOG_SEGMENT_START_SUFFIX)) {
					// condition b) next segment started
					break;
				}

				// collect content lines
				segmentLineNum++;
				if (segmentLineNum >= fromLineNum) {
					toLineNum = segmentLineNum;
					logContentBuilder.append(line).append(System.lineSeparator());
				}
			}

			// if START marker not found at offset position, fallback to full scan
			if (!foundStart && byteOffset > 0) {
				return readLogByLogIdFullScan(logFileName, logId, fromLineNum);
			}

		} catch (IOException e) {
			logger.error("XxlJobFileAppender readLogByLogId error, logFileName:{}, logId:{}", logFileName, logId, e);
		}

		// if START not found at all
		if (!foundStart) {
			return new LogData(fromLineNum, 0, "readLog fail, logId segment not found", true);
		}

		return new LogData(fromLineNum, toLineNum, logContentBuilder.toString(), isEnd);
	}

	/**
	 * fallback: read log by logId with full file scan (when index is unavailable or invalid)
	 */
	private static LogData readLogByLogIdFullScan(String logFileName, long logId, int fromLineNum) {
		String startMarker = LOG_SEGMENT_START_PREFIX + logId + LOG_SEGMENT_START_SUFFIX;
		String endMarker = LOG_SEGMENT_END_PREFIX + logId + LOG_SEGMENT_END_SUFFIX;

		StringBuilder logContentBuilder = new StringBuilder();
		int segmentLineNum = 0;
		int toLineNum = 0;
		boolean foundStart = false;
		boolean isEnd = false;

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(logFileName), StandardCharsets.UTF_8))) {

			String line;
			while ((line = reader.readLine()) != null) {
				if (!foundStart) {
					if (line.equals(startMarker)) {
						foundStart = true;
					}
					continue;
				}

				if (line.equals(endMarker)) {
					isEnd = true;
					continue;
				}
				if (line.startsWith(LOG_SEGMENT_START_PREFIX) && line.endsWith(LOG_SEGMENT_START_SUFFIX)) {
					break;
				}

				segmentLineNum++;
				if (segmentLineNum >= fromLineNum) {
					toLineNum = segmentLineNum;
					logContentBuilder.append(line).append(System.lineSeparator());
				}
			}
		} catch (IOException e) {
			logger.error("XxlJobFileAppender readLogByLogIdFullScan error, logFileName:{}, logId:{}", logFileName, logId, e);
		}

		if (!foundStart) {
			return new LogData(fromLineNum, 0, "readLog fail, logId segment not found", true);
		}

		return new LogData(fromLineNum, toLineNum, logContentBuilder.toString(), isEnd);
	}

	/**
	 * lookup byte offset from index file
	 *
	 * @param idxFileName index file name
	 * @param logId       log id to lookup
	 * @return byte offset, or -1 if not found
	 */
	private static long lookupOffset(String idxFileName, long logId) {
		if (!FileTool.exists(idxFileName)) {
			return -1;
		}

		String targetPrefix = logId + ",";
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(idxFileName), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(targetPrefix)) {
					return Long.parseLong(line.substring(targetPrefix.length()));
				}
			}
		} catch (IOException | NumberFormatException e) {
			logger.error("XxlJobFileAppender lookupOffset error, idxFileName:{}, logId:{}", idxFileName, logId, e);
		}
		return -1;
	}

	// ---------------------- legacy read log (for backward compatibility) ----------------------

	/**
	 * support read log-file (legacy: one file per execution)
	 *
	 * @param logFileName	log file name
	 * @param fromLineNum	from line num
	 * @return log content
	 */
	public static LogData readLog(String logFileName, final int fromLineNum){

		// valid
		if (StringTool.isBlank(logFileName)) {
            return new LogData(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		if (!FileTool.exists(logFileName)) {
            return new LogData(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// read data
        StringBuilder logContentBuilder = new StringBuilder();
        // num: [from, to], start as 1
        AtomicInteger toLineNum = new AtomicInteger(0);
        AtomicInteger currentLineNum = new AtomicInteger(0);
        /*int readLineCount = 0;*/

        // do read
        try {
            FileTool.readLines(logFileName, new Consumer<String>() {
                @Override
                public void accept(String line) {
                    // refresh line num
                    currentLineNum.incrementAndGet();

                    // valid
                    if (currentLineNum.get() < fromLineNum) {
                        return;
                    }

                    // Limit return less than 1000 rows per query request	// todo
                    /*if(++readLineCount >= 1000) {
                        break;
                    }*/

                    // collect line data
                    toLineNum.set(currentLineNum.get());
                    logContentBuilder.append(line).append(System.lineSeparator());      // [from, to], start as 1
                }
            });
        } catch (IOException e) {
            logger.error("XxlJobFileAppender readLog error, logFileName:{}, fromLineNum:{}", logFileName, fromLineNum, e);
        }

        // result
        return new LogData(fromLineNum, toLineNum.get(), logContentBuilder.toString(), false);
	}

}

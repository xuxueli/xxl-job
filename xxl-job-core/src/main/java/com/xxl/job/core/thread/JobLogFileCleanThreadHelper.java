package com.xxl.job.core.thread;

import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.tool.concurrent.CyclicThread;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.io.FileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * job file clean thread
 *
 * @author xuxueli 2017-12-29 16:23:43
 */
public class JobLogFileCleanThreadHelper {
    private static final Logger logger = LoggerFactory.getLogger(JobLogFileCleanThreadHelper.class);


    /**
     * monitor thread
     */
    private CyclicThread logFileCleanThread;


    /**
     * start
     */
    public void start(final long logRetentionDays){

        /**
         * limit min value
          */
        if (logRetentionDays < 3 ) {
            return;     // effective only when logRetentionDays >= 3
        }

        /**
         * logFileCleanThread
         */
        logFileCleanThread = new CyclicThread("JobLogFileCleanThreadHelper#logFileCleanThread", true, new Runnable() {
            @Override
            public void run() {
                // clean log dir, over logRetentionDays
                File[] childDirs = new File(XxlJobFileAppender.getLogPath()).listFiles();
                if (childDirs!=null && childDirs.length>0) {

                    // today
                    Calendar todayCal = Calendar.getInstance();
                    todayCal.set(Calendar.HOUR_OF_DAY,0);
                    todayCal.set(Calendar.MINUTE,0);
                    todayCal.set(Calendar.SECOND,0);
                    todayCal.set(Calendar.MILLISECOND,0);

                    Date todayDate = todayCal.getTime();

                    // clean expired logfile
                    for (File childFile: childDirs) {

                        // valid log-path: must be directory
                        if (!childFile.isDirectory()) {
                            continue;
                        }

                        // valid day log-path: like "---/2017-12-25/639.log"
                        if (!childFile.getName().contains("-")) {
                            continue;
                        }

                        // parse create-day of file-path
                        Date logFileCreateDate = null;
                        try {
                            logFileCreateDate = DateTool.parseDate(childFile.getName());
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                        if (logFileCreateDate == null) {
                            continue;
                        }

                        // check expired
                        Date expiredDate = DateTool.addDays(logFileCreateDate, logRetentionDays);
                        if (todayDate.getTime() > expiredDate.getTime()) {
                            // expired, remove all log of this day
                            FileTool.delete(childFile);
                            //FileUtil.deleteRecursively(childFile);
                        }
                    }
                }
            }
        }, DateTool.MILLIS_PER_DAY, true);
        logFileCleanThread.start();

    }

    /**
     * stop
     */
    public void stop() {

        // stop logFileCleanThread
        logFileCleanThread.stop();
    }

}

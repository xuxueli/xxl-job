package com.xxl.job.core.thread;

import com.xxl.job.core.log.XxlJobFileAppender;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.io.FileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * job file clean thread
 *
 * @author xuxueli 2017-12-29 16:23:43
 */
public class JobLogFileCleanThread {
    private static Logger logger = LoggerFactory.getLogger(JobLogFileCleanThread.class);

    private static JobLogFileCleanThread instance = new JobLogFileCleanThread();
    public static JobLogFileCleanThread getInstance(){
        return instance;
    }

    private Thread localThread;
    private volatile boolean toStop = false;
    public void start(final long logRetentionDays){

        // limit min value
        if (logRetentionDays < 3 ) {
            return;     // effective only when logRetentionDays >= 3
        }

        localThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
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

                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    try {
                        TimeUnit.DAYS.sleep(1);
                    } catch (Throwable e) {
                        if (!toStop) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                logger.info(">>>>>>>>>>> xxl-job, executor JobLogFileCleanThread thread destroy.");

            }
        });
        localThread.setDaemon(true);
        localThread.setName("xxl-job, executor JobLogFileCleanThread");
        localThread.start();
    }

    public void toStop() {
        toStop = true;

        if (localThread == null) {
            return;
        }

        // interrupt and wait
        localThread.interrupt();
        try {
            localThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}

package com.xxl.job.executor.factory.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.executor.utils.JobLogUtils;
import com.xxl.job.spring.boot.autoconfigure.XxlJobExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 日志文件清理线程
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class JobLogFileCleanThread extends BaseTaskThread {

    @Autowired
    private XxlJobExecutorProperties xxlJobExecutorProperties;

    private Thread localThread;
    private volatile boolean toStop = false;

    @Override
    public void start() {

        localThread = newThread(() -> {

            log.info(">>>>>>>>>>> xxl-job, executor JobLogFileCleanThread thread running.");

            while (!toStop) {
                try {
                    // clean log dir, over logRetentionDays
                    File[] childDirs = new File(JobLogUtils.getLogPath()).listFiles();
                    if (childDirs != null && childDirs.length > 0) {

                        // today
                        Calendar todayCal = Calendar.getInstance();
                        todayCal.set(Calendar.HOUR_OF_DAY, 0);
                        todayCal.set(Calendar.MINUTE, 0);
                        todayCal.set(Calendar.SECOND, 0);
                        todayCal.set(Calendar.MILLISECOND, 0);

                        Date todayDate = todayCal.getTime();

                        for (File childFile : childDirs) {

                            // valid
                            if (!childFile.isDirectory()) {
                                continue;
                            }
                            if (childFile.getName().indexOf("-") == -1) {
                                continue;
                            }

                            // file create date
                            Date logFileCreateDate = null;
                            try {
                                logFileCreateDate = DateUtil.parseDate(childFile.getName());
                            }catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }

                            if (ObjectUtil.isNull(logFileCreateDate)) {
                                continue;
                            }

                            if ((todayDate.getTime() - logFileCreateDate.getTime()) >= xxlJobExecutorProperties.getExecutor().getLogRetentionDays() * (24 * 60 * 60 * 1000)) {
                                FileUtil.del(childFile);
                            }
                        }
                    }

                } catch (Exception e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }

                }

                try {
                    TimeUnit.DAYS.sleep(1);
                } catch (InterruptedException e) {
                    if (!toStop) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info(">>>>>>>>>>> xxl-job, executor JobLogFileCleanThread thread destroy.");
        }, "xxl-job, executor JobLogFileCleanThread");
    }

    @Override
    public void stop() {
        toStop = true;
        ThreadUtil.interrupt(localThread, Boolean.TRUE);
    }

}

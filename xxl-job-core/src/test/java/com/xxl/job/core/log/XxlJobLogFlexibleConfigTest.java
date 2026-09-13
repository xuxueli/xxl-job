package com.xxl.job.core.log;

import com.xxl.job.core.thread.JobLogFileCleanThreadHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 日志灵活配置测试（对应 issue #4001）：
 * 1、logRetentionDays 支持设置为 1（此前下限为 3，低于 3 时清理线程不启动，永不清理）；
 * 2、logEnabled=false 时执行日志不再写文件，高频任务不会产生海量小文件。
 */
public class XxlJobLogFlexibleConfigTest {

    @Test
    public void appendLog_shouldCreateNoFileWhenLogDisabled() throws Exception {
        File base = Files.createTempDirectory("xxljob-log-disabled").toFile();
        try {
            XxlJobFileAppender.setLogEnabled(false);
            XxlJobFileAppender.initLogPath(base.getPath());
            String logFileName = XxlJobFileAppender.makeLogFileName(new Date(), 1);

            XxlJobFileAppender.appendLog(logFileName, "should not be written");

            assertFalse(new File(logFileName).exists(), "no log file should be created when log disabled");

            // 重新开启后恢复写入
            XxlJobFileAppender.setLogEnabled(true);
            XxlJobFileAppender.appendLog(logFileName, "written after re-enabled");
            assertTrue(new File(logFileName).exists(), "log file should be created when log re-enabled");
        } finally {
            XxlJobFileAppender.setLogEnabled(true);
        }
    }

    @Test
    public void cleanThread_shouldStartWithRetentionDaysOne() throws Exception {
        JobLogFileCleanThreadHelper helper = new JobLogFileCleanThreadHelper();
        helper.start(1);
        try {
            Field f = JobLogFileCleanThreadHelper.class.getDeclaredField("logFileCleanThread");
            f.setAccessible(true);
            assertNotNull(f.get(helper), "clean thread should start when logRetentionDays=1");
        } finally {
            helper.stop();
        }
    }

    @Test
    public void cleanThread_shouldNotStartWithRetentionDaysZero() throws Exception {
        JobLogFileCleanThreadHelper helper = new JobLogFileCleanThreadHelper();
        helper.start(0);
        try {
            Field f = JobLogFileCleanThreadHelper.class.getDeclaredField("logFileCleanThread");
            f.setAccessible(true);
            assertNull(f.get(helper), "clean thread should not start when logRetentionDays=0");
        } finally {
            helper.stop();
        }
    }
}

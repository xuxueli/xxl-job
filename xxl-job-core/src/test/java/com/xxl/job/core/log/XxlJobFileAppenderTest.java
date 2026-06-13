package com.xxl.job.core.log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XxlJobFileAppenderTest {

    @Test
    void callbacklogs_test(@TempDir Path tempDir) throws IOException {
        String customLogPath = tempDir.toFile().getPath();

        XxlJobFileAppender.initLogPath(customLogPath);

        File expectedCallbackDir = new File(customLogPath, "callbacklogs");
        assertEquals(expectedCallbackDir.getPath(), XxlJobFileAppender.getCallbackLogPath(),
                "callbacklogs should be located below the user-specified directory");
    }

    @Test
    void gluesource_test(@TempDir Path tempDir) throws IOException {
        String customLogPath = tempDir.toFile().getPath();

        XxlJobFileAppender.initLogPath(customLogPath);

        File expectedGlueDir = new File(customLogPath, "gluesource");
        assertEquals(expectedGlueDir.getPath(), XxlJobFileAppender.getGlueSrcPath(),
                "gluesource should be located below the user-specified directory");
    }
}

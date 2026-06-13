/*
 * Copyright 1999-2026 xuxueli.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xxl.job.core.log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for issue #3959:
 * initLogPath should refresh callbackLogPath together with logBasePath and glueSrcPath.
 */
class XxlJobFileAppenderTest {

    @Test
    void initLogPath_shouldRefreshCallbackLogPathRelativeToCustomLogBase(@TempDir Path tempDir) throws IOException {
        String customLogPath = tempDir.toFile().getPath();

        XxlJobFileAppender.initLogPath(customLogPath);

        File expectedCallbackDir = new File(customLogPath, "callbacklogs");
        assertEquals(expectedCallbackDir.getPath(), XxlJobFileAppender.getCallbackLogPath(),
                "callbackLogPath should sit under the user-supplied logPath after initLogPath");
    }

    @Test
    void initLogPath_shouldRefreshGlueSrcPathRelativeToCustomLogBase(@TempDir Path tempDir) throws IOException {
        String customLogPath = tempDir.toFile().getPath();

        XxlJobFileAppender.initLogPath(customLogPath);

        File expectedGlueDir = new File(customLogPath, "gluesource");
        assertEquals(expectedGlueDir.getPath(), XxlJobFileAppender.getGlueSrcPath());
        assertTrue(expectedGlueDir.exists(), "gluesource directory should be created under custom logPath");
    }
}

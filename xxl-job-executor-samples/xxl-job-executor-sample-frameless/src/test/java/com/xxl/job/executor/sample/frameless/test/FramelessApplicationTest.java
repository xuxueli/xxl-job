package com.xxl.job.executor.sample.frameless.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Testable
public class FramelessApplicationTest {
    private static final Logger logger = LoggerFactory.getLogger(FramelessApplicationTest.class);

    @Test
    @DisplayName("test1")
    public void test1(){
        logger.info("111");
        Assertions.assertNull( null);
    }

}

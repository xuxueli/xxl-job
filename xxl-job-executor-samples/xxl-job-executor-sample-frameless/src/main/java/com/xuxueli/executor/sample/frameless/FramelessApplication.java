package com.xuxueli.executor.sample.frameless;

import com.xuxueli.executor.sample.frameless.config.FrameLessXxlJobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2018-10-31 19:05:43
 */
public class FramelessApplication {
    private static Logger logger = LoggerFactory.getLogger(FramelessApplication.class);

    public static void main(String[] args) {

        try {
            // start
            FrameLessXxlJobConfig.getInstance().initXxlJobExecutor();

            // Blocks until interrupted
            while (true) {
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            // destory
            FrameLessXxlJobConfig.getInstance().destoryXxlJobExecutor();
        }

    }

}

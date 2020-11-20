package com.xxl.job.executor;

import com.xxl.job.core.autoconfigure.XxlJobAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
@Import({XxlJobAutoConfiguration.class})
public class XxlJobExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlJobExecutorApplication.class, args);
    }

}
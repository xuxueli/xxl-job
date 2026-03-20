package com.xxl.job.writing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 写作任务协作管理平台启动类
 */
@SpringBootApplication
@EnableScheduling
public class WritingPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(WritingPlatformApplication.class, args);
    }
}
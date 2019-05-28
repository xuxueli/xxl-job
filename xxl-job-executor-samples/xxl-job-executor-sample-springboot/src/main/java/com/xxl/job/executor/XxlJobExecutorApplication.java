package com.xxl.job.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
@EnableFeignClients
public class XxlJobExecutorApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlJobExecutorApplication.class, args);
	}

}
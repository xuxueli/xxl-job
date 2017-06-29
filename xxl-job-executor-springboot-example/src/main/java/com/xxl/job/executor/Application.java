package com.xxl.job.executor;

import com.xxl.job.executor.properties.XxlJobProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(XxlJobProperties.class)
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
	}

}
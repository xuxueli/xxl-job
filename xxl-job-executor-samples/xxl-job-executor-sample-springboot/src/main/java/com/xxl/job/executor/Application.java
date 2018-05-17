package com.xxl.job.executor;

import com.xxl.job.core.annotationtask.annotations.XxlScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@XxlScan("com.xxl.job.executor.model.xxl")
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
	}

}
package com.xxl.job.spring.boot.autoconfigure;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.spring.boot.autoconfigure.service.jobhandler.DemoJobHandler;

//@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {



		XxlJobExecutor exe = new XxlJobExecutor("", 9999, "testmain", "http://localhost:8080/", "/data/testmain");

		XxlJobExecutor.registJobHandler("tttt", new DemoJobHandler());
		exe.start();

		Thread.sleep(1000000000);
	}

}
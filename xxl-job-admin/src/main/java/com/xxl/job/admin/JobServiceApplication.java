package com.xxl.job.admin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * User: Joni
 * Email: joni@efbiz.org
 * Date: 2017/3/29
 */
@SpringBootApplication
@ComponentScan(basePackages={"com.xxl.job.admin.controller","com.xxl.job.admin.service", "com.xxl.job.admin.dao", "com.xxl.job.admin.config"})
public class JobServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JobServiceApplication.class, args);
    }

}

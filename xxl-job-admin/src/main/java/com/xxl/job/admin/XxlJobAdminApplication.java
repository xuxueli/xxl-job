package com.xxl.job.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xxl.job")
@MapperScan(basePackages = {"com.xxl.job.admin.mapper"})
public class XxlJobAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
    }

}
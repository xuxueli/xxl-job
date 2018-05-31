package com.xxl.job.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Johnny on 2018/5/17.
 */
@SpringBootApplication
@MapperScan(basePackages = "com.xxl.job.admin.dao")
public class JobAdminStart {


    public static void main(String[] args) {
        SpringApplication.run(JobAdminStart.class, args);
    }
}

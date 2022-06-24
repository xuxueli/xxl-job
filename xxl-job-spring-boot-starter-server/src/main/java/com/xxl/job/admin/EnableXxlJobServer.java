package com.xxl.job.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan(basePackages = {"com.xxl.job.admin"})
@MapperScan(basePackages = {"com.xxl.job.admin.dao"})
@PropertySource(value = "classpath:xxl-job-default.properties")
@Documented
public @interface EnableXxlJobServer {
}
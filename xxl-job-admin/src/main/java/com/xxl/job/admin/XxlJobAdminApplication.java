package com.xxl.job.admin;

import com.xxl.job.admin.slf4j.WarBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
public class XxlJobAdminApplication extends WarBootApplication {

	public static void main(String[] args) {
        startup(XxlJobAdminApplication.class, args);
	}

}

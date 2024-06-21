package com.xxl.job.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.context.WebServerPortFileWriter;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
public class XxlJobAdminApplication {

	public static void main(String[] args) {
	    SpringApplication app = new SpringApplication(XxlJobAdminApplication.class);
	    app.addListeners(new ApplicationPidFileWriter(), new WebServerPortFileWriter());
	    app.run(args);
      }

}

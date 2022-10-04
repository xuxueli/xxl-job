package com.xxl.job.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.xxl.job.admin.core.util.EntityUtil;
import com.xxl.job.admin.service.SystemService;

/**
 * 
 * @author spenggch
 *
 */
@Component
public class XxlJobApplicationRunner implements ApplicationRunner {

	@Autowired
	SystemService systemService;

	@Value("${xxl.job.table.prefix:}")
	String xxlJobTablePrefix;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		EntityUtil.setTablePrefix(xxlJobTablePrefix);
		systemService.initializeData();
	}

}

package com.xxl.job.core.extension.server;

import com.xxl.job.admin.XxlJobAdminApplication;
import com.xxl.job.core.extension.server.controller.JobAutoRegisterController;
import com.xxl.job.core.extension.server.service.JobAutoRegisterService;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author lesl
 */
@Slf4j
@Configuration
@Import({JobAutoRegisterController.class, JobAutoRegisterService.class})
@ConditionalOnClass(XxlJobAdminApplication.class)
public class RegisterServer {
	@PostConstruct
	public void init(){
		log.info(" >>>>>>>>>>>>>>>>>> job-auto-register extension started.");
	}
}

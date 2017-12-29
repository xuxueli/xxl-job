package com.xuxueli.executor.sample.nutz.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * nutz setup
 *
 * @author xuxueli 2017-12-25 17:58:43
 */
public class NutzSetup implements Setup {
	private Logger logger = LoggerFactory.getLogger(NutzSetup.class);
	//public static final Log logger = Logs.get();

	private XxlJobExecutor xxlJobExecutor = null;

	@Override
	public void init(NutConfig cfg) {

		// regist JobHandler
		String[] beanNames = cfg.getIoc().getNamesByType(IJobHandler.class);
		if (beanNames==null || beanNames.length==0) {
			return;
		}
		for (String beanName : beanNames) {
			IJobHandler jobHandler = cfg.getIoc().get(IJobHandler.class, beanName);
			String name = jobHandler.getClass().getAnnotation(JobHandler.class).value();
			XxlJobExecutor.registJobHandler(name, jobHandler);
		}

		// load executor prop
		PropertiesProxy xxlJobProp = new PropertiesProxy("xxl-job-executor.properties");

		// init executor
		xxlJobExecutor = new XxlJobExecutor();
		xxlJobExecutor.setAdminAddresses(xxlJobProp.get("xxl.job.admin.addresses"));
		xxlJobExecutor.setAppName(xxlJobProp.get("xxl.job.executor.appname"));
		xxlJobExecutor.setIp(xxlJobProp.get("xxl.job.executor.ip"));
		xxlJobExecutor.setPort(xxlJobProp.getInt("xxl.job.executor.port"));
		xxlJobExecutor.setAccessToken(xxlJobProp.get("xxl.job.accessToken"));
		xxlJobExecutor.setLogPath(xxlJobProp.get("xxl.job.executor.logpath"));
		xxlJobExecutor.setLogRetentionDays(xxlJobProp.getInt("xxl.job.executor.logretentiondays"));

		// start executor
		try {
			xxlJobExecutor.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void destroy(NutConfig cfg) {
		if (xxlJobExecutor != null) {
			xxlJobExecutor.destroy();
		}
	}

}

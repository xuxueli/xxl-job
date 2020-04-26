package com.xuxueli.executor.sample.nutz.config;

import com.xuxueli.executor.sample.nutz.jobhandler.CommandJobHandler;
import com.xuxueli.executor.sample.nutz.jobhandler.DemoJobHandler;
import com.xuxueli.executor.sample.nutz.jobhandler.HttpJobHandler;
import com.xuxueli.executor.sample.nutz.jobhandler.ShardingJobHandler;
import com.xxl.job.core.executor.XxlJobExecutor;
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

	private XxlJobExecutor xxlJobExecutor = null;

	@Override
	public void init(NutConfig cfg) {

		// registry jobhandler
		XxlJobExecutor.registJobHandler("demoJobHandler", new DemoJobHandler());
		XxlJobExecutor.registJobHandler("shardingJobHandler", new ShardingJobHandler());
		XxlJobExecutor.registJobHandler("httpJobHandler", new HttpJobHandler());
		XxlJobExecutor.registJobHandler("commandJobHandler", new CommandJobHandler());

		// load executor prop
		PropertiesProxy xxlJobProp = new PropertiesProxy("xxl-job-executor.properties");

		// init executor
		xxlJobExecutor = new XxlJobExecutor();
		xxlJobExecutor.setAdminAddresses(xxlJobProp.get("xxl.job.admin.addresses"));
		xxlJobExecutor.setAccessToken(xxlJobProp.get("xxl.job.accessToken"));
		xxlJobExecutor.setAppname(xxlJobProp.get("xxl.job.executor.appname"));
		xxlJobExecutor.setAddress(xxlJobProp.get("xxl.job.executor.address"));
		xxlJobExecutor.setIp(xxlJobProp.get("xxl.job.executor.ip"));
		xxlJobExecutor.setPort(xxlJobProp.getInt("xxl.job.executor.port"));
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

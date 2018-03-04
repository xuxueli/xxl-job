package com.xuxueli.executor.sample.jfinal.config;

import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.xuxueli.executor.sample.jfinal.controller.IndexController;
import com.xuxueli.executor.sample.jfinal.jobhandler.DemoJobHandler;
import com.xuxueli.executor.sample.jfinal.jobhandler.ShardingJobHandler;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuxueli 2017-08-11 14:17:41
 */
public class JFinalCoreConfig extends JFinalConfig {
	private Logger logger = LoggerFactory.getLogger(JFinalCoreConfig.class);

	// ---------------------- xxl-job executor ----------------------
	private XxlJobExecutor xxlJobExecutor = null;
	private void initXxlJobExecutor() {

		// registry jobhandler
		XxlJobExecutor.registJobHandler("demoJobHandler", new DemoJobHandler());
		XxlJobExecutor.registJobHandler("shardingJobHandler", new ShardingJobHandler());

		// load executor prop
		Prop xxlJobProp = PropKit.use("xxl-job-executor.properties");

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
	private void destoryXxlJobExecutor() {
		if (xxlJobExecutor != null) {
			xxlJobExecutor.destroy();
		}
	}

	// ---------------------- jfinal ----------------------

	public void configRoute(Routes route) {
		route.add("/", IndexController.class);
	}

	@Override
	public void afterJFinalStart() {
		initXxlJobExecutor();
	}

	@Override
	public void beforeJFinalStop() {
		destoryXxlJobExecutor();
	}

	public void configConstant(Constants constants) {

	}

	public void configPlugin(Plugins plugins) {

	}

	public void configInterceptor(Interceptors interceptors) {

	}

	public void configHandler(Handlers handlers) {

	}


}

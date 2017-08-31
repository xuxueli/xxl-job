package com.xuxueli.executor.sample.nutz;

import org.nutz.ioc.IocException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;

/**
 * 
 * @author 邓华锋
 *
 */
public class MainSetup implements Setup {
	public static final Log log = Logs.get();
	XxlJobExecutor xxlJobExecutor = null;

	@Override
	public void init(NutConfig cfg) {
		// 通用注册IJobHandler
		String[] names = cfg.getIoc().getNamesByType(IJobHandler.class);
		for (String name : names) {
			XxlJobExecutor.registJobHandler(name, cfg.getIoc().get(IJobHandler.class, name));
		}
		// load executor prop
		PropertiesProxy xxlJobProp = cfg.getIoc().get(PropertiesProxy.class, "conf");

		// init executor
		xxlJobExecutor = new XxlJobExecutor();
		xxlJobExecutor.setIp(xxlJobProp.get("xxl.job.executor.ip"));
		xxlJobExecutor.setPort(xxlJobProp.getInt("xxl.job.executor.port"));
		xxlJobExecutor.setAppName(xxlJobProp.get("xxl.job.executor.appname"));
		xxlJobExecutor.setAdminAddresses(xxlJobProp.get("xxl.job.admin.addresses"));
		xxlJobExecutor.setLogPath(xxlJobProp.get("xxl.job.executor.logpath"));
		xxlJobExecutor.setAccessToken(xxlJobProp.get("xxl.job.accessToken"));

		// start executor
		try {
			xxlJobExecutor.start();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void destroy(NutConfig cfg) {
		if (xxlJobExecutor != null) {
			xxlJobExecutor.destroy();
		}
	}
}

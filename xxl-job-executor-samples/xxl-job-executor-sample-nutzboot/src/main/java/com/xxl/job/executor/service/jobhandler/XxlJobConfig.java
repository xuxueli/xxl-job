package com.xxl.job.executor.service.jobhandler;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.xxl.job.core.executor.XxlJobExecutor;

/**
 * xxl-job config
 *
 * @author wendal 2017-12-27
 */
@IocBean
public class XxlJobConfig {

    private static Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @IocBean(create = "start", depose = "destroy")
    public XxlJobExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        // 下列参数均为必填项,声明在application.properties
        xxlJobExecutor.setIp(conf.check("xxl.job.executor.ip"));
        xxlJobExecutor.setPort(conf.getInt("xxl.job.executor.port"));
        xxlJobExecutor.setAppName(conf.check("xxl.job.executor.appname"));
        xxlJobExecutor.setAdminAddresses(conf.check("xxl.job.admin.addresses"));
        xxlJobExecutor.setLogPath(conf.check("xxl.job.executor.logpath"));
        xxlJobExecutor.setAccessToken(conf.check("xxl.job.accessToken"));
        return xxlJobExecutor;
    }

}
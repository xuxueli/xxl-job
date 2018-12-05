package com.xuxueli.executor.sample.frameless.config;

import com.xuxueli.executor.sample.frameless.jobhandler.CommandJobHandler;
import com.xuxueli.executor.sample.frameless.jobhandler.DemoJobHandler;
import com.xuxueli.executor.sample.frameless.jobhandler.HttpJobHandler;
import com.xuxueli.executor.sample.frameless.jobhandler.ShardingJobHandler;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author xuxueli 2018-10-31 19:05:43
 */
public class FrameLessXxlJobConfig {
    private static Logger logger = LoggerFactory.getLogger(FrameLessXxlJobConfig.class);


    private static FrameLessXxlJobConfig instance = new FrameLessXxlJobConfig();
    public static FrameLessXxlJobConfig getInstance() {
        return instance;
    }


    private XxlJobExecutor xxlJobExecutor = null;

    /**
     * init
     */
    public void initXxlJobExecutor() {

        // registry jobhandler
        XxlJobExecutor.registJobHandler("demoJobHandler", new DemoJobHandler());
        XxlJobExecutor.registJobHandler("shardingJobHandler", new ShardingJobHandler());
        XxlJobExecutor.registJobHandler("httpJobHandler", new HttpJobHandler());
        XxlJobExecutor.registJobHandler("commandJobHandler", new CommandJobHandler());

        // load executor prop
        Properties xxlJobProp = loadProperties("xxl-job-executor.properties");


        // init executor
        xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setAdminAddresses(xxlJobProp.getProperty("xxl.job.admin.addresses"));
        xxlJobExecutor.setAppName(xxlJobProp.getProperty("xxl.job.executor.appname"));
        xxlJobExecutor.setIp(xxlJobProp.getProperty("xxl.job.executor.ip"));
        xxlJobExecutor.setPort(Integer.valueOf(xxlJobProp.getProperty("xxl.job.executor.port")));
        xxlJobExecutor.setAccessToken(xxlJobProp.getProperty("xxl.job.accessToken"));
        xxlJobExecutor.setLogPath(xxlJobProp.getProperty("xxl.job.executor.logpath"));
        xxlJobExecutor.setLogRetentionDays(Integer.valueOf(xxlJobProp.getProperty("xxl.job.executor.logretentiondays")));

        // start executor
        try {
            xxlJobExecutor.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * destory
     */
    public void destoryXxlJobExecutor() {
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }


    public static Properties loadProperties(String propertyFileName) {
        InputStreamReader in = null;
        try {
            ClassLoader loder = Thread.currentThread().getContextClassLoader();

            in = new InputStreamReader(loder.getResourceAsStream(propertyFileName), "UTF-8");;
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                return prop;
            }
        } catch (IOException e) {
            logger.error("load {} error!", propertyFileName);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("close {} error!", propertyFileName);
                }
            }
        }
        return null;
    }

}

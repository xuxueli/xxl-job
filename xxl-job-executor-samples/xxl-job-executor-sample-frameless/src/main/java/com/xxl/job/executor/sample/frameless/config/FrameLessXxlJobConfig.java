package com.xxl.job.executor.sample.frameless.config;

import com.xxl.job.executor.sample.frameless.jobhandler.SampleXxlJob;
import com.xxl.job.core.executor.impl.XxlJobSimpleExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
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

    private XxlJobSimpleExecutor xxlJobExecutor = null;

    /**
     * init
     */
    public void initXxlJobExecutor() {
        // load executor prop
        Properties xxlJobProp = loadProperties("xxl-job-executor.properties");

        // init executor
        xxlJobExecutor = new XxlJobSimpleExecutor();

        String envAppname = System.getenv("XXL_JOB_EXECUTOR_APPNAME");
        if (envAppname == null) {
            envAppname = xxlJobProp.getProperty("xxl.job.executor.appname");
        }
        xxlJobExecutor.setAppname(envAppname);

        String envPort = System.getenv("XXL_JOB_EXECUTOR_PORT");
        if (envPort == null) {
            envPort = xxlJobProp.getProperty("xxl.job.executor.port");
        }
        xxlJobExecutor.setPort(Integer.valueOf(envPort));

        String envAdminAddresses = System.getenv("XXL_JOB_ADMIN_ADDRESSES");
        if (envAdminAddresses == null) {
            envAdminAddresses = xxlJobProp.getProperty("xxl.job.admin.addresses");
        }
        xxlJobExecutor.setAdminAddresses(envAdminAddresses);

        String accessToken = System.getenv("XXL_JOB_ACCESS_TOKEN");
        if (accessToken == null) {
            accessToken = xxlJobProp.getProperty("xxl.job.accessToken");
        }
        xxlJobExecutor.setAccessToken(accessToken);

        String envAddress = System.getenv("XXL_JOB_EXECUTOR_ADDRESS");
        if (envAddress == null) {
            envAddress = xxlJobProp.getProperty("xxl.job.executor.address");
        }
        xxlJobExecutor.setAddress(envAddress);

        String envIp = System.getenv("XXL_JOB_EXECUTOR_IP");
        if (envIp == null) {
            envIp = xxlJobProp.getProperty("xxl.job.executor.ip");
        }
        xxlJobExecutor.setIp(envIp);

        String envLogPath = System.getenv("XXL_JOB_EXECUTOR_LOGPATH");
        if (envLogPath == null) {
            envLogPath = xxlJobProp.getProperty("xxl.job.executor.logpath");
        }
        xxlJobExecutor.setLogPath(envLogPath);

        String envLogRetentionDays = System.getenv("XXL_JOB_EXECUTOR_LOGRETENTIONDAYS");
        if (envLogRetentionDays == null) {
            envLogRetentionDays = xxlJobProp.getProperty("xxl.job.executor.logretentiondays");
        }
        xxlJobExecutor.setLogRetentionDays(Integer.valueOf(envLogRetentionDays));

        // registry job bean
        xxlJobExecutor.setXxlJobBeanList(Arrays.asList(new SampleXxlJob()));

        // start executor
        try {
            xxlJobExecutor.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * destroy
     */
    public void destroyXxlJobExecutor() {
        if (xxlJobExecutor != null) {
            xxlJobExecutor.destroy();
        }
    }

    public static Properties loadProperties(String propertyFileName) {
        InputStreamReader in = null;
        try {
            ClassLoader loder = Thread.currentThread().getContextClassLoader();

            in = new InputStreamReader(loder.getResourceAsStream(propertyFileName), "UTF-8");
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

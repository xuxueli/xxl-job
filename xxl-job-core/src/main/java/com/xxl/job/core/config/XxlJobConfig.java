package com.xxl.job.core.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.thread.JobLogFileCleanThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Configuration
public class XxlJobConfig  implements SmartInitializingSingleton, ApplicationContextAware, DisposableBean, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.address}")
    private String address;

    @Value("${xxl.job.executor.ip}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    @Value("${xxl.job.executor.concurrency}")
    private int concurrency = 1;

    private static ApplicationContext applicationContext;

    private List<XxlJobSpringExecutor> executorList;


    // @Bean
    // public XxlJobSpringExecutor xxlJobExecutor() {
    //     logger.info(">>>>>>>>>>> xxl-job config init.");
    //     XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
    //     xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
    //     xxlJobSpringExecutor.setAppname(appname);
    //     xxlJobSpringExecutor.setAddress(address);
    //     xxlJobSpringExecutor.setIp(ip);
    //     xxlJobSpringExecutor.setPort(port);
    //     xxlJobSpringExecutor.setAccessToken(accessToken);
    //     xxlJobSpringExecutor.setLogPath(logPath);
    //     xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
    //
    //     return xxlJobSpringExecutor;
    // }

    @Bean
    public List<XxlJobSpringExecutor> xxlJobSpringExecutorList() {
        this.logger.info(">>>>>>>>>>> xxl-job config init.");

        XxlJobSpringExecutor.setApplicationContext(applicationContext);

        List<XxlJobSpringExecutor> executorList = new ArrayList<XxlJobSpringExecutor>();

        for (int i = 0; i < concurrency; i++) {
            if(StringUtils.hasText(this.adminAddresses) && StringUtils.hasText(this.appname)) {
                XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
                xxlJobSpringExecutor.setAdminAddresses(this.adminAddresses);
                xxlJobSpringExecutor.setAppname(this.appname);
                xxlJobSpringExecutor.setAddress(this.address);
                xxlJobSpringExecutor.setIp(this.ip);
                xxlJobSpringExecutor.setPort(this.port);
                xxlJobSpringExecutor.setAccessToken(this.accessToken);
                xxlJobSpringExecutor.setLogPath(this.logPath);
                xxlJobSpringExecutor.setLogRetentionDays(this.logRetentionDays);
                executorList.add(xxlJobSpringExecutor);
            } else {
                throw new RuntimeException(">>>>>>>>>>> xxl-job config init Exception. require adminAddresses and appName");
            }
        }
        return executorList;
    }

    @Override
    public void afterSingletonsInstantiated() {
        executorList = (List<XxlJobSpringExecutor>) applicationContext.getBean("xxlJobSpringExecutorList");
        XxlJobSpringExecutor.initJobHandler();
        for (XxlJobSpringExecutor exectuor : executorList) {
            try {
                exectuor.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        // List<XxlJobSpringExecutor> executorList = (List<XxlJobSpringExecutor>) applicationContext.getBean("xxlJobSpringExecutorList");
        for (XxlJobSpringExecutor exectuor : executorList) {
            try {
                exectuor.destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        JobLogFileCleanThread.getInstance().toStop();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        XxlJobConfig.applicationContext = applicationContext;
        // Map<String, Object> scJobs = applicationContext.getBeansWithAnnotation(EnableXxlJob.class);
        // Iterator iter = scJobs.keySet().iterator();
        //
        // while(iter.hasNext()) {
        //     String name = (String)iter.next();
        //     Object o = scJobs.get(name);
        //
        //     try {
        //         String className = ClassUtils.getPackageName(o.getClass()) + "." + ClassUtils.getShortName(o.getClass());
        //         EnableXxlJob enableXxlJob = (EnableXxlJob)o.getClass().getClassLoader().loadClass(className).getAnnotation(EnableXxlJob.class);
        //         if (enableXxlJob != null) {
        //             this.adminAddresses = enableXxlJob.adminAddresses();
        //             this.appname = enableXxlJob.appname();
        //         }
        //     } catch (ClassNotFoundException e) {
        //         e.printStackTrace();
        //     }
        // }
    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     *
     *      1、引入依赖：
     *          <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-commons</artifactId>
     *             <version>${version}</version>
     *         </dependency>
     *
     *      2、配置文件，或者容器启动变量
     *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     *
     *      3、获取IP
     *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    @Override
    public void afterPropertiesSet() throws Exception {}
}
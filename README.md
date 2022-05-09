# xxl-job国产化适配

使用mybatis-plus 改写

## xxl-job 适配数据库

1. mysql（原生）
2. kingbase esv8 (人大金仓)
3. dm(达梦)

## xxl-job 整合nacos 实现xxl-job配置刷新

需要使用本项目的xxl-job-core

监听nacos配置变化类 NacosConfigListening

```java

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.donjin.ccsp.common.util.SpringContextUtil;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author lgh
 * @date 2022-5-6
 * @description nacos配置监听
 */
@Configuration
@Slf4j
public class NacosConfigListening extends AbstractListener {

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private XxlJobConfig xxlJobConfig;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    @Value("${spring.application.name}.${spring.cloud.nacos.config.file-extension}")
    private String dataId;


    @Bean
    public ApplicationRunner runner() {
        return args -> {
            String dataId = this.dataId;
            String group = this.group;
            log.warn("NacosConfigListening   dataId:{}  group:{}", dataId, group);
            String configInfo = nacosConfigManager.getConfigService().getConfigAndSignListener(dataId, group, 1000, this);
            if (!StringUtils.isBlank(configInfo)) {
                receiveConfigInfo(configInfo);
            }
        };
    }


    @Override
    public void receiveConfigInfo(String configInfo) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(configInfo));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("NacosConfigListening receiveConfigInfo format error configInfo:{}", configInfo);
        }
        listeningXxljobConfig(properties);
    }

    /**
     * 监听xxljob 配置变更
     *
     * @param properties
     */
    private void listeningXxljobConfig(Properties properties) {
        XxlJobConfig xxlJobConfig = new XxlJobConfig();
        String adminAddresses = properties.getProperty("xxl.job.admin.addresses");
        xxlJobConfig.setAdminAddresses(adminAddresses);
        xxlJobConfig.setAccessToken(properties.getProperty("xxl.job.accessToken"));
        xxlJobConfig.setAppname(properties.getProperty("xxl.job.executor.appname"));
        xxlJobConfig.setAddress(properties.getProperty("xxl.job.executor.address"));
        xxlJobConfig.setIp(properties.getProperty("xxl.job.executor.ip"));
        xxlJobConfig.setPort(Integer.valueOf(properties.getProperty("xxl.job.executor.port")));
        xxlJobConfig.setLogPath(properties.getProperty("xxl.job.executor.logpath"));
        xxlJobConfig.setLogRetentionDays(Integer.valueOf(properties.getProperty("xxl.job.executor.logretentiondays")));
        if (!this.xxlJobConfig.equals(xxlJobConfig)) {
            this.xxlJobConfig = xxlJobConfig;
            String xxljobName = "xxlJobExecutor";
            XxlJobSpringExecutor springXxljob = (XxlJobSpringExecutor) SpringContextUtil.getBean(xxljobName);
            //TODO 需要手动销毁
            springXxljob.destroy();
            if (StringUtils.isNotBlank(adminAddresses)) {
                XxlJobSpringExecutor singletonObject = xxlJobConfig.xxlJobExecutor();
                SpringContextUtil.replaceSingletonBean(xxljobName, singletonObject);
                singletonObject.afterSingletonsInstantiated();
            }
        }
    }

    @PostConstruct
    public void init() {
        log.warn("NacosConfigListening  init");
    }

    @PreDestroy
    public void destroy() {
        log.warn("NacosConfigListening  destroy");
    }

}
```

手动管理bean工具类

```java
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SpringApplication工具类
 *
 * @author RLP
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    public static ApplicationContext application;


    public static Object getBean(String beanId) {
        return application.getBean(beanId);
    }


    public static <T> T getBean(String beanId, Class<T> requiredType) {
        return application.getBean(beanId, requiredType);
    }


    public static <T> T getBean(Class<T> beanClass) {
        return (T) application.getBean(beanClass);
    }


    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return (Map<String, T>) application.getBeansOfType(type);
    }


    public static String getBeanId(Class<?> cls) {
        return application.getBeanNamesForType(cls)[0];
    }


    public static Class<?> getBeanType(String beanId) {
        return application.getType(beanId);
    }

    /**
     * 返回spring上下文
     */
    public static ApplicationContext getApplication() {
        return application;
    }

    /**
     * 设置spring上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.application = applicationContext;
    }

    /**
     * 动态注入bean
     *
     * @param requiredType 注入类
     * @param beanName     bean名称
     */
    public static Object registerBean(Class<?> requiredType, String beanName) {
        //将applicationContext转换为ConfigurableApplicationContext
        DefaultListableBeanFactory defaultListableBeanFactory = getDefaultListableBeanFactory();
        //创建bean信息.
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(requiredType);
        //动态注册bean.
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        //获取动态注册的bean.
        return application.getBean(requiredType);
    }

    /**
     * 销毁单例bean
     *
     * @param beanName bean名称
     */
    public static void destroySingleton(String beanName) {
        //将applicationContext转换为ConfigurableApplicationContext
        DefaultListableBeanFactory defaultListableBeanFactory = getDefaultListableBeanFactory();
        //动态注册bean.
        defaultListableBeanFactory.destroySingleton(beanName);
    }

    /**
     * 替换单例bean
     *
     * @param beanName  bean名称
     * @param object  T
     * @return
     */
    public static Object replaceSingletonBean(String beanName, Object object) {
        //将applicationContext转换为ConfigurableApplicationContext
        DefaultListableBeanFactory defaultListableBeanFactory = getDefaultListableBeanFactory();
        //动态注册bean.
        defaultListableBeanFactory.destroySingleton(beanName);
        //动态注册bean.
        defaultListableBeanFactory.registerSingleton(beanName, object);
        return application.getBean(beanName);
    }

    /**
     * 动态注入单例bean实例
     *
     * @param beanName        bean名称
     * @param singletonObject 单例bean实例
     * @return 注入实例
     */
    public static Object registerSingletonBean(String beanName, Object singletonObject) {
        DefaultListableBeanFactory defaultListableBeanFactory = getDefaultListableBeanFactory();
        //动态注册bean.
        defaultListableBeanFactory.registerSingleton(beanName, singletonObject);
        //获取动态注册的bean.
        return application.getBean(beanName);
    }

    private static DefaultListableBeanFactory getDefaultListableBeanFactory() {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) application;
        //获取BeanFactory
        return (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();
    }
}
```
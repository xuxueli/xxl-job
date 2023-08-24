package com.xxl.job.core.config;

import com.xxl.job.core.enums.ThreadStatus;
import com.xxl.job.core.thread.ThreadEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job管理自动配置
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
@Slf4j
@Configuration
public class XxlJobThreadEventConfig implements ApplicationContextAware, InitializingBean, DisposableBean {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.publishEvent(new ThreadEvent(this, ThreadStatus.START));
        log.info(">>>>>>>>> init job thread success.");
    }

    @Override
    public void destroy() throws Exception {
        applicationContext.publishEvent(new ThreadEvent(this, ThreadStatus.STOP));
        log.info(">>>>>>>>> destroy job thread success.");
    }



}

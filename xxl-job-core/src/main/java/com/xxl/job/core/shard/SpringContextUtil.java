package com.xxl.job.core.shard;


import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;


public class SpringContextUtil {

    private static Logger logger = LoggerFactory.getLogger(SpringContextUtil.class);

    /**
     * 获取对象
     * @param beanId bean名称
     * @return
     * @throws BeansException
     */
    public static Object getBean(String beanId) throws BeansException {
        ApplicationContext applicationContext = XxlJobSpringExecutor.getApplicationContext();
        try {
            return applicationContext.getBean(beanId);
        } catch (BeansException e) {
            logger.error(">>>>>>>>>>> xxl-job shard trigger info {}",e.getMessage());
            return null;
        }
    }

    /**
     * 获取对象
     * @param clazz 类名
     * @return
     * @throws BeansException
     */
    public static Object getBeanByType(Class clazz) throws BeansException {
        ApplicationContext applicationContext = XxlJobSpringExecutor.getApplicationContext();
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            logger.error(">>>>>>>>>>> xxl-job shard trigger error {} >>>>>>>>>>",e.getMessage());
            return null;
        }
    }

}

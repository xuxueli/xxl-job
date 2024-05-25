package com.xxl.job.admin.platform;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author Ice2Faith
 * @date 2024/5/22 9:39
 * @desc
 */
@Component
public class DatabasePlatformUtil implements ApplicationContextAware {
    private static DatabasePlatformConfig platformConfig;
    public static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DatabasePlatformUtil.applicationContext=applicationContext;
    }
    public static DatabasePlatformConfig getPlatformConfig(){
        if(platformConfig!=null){
            return platformConfig;
        }
        while(applicationContext==null){
            try{
                Thread.sleep(5);
            }catch(Exception e){
            }
        }
        platformConfig=applicationContext.getBean(DatabasePlatformConfig.class);
        return platformConfig;
    }
}

package com.xxl.job.admin.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:39
 */
@Component
public class JsonUtil implements ApplicationContextAware {
    private static volatile ApplicationContext applicationContext;
    private static final CountDownLatch latch=new CountDownLatch(1);
    private static volatile ObjectMapper objectMapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JsonUtil.applicationContext=applicationContext;
        latch.countDown();
    }

    public static ObjectMapper getObjectMapper(){
        if(objectMapper==null){
            synchronized (JsonUtil.class){
                if(objectMapper==null) {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {

                    }
                    objectMapper = applicationContext.getBean(ObjectMapper.class);
                }
            }
        }
        return objectMapper;
    }

    public static String toJson(Object obj){
        try{
            return getObjectMapper().writeValueAsString(obj);
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }

    public static<T> T fromJson(String json,Class<T> type){
        try{
            return getObjectMapper().readValue(json,type);
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }

    public static<T> T fromJson(String json, TypeReference<T> type){
        try{
            return getObjectMapper().readValue(json,type);
        }catch(Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }
}

package com.xxl.job.executor.factory.glue;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.executor.factory.handler.JobHandler;
import groovy.lang.GroovyClassLoader;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 胶水处理器
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
public abstract class BaseGlueProcessor implements GlueProcessor {

    /**
     * groovy class loader
     */
    private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    private final ConcurrentMap<String, Class<?>> classCache = new ConcurrentHashMap<>();

    @Override
    public JobHandler loadNewInstance(String codeSource) throws Exception{
        if (codeSource!=null && codeSource.trim().length()>0) {
            Class<?> clazz = getCodeSourceClass(codeSource);
            if (clazz != null) {
                Object instance = clazz.newInstance();
                if (ObjectUtil.isNotNull(instance)) {
                    if (instance instanceof JobHandler) {
                        this.injectService(instance);
                        return (JobHandler) instance;
                    } else {
                        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
                                + "cannot convert from instance["+ instance.getClass() +"] to IJobHandler");
                    }
                }
            }
        }
        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
    }

    private Class<?> getCodeSourceClass(String codeSource){
        try {
            byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
            String md5Str = new BigInteger(1, md5).toString(16);

            Class<?> clazz = classCache.get(md5Str);
            if(ObjectUtil.isEmpty(clazz)){
                clazz = groovyClassLoader.parseClass(codeSource);
                classCache.putIfAbsent(md5Str, clazz);
            }
            return clazz;
        } catch (Exception e) {
            return groovyClassLoader.parseClass(codeSource);
        }
    }

    /**
     * inject service of bean field
     *
     * @param instance 实例
     */
    public void injectService(Object instance) {
    }












}

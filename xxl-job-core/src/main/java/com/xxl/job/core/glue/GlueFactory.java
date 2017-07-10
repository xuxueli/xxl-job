package com.xxl.job.core.glue;

import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * glue factory, product class/object by name
 *
 * @author xuxueli 2016-1-2 20:02:27
 */
public class GlueFactory {
    private static Logger logger = LoggerFactory.getLogger(GlueFactory.class);
    private static AutowireCapableBeanFactory factory;
    private static GlueFactory glueFactory;
    private static volatile boolean isSpringProject;
    private static volatile boolean isInit;

    public static void init(AutowireCapableBeanFactory beanFactory) {
        if (!isInit) {
            factory = beanFactory;
            isSpringProject = true;
            glueFactory = new GlueFactory();
            isInit = true;
        }
    }

    public static void init() {
        if (!isInit) {
            factory = null;
            isSpringProject = false;
            glueFactory = new GlueFactory();
            isInit = true;
        }
    }

    public static GlueFactory getInstance() {
        if (isInit)
            return glueFactory;
        throw new NullPointerException();
    }

    /**
     * groovy class loader
     */
    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    // ----------------------------- load instance -----------------------------
    // load new instance, prototype
    public IJobHandler loadNewInstance(String codeSource) throws Exception {
        if (codeSource != null && codeSource.trim().length() > 0) {
            Class<?> clazz = groovyClassLoader.parseClass(codeSource);
            if (clazz != null && clazz.isAnnotationPresent(JobHander.class)) {
                Object instance = clazz.newInstance();
                if (instance != null) {
                    if (instance instanceof IJobHandler) {
                        IJobHandler handler = (IJobHandler) instance;
                        if (isSpringProject) {
                            factory.autowireBean(handler);
                        }
                        return handler;
                    } else {
                        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
                                + "cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
                    }
                }
            }
        }
        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
    }
}

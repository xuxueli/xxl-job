package com.xxl.job.executor.factory.glue;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Spring Glue处理器
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Slf4j
public class SpringGlueProcessor extends BaseGlueProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void injectService(Object instance) {
        if (ObjectUtil.isEmpty(instance)) return;

        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            Object fieldBean = null;
            // with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired

            Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
            Autowired autowired = AnnotationUtils.getAnnotation(field, Autowired.class);
            if (ObjectUtil.isNotNull(resource)) {
                try {
                    fieldBean = StrUtil.isNotBlank(resource.name())
                            ? applicationContext.getBean(resource.name())
                            : applicationContext.getBean(field.getName());
                } catch (Exception ignored) {}

                if (ObjectUtil.isEmpty(fieldBean)) {
                    fieldBean = applicationContext.getBean(field.getType());
                }
            } else if (ObjectUtil.isNotNull(autowired)) {
                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
                fieldBean = ObjectUtil.isNotNull(qualifier) && StrUtil.isNotBlank(qualifier.value())
                        ? applicationContext.getBean(qualifier.value())
                        : applicationContext.getBean(field.getType());
            }

            if (ObjectUtil.isNotNull(fieldBean)) {
                field.setAccessible(true);
                try {
                    field.set(instance, fieldBean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }



}

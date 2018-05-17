package com.xxl.job.core.annotationtask.spring;

import com.xxl.job.core.annotationtask.annotations.XxlScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class XxlScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(XxlScan.class.getName()));
        XxlScanner scanner = new XxlScanner(beanDefinitionRegistry);
        scanner.registerDefaultFilters();
        scanner.doScan(annoAttrs.getStringArray("value"));
    }
}

package com.xxl.job.core.annotationtask.annotations;

import com.xxl.job.core.annotationtask.spring.XxlScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(XxlScannerRegistrar.class)
public @interface XxlScan {

    String[] value() default {};
}

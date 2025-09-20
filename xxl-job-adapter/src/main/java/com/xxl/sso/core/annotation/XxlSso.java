package com.xxl.sso.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:52
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlSso {
    boolean login() default true;

    String permission() default "";

    String role() default "";
}

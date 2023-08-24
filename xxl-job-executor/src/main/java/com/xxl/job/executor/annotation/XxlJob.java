package com.xxl.job.executor.annotation;

import java.lang.annotation.*;

/**
 * 任务注解
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    /**
     * 处理器名
     *
     * @return {@link String}
     */
    String value();

    /**
     * 初始化处理器
     *
     * @return {@link String}
     */
    String init() default "";

    /**
     * 销毁处理器
     *
     * @return {@link String}
     */
    String destroy() default "";

}

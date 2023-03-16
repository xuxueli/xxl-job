package com.xxl.job.core.handler.annotation;

import com.xxl.job.core.converter.Converter;
import com.xxl.job.core.converter.JsonConverter;

import java.lang.annotation.*;

/**
 * annotation for method jobhandler
 *
 * @author xuxueli 2019-12-11 20:50:13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    /**
     * jobhandler name
     */
    String value();

    /**
     * init handler, invoked when JobThread init
     */
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     */
    String destroy() default "";

    /**
     * Converter to param
     */
    Class<? extends Converter> converter() default JsonConverter.class;

}

package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 * annotation for method jobhandler
 *
 * will be replaced bu {@link com.xxl.job.core.handler.annotation.XxlJob}
 *
 * @author jhb 2021-12-21 15:50:13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JobHandler {

    /**
     * jobhandler name
     */
    String value();

}

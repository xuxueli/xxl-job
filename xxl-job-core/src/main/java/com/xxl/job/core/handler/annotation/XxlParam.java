package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 * annotation for method jobhandler param name
 *
 * @author xuxueli 2019-12-11 20:50:13
 */
@Target({
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlParam {
    /**
     * param name
     */
    String value();

}

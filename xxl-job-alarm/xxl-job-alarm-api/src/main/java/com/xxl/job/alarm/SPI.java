package com.xxl.job.alarm;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */

@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface SPI {

    String value();

}

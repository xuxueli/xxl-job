package com.xxl.job.executor.register;

import java.lang.annotation.*;

/**
 * Created by Adam on 2018/1/9.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JobAutoRegister {

    /**
     * 路由策略
     * @return
     */
    RouterPolicy routerPolicy() default RouterPolicy.ROUND;

    /**
     * 执行策略
     * @return
     */
    ExecutePolicy executePolicy() default ExecutePolicy.SERIAL_EXECUTION;

    /**
     * 模式
     * @return
     */
    String mode() default "BEAN";

    /**
     * cron
     * @return
     */
    String cron();

    /**
     * job name
     * @return
     */
    String name();

    /**
     * 执行参数
     * @return
     */
    String param() default "";

    /**
     * 任务描述
     * @return
     */
    String desc() default "";

    FailPolicy failPolicy() default FailPolicy.FAIL_ALARM;

}

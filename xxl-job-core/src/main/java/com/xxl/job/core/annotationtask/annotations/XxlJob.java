package com.xxl.job.core.annotationtask.annotations;

import com.xxl.job.core.annotationtask.enums.ExecutorBlockStrategy;
import com.xxl.job.core.annotationtask.enums.ExecutorFailStrategy;
import com.xxl.job.core.annotationtask.enums.ExecutorRouteStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlJob {

    String jobCron();// 任务执行CRON表达式 【base on quartz】必传
    String jobDesc() default "";//任务描述
    Class<?> executorHandler() ;// 执行handler，任务Handler名称
    String author() default "";// 负责人
    String alarmEmail() default "";// 报警邮件
    String executorParam() default "";// 执行器，任务参数
    boolean onStart() default false;


    ExecutorRouteStrategy executorRouteStrategy() default ExecutorRouteStrategy.FIRST;// 执行器路由策略 选择哪个执行器机器
    /**
     * 阻塞处理策略 对于一个jobDetail会有个jobThread 处理 一个jobDetail有2次触发
     * 这时候就需要指定阻塞策略了
     * @return
     */
    ExecutorBlockStrategy executorBlockStrategy() default ExecutorBlockStrategy.SERIAL_EXECUTION;
    /**
     * 失败处理策略 如果执行失败了怎么办
     * 有失败重试失败告警
     * @return
     */
    ExecutorFailStrategy executorFailStrategy() default ExecutorFailStrategy.FAIL_RETRY;


}

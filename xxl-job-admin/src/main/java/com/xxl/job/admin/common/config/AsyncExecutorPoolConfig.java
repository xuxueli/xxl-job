package com.xxl.job.admin.common.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Rong.Jia
 * @date 2021/04/29 08:58
 */
@Slf4j
@Data
@Configuration
@ConditionalOnProperty(prefix = "xdc.task.pool", name = "enabled", havingValue = "true")
@ConfigurationProperties(prefix = "xdc.task.pool")
public class AsyncExecutorPoolConfig implements AsyncConfigurer {

    /**
     *  是否开启，默认：false
     */
    private boolean enabled = false;

    /**
     * 核心数
     */
    private int corePoolSize;

    /**
     * 最大数
     */
    private int maxPoolSize;

    /**
     * 活跃时间
     */
    private int keepAliveSeconds;

    /**
     * 队列大小
     */
    private int queueCapacity;

    /**
     * 线程名前缀
     */
    private String threadName = "asyncExecutor-";

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //核心线程池大小
        executor.setCorePoolSize(corePoolSize);
        //最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //队列容量
        executor.setQueueCapacity(queueCapacity);
        //活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);

        //线程名字前缀
        executor.setThreadNamePrefix(threadName);

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("线程池执行任务发生未知异常", ex);
    }
}

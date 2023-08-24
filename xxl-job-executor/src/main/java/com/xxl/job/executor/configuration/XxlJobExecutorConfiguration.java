package com.xxl.job.executor.configuration;

import com.xxl.job.executor.factory.glue.GlueProcessor;
import com.xxl.job.executor.factory.glue.SpringGlueProcessor;
import com.xxl.job.executor.factory.thread.ExecutorRegistryThread;
import com.xxl.job.executor.factory.thread.JobLogFileCleanThread;
import com.xxl.job.executor.factory.thread.TriggerCallbackThread;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job执行器配置
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Configuration
public class XxlJobExecutorConfiguration {

    @Bean
    @ConditionalOnMissingBean(GlueProcessor.class)
    public GlueProcessor glueProcessor() {
        return new SpringGlueProcessor();
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorRegistryThread.class)
    public ExecutorRegistryThread executorRegistryThread() {
        return new ExecutorRegistryThread();
    }

    @Bean
    @ConditionalOnMissingBean(JobLogFileCleanThread.class)
    public JobLogFileCleanThread jobLogFileCleanThread() {
        return new JobLogFileCleanThread();
    }

    @Bean
    @ConditionalOnMissingBean(TriggerCallbackThread.class)
    public TriggerCallbackThread triggerCallbackThread() {
        return new TriggerCallbackThread();
    }








}

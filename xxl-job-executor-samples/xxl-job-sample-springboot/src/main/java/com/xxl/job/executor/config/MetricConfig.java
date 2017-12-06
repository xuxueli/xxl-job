package com.xxl.job.executor.config;
import com.codahale.metrics.*;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MetricConfig {

    @Bean
    public MetricRegistry metrics() {
        return new MetricRegistry();
    }

    /**
     * Reporter 数据的展现位置
     *
     * @param metrics
     * @return
     */
    @Bean
    public ConsoleReporter consoleReporter(MetricRegistry metrics) {
        return ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
    }

    @Bean
    public Slf4jReporter slf4jReporter(MetricRegistry metrics) {
        return Slf4jReporter.forRegistry(metrics).outputTo(LoggerFactory.getLogger(MetricConfig.class)).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
    }

    @Bean
    public JmxReporter jmxReporter(MetricRegistry metrics) {
        return JmxReporter.forRegistry(metrics).build();
    }

//    /**
//     * TPS 计算器
//     *
//     * @param metrics
//     * @return
//     */
//    @Bean("tps")
//    public Meter meter(MetricRegistry metrics) {
//        return metrics.meter("Metric-TPS");
//    }
//    /**
//     * 直方图
//     *
//     * @param metrics
//     * @return
//     */
//    @Bean
//    public Histogram histogram(MetricRegistry metrics) {
//        return metrics.histogram("Metric-Histogram");
//    }
//
//    /**
//     * 计数器
//     *
//     * @param metrics
//     * @return
//     */
//    @Bean
//    public Counter jobcounter(MetricRegistry metrics) {
//        return metrics.counter("Metric-Counter");
//    }
//
//    /**
//     * 计时器
//     *
//     * @param metrics
//     * @return
//     */
//    @Bean
//    public Timer jobtimer(MetricRegistry metrics) {
//        return metrics.timer("Metric-Execute-Time");
//    }
}
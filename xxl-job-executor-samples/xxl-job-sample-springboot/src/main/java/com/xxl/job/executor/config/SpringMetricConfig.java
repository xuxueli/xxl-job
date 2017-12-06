package com.xxl.job.executor.config;
//
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.context.annotation.Configuration;
//import com.codahale.metrics.ConsoleReporter;
//import com.codahale.metrics.MetricRegistry;
//import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
//import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
//
//@Configuration
//@EnableMetrics
//public class SpringMetricConfig  extends MetricsConfigurerAdapter {
//	/**
//	 * registerReporter allows the MetricsConfigurerAdapter to shut down the reporter when the Spring context is closed
//	 */
//    @Override
//    public void configureReporters(MetricRegistry metricRegistry) {
//        registerReporter(ConsoleReporter.forRegistry(metricRegistry).build()).start(1, TimeUnit.MINUTES);
//    }
//}

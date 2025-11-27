package com.xxl.job.admin.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metrics Configuration for Prometheus Integration
 * 
 * Configures common tags for all metrics exported to Prometheus
 * 
 * @author xxl-job-community
 * @since 3.3.0
 */
@Configuration
public class MetricsConfig {

    /**
     * Add common tags to all metrics
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
            "application", "xxl-job-admin",
            "instance", getInstanceId()
        );
    }

    /**
     * Get instance identifier from environment or hostname
     */
    private String getInstanceId() {
        // Try to get from Kubernetes pod name
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        
        // Try to get from system property
        hostname = System.getProperty("instance.id");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        
        // Fallback to unknown
        return "unknown";
    }
}
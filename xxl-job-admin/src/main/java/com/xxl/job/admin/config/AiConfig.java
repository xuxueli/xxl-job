package com.xxl.job.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AI Code Generation Configuration
 *
 * @author xxl-job
 */
@Component
public class AiConfig {

    @Value("${xxl.job.ai.enabled:false}")
    private boolean enabled;

    @Value("${xxl.job.ai.service-url:}")
    private String serviceUrl;

    @Value("${xxl.job.ai.api-key:}")
    private String apiKey;

    @Value("${xxl.job.ai.timeout:30000}")
    private int timeout;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Check if AI configuration is valid
     */
    public boolean isConfigValid() {
        return enabled && serviceUrl != null && !serviceUrl.trim().isEmpty()
                && apiKey != null && !apiKey.trim().isEmpty();
    }
}

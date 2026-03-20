package com.xxl.job.writing.config;

import com.xxl.job.writing.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers MVC interceptors for the writing platform module.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/v3/api-docs/**",
                        "/actuator/**",
                        "/error"
                );
    }
}

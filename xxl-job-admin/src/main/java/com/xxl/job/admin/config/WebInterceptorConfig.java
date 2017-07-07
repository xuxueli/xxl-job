package com.xxl.job.admin.config;

import com.xxl.job.admin.controller.interceptor.CookieInterceptor;
import com.xxl.job.admin.controller.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Author: Antergone
 * Date: 2017/6/29
 */
@Configuration
public class WebInterceptorConfig extends WebMvcConfigurerAdapter {

    private final PermissionInterceptor permissionInterceptor;

    @Autowired
    public WebInterceptorConfig(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(new CookieInterceptor()).addPathPatterns("/**");
    }
}

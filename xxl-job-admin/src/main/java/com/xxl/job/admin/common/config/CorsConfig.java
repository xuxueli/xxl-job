package com.xxl.job.admin.common.config;

import cn.hutool.core.comparator.VersionComparator;
import lombok.Data;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 *
 * @author Rong.Jia
 * @date 2021/12/20
 */
@Data
@Configuration
public class CorsConfig {

    private static final String SPRING_VERSION = "2.4.0";

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", createCorsConfiguration());
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    /**
     * 比较版本
     *
     * @return boolean
     */
    private boolean compareVersion() {
        String version = SpringBootVersion.getVersion();
        return VersionComparator.INSTANCE.compare(version, SPRING_VERSION) >= 0;
    }

    /**
     * 创建跨域配置
     *
     * @return {@link CorsConfiguration}
     */
    private CorsConfiguration createCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedHeaders(Collections.singletonList("Authorization,Origin, X-Requested-With, Content-Type, Accept,WWW-Authenticate"));
        config.setAllowCredentials(true);

        if (compareVersion()) {
            config.addAllowedOriginPattern("*");
        }else {
            config.addAllowedOrigin("*");
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.addExposedHeader("Location");

        return config;
    }















}

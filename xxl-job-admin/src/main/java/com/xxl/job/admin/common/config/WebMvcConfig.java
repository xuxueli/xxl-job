package com.xxl.job.admin.common.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.xxl.job.admin.common.config.escape.StringEscapeEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 *  web配置类
 * @author Rong.Jia
 * @date 2019/08/14 11:45
 */
@Configuration
@ConditionalOnClass({DispatcherServlet.class, WebMvcConfigurer.class})
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patterns = new ArrayList<>();
        patterns.add("/**/*.js");
        patterns.add("/**/*.css");
        patterns.add("/fonts/**");
        patterns.add("/css/**");
        patterns.add("/js/**");
        patterns.add("/favicon.ico");
        patterns.add("/plugins/**");
        patterns.add("/images/**");
        patterns.add("/login");
        patterns.add("/register");
        patterns.add("/auth/**");
        patterns.add("/error");
        patterns.add("/swagger/**");
        patterns.add("/swagger-ui.html");
        patterns.add("/doc.html");
        patterns.add("/webjars/**");
        patterns.add("/v2/**");
        patterns.add("/**/v2/api-docs");
        patterns.add("/swagger-resources/**");
        patterns.add("/admin/**");
        patterns.add("/job/cron");
        patterns.add("/static/**");
        patterns.add("/templates/**");
        patterns.add("/**/*.html");
        patterns.add("/**/*.icon");


        registry.addInterceptor(loginInterceptor()).excludePathPatterns(patterns).addPathPatterns("/**");
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    /**
     * 对get,form 去除字符串前后空格
     */
    @Bean
    public ConfigurableWebBindingInitializer configurableWebBindingInitializer() {
        ConfigurableWebBindingInitializer initializer = new ConfigurableWebBindingInitializer();
        FormattingConversionService conversionService = new DefaultFormattingConversionService();
        //we can add our custom converters and formatters
        //conversionService.addConverter(...);
        //conversionService.addFormatter(...);
        initializer.setConversionService(conversionService);
        //we can set our custom validator
        //initializer.setValidator(....);

        //here we are setting a custom PropertyEditor
        initializer.setPropertyEditorRegistrar(propertyEditorRegistry -> propertyEditorRegistry.registerCustomEditor(String.class,
                new StringEscapeEditor()));
        return initializer;
    }

}

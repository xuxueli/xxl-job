package com.xxl.job.admin.config;


import com.xxl.job.admin.controller.resolver.WebExceptionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@ComponentScan("com.xxl.job.admin")
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/webapp/favicon.ico");
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("/static/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/webapp/static/");
        registry.addResourceHandler("/**/*.html")
                .addResourceLocations("classpath:/webapp/WIN-INF");
    }

    @Bean("viewResolver")
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setViewClass(FreeMarkerView.class);
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".ftl");
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setExposeSpringMacroHelpers(true);
        viewResolver.setExposeRequestAttributes(true);
        viewResolver.setExposeSessionAttributes(true);
        viewResolver.setRequestContextAttribute("request");
        viewResolver.setCache(true);
        viewResolver.setOrder(0);

        return viewResolver;
    }

    @Bean("exceptionResolver")
    public WebExceptionResolver exceptionResolver() {
        return new WebExceptionResolver();
    }
}

package com.xxl.job.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.xxl.job.admin.dao")
public class MybatisPlusConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 自定义拦截器，先添加先执行。
        if (url.contains("jdbc:dm")) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.DM));
        } else if (url.contains("jdbc:kingbase")) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor((DbType.KINGBASE_ES)));
        } else if (url.contains("jdbc:mysql")) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        }
        return interceptor;
    }


}
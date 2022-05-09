package com.xxl.job.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xxl.job.admin.constant.DbTypeConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.xxl.job.admin.dao")
public class MybatisPlusConfig {

    /**
     * 数据库类型
     */
    @Value("${db.type}")
    private Integer dbType;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 自定义拦截器，先添加先执行。
        if (dbType.equals(DbTypeConstant.KINGBASE)) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor((DbType.KINGBASE_ES)));
        } else if (dbType.equals(DbTypeConstant.MYSQL)) {
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        }
        return interceptor;
    }


}
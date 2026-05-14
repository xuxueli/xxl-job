package com.xxl.job.admin;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication(scanBasePackages = "com.xxl.job")
@MapperScan(basePackages = {"com.xxl.job.admin.mapper"})
public class XxlJobAdminApplication {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Autowired DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // Configure with MyBatis Plus configuration
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        factoryBean.setConfiguration(configuration);

        // Set type aliases package
        factoryBean.setTypeAliasesPackage("com.xxl.job.admin.model");

        return factoryBean.getObject();
    }

    public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
    }

}
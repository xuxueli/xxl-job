package com.xxl.job.admin.core.conf;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class DatabaseIdProviderConfig {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties props = new Properties();
        props.put("MySQL", "mysql");
        props.put("Oracle", "oracle");
        props.put("Microsoft SQL Server", "sqlserver");
        props.put("PostgreSQL", "pg");
        props.put("KingbaseES", "kingbase");
        databaseIdProvider.setProperties(props);
        return databaseIdProvider;
    }

}
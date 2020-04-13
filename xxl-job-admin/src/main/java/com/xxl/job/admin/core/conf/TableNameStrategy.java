package com.xxl.job.admin.core.conf;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.stereotype.Component;

/**
 * @author YunSongLiu
 */
@Component
public class TableNameStrategy extends SpringPhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        String tableName = tableNamePrefix + "_" + name.getText();
        return Identifier.toIdentifier(tableName);
    }

    @Value("${xxl.tablename.prefix:xxl}")
    private String tableNamePrefix;

}

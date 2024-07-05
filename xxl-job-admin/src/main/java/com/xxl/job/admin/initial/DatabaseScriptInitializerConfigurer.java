package com.xxl.job.admin.initial;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ice2Faith
 * @date 2024/5/23 22:07
 * @desc
 */
@ConditionalOnExpression("${xxl.job.database.init.enable:false}")
@ConfigurationProperties(prefix = "xxl.job.database.init")
@Configuration
public class DatabaseScriptInitializerConfigurer implements InstantiationAwareBeanPostProcessor {
    private Logger log = LoggerFactory.getLogger(DatabaseScriptInitializerConfigurer.class);

    private String testSql = "select 1 from xxl_job_info where 1!=1";
    private String encoding = "UTF-8";
    private String separator = ";";
    private String scriptLocations ;

    @Autowired
    private DataSource dataSource;

    public String getTestSql() {
        return testSql;
    }

    public void setTestSql(String testSql) {
        this.testSql = testSql;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getScriptLocations() {
        return scriptLocations;
    }

    public void setScriptLocations(String scriptLocations) {
        this.scriptLocations = scriptLocations;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass.equals(XxlJobAdminConfig.class)) {
            if (scriptLocations == null) {
                log.warn("jump init database, none init script location config.");
                return null;
            }
            if (testSql == null) {
                log.warn("jump init database, none init test sql config.");
                return null;
            }
            String tql = testSql.trim();
            if ("".equals(tql)) {
                log.warn("jump init database, empty init test sql config.");
                return null;
            }
            String[] locations = scriptLocations.split("\\s*[,|;]\\s*");
            List<String> paths = new ArrayList<>();
            for (String item : locations) {
                String str = item.trim();
                if (!"".equals(str)) {
                    paths.add(str);
                }
            }

            if(paths.isEmpty()){
                log.warn("jump init database, empty init script location config.");
                return null;
            }

            List<String> sqls = new ArrayList<>();

            for (String path : paths) {
                if (path != null && !"".equals(path)) {
                    log.info("read script : "+path);
                    try {
                        URL url = ResourceUtils.getURL(path);
                        if (url != null) {
                            InputStream is = url.openStream();
                            String text = StreamUtils.copyToString(is, Charset.forName(encoding));
                            is.close();

                            String[] arr = text.split(separator);
                            for (String item : arr) {
                                String sql = item.trim();
                                if (!"".equals(sql)) {
                                    sqls.add(sql);
                                }
                            }
                            log.info("loaded script : "+path);
                        }
                    } catch (Exception e) {
                        log.info("read script content error,continue it : "+e.getMessage(),e);
                    }
                }
            }

            if (sqls.isEmpty()) {
                log.warn("jump init database, empty init script found.");
                return null;
            }

            Connection conn = DataSourceUtils.getConnection(dataSource);
            try {

                if (testSql != null) {
                    boolean hasTable = false;
                    try {
                        Statement stat = conn.createStatement();
                        ResultSet rs = stat.executeQuery(testSql);
                        hasTable = true;
                        rs.close();
                        stat.close();
                    } catch (Exception e) {
                        hasTable = false;
                        log.debug("test table exists exception,ignore it : "+e.getMessage(),e);
                    }

                    if (hasTable) {
                        log.warn("jump init database, test success, database has initialed.");
                        return null;
                    }
                }

                try {
                    log.info("database init script running...");
                    for (String sql : sqls) {
                        Statement stat = conn.createStatement();
                        stat.executeUpdate(sql);
                        stat.close();
                    }
                    log.info("database init script has run.");
                } catch (Exception e) {
                    log.error("database init script run error : "+e.getMessage(),e);
                }

            } finally {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        }


        return null;
    }
}

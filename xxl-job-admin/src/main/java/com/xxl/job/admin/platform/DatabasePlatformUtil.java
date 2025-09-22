package com.xxl.job.admin.platform;

import com.xxl.job.admin.platform.pageable.IDatabaseLockStatementSupplier;
import com.xxl.job.admin.platform.pageable.impl.common.CommonLockStatementSupplier;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Ice2Faith
 * @date 2024/5/22 9:39
 * @desc
 */
@Component
public class DatabasePlatformUtil implements ApplicationContextAware {
    private static volatile DatabasePlatformConfig platformConfig;
    private static volatile IDatabaseLockStatementSupplier lockStatementSupplier;
    private static volatile ApplicationContext applicationContext;
    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DatabasePlatformUtil.applicationContext = applicationContext;
        latch.countDown();
    }

    public static ApplicationContext getApplicationContext() {
        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applicationContext;
    }

    public static DatabasePlatformConfig getPlatformConfig() {
        if (platformConfig != null) {
            return platformConfig;
        }
        platformConfig = getApplicationContext().getBean(DatabasePlatformConfig.class);
        return platformConfig;
    }

    public static boolean isStandalone() {
        return getPlatformConfig().isStandalone();
    }

    public static String getDatabaseType() {
        return getPlatformConfig().getType();
    }

    public static IDatabaseLockStatementSupplier getLockStatementSupplier() {
        if (lockStatementSupplier != null) {
            return lockStatementSupplier;
        }
        Map<String, IDatabaseLockStatementSupplier> beanMap = getApplicationContext().getBeansOfType(IDatabaseLockStatementSupplier.class);
        for (Map.Entry<String, IDatabaseLockStatementSupplier> entry : beanMap.entrySet()) {
            IDatabaseLockStatementSupplier value = entry.getValue();
            if (value.supportDatabase(getDatabaseType())) {
                lockStatementSupplier = value;
                break;
            }
        }
        if (lockStatementSupplier == null) {
            lockStatementSupplier = getApplicationContext().getBean(CommonLockStatementSupplier.class);
        }
        return lockStatementSupplier;
    }

    public static PreparedStatement getLockStatement(Connection conn) throws SQLException {
        IDatabaseLockStatementSupplier supplier = getLockStatementSupplier();
        return supplier.getStatement(conn);
    }
}

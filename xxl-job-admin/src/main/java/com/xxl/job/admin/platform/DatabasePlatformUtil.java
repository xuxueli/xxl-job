package com.xxl.job.admin.platform;

import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@Component
public class DatabasePlatformUtil implements ApplicationContextAware {
    private static volatile DatabasePlatformConfig platformConfig;
    private static volatile ApplicationContext applicationContext;
    private static final CountDownLatch latch = new CountDownLatch(1);

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

    public static DatabasePlatformType getDatabaseType() {
        return DatabasePlatformType.of(getPlatformConfig().getType());
    }

}

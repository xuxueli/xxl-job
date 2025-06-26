package com.xxl.job.admin.core.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.function.Consumer;

/**
 * @author: donglingmo
 * Date: 2025/4/14
 * Description:
 */
public class DBHelper {
    @FunctionalInterface
    public interface SqlConsumer {
        void accept(Connection conn) throws Exception;
    }

    public static void withTransaction(DataSource ds, SqlConsumer consumer, boolean suppressLog, Consumer<Throwable> onError) {
        try (Connection conn = ds.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                consumer.accept(conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (!suppressLog) {
                    onError.accept(e);
                }
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (Exception e) {
            if (!suppressLog) {
                onError.accept(e);
            }
            throw new RuntimeException("DB transaction failed", e);
        }
    }
}

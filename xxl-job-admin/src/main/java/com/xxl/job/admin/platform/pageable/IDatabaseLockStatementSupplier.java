package com.xxl.job.admin.platform.pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Ice2Faith
 * @date 2024/7/5 11:43
 * @desc
 */
public interface IDatabaseLockStatementSupplier {
    boolean supportDatabase(String type);
    PreparedStatement getStatement(Connection conn) throws SQLException;
}

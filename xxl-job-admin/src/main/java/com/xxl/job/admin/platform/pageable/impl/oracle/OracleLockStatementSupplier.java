package com.xxl.job.admin.platform.pageable.impl.oracle;

import com.xxl.job.admin.platform.pageable.IDatabaseLockStatementSupplier;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ice2Faith
 * @date 2024/7/5 11:44
 * @desc
 */
@Component
public class OracleLockStatementSupplier implements IDatabaseLockStatementSupplier {
    public static final Set<String> SUPPORT_TYPES= Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "oracle"
            ))
    );
    @Override
    public boolean supportDatabase(String type) {
        return SUPPORT_TYPES.contains(type);
    }

    @Override
    public PreparedStatement getStatement(Connection conn) throws SQLException {
        return conn.prepareStatement(  "select * from XXL_JOB_LOCK where \"LOCK_NAME\" = 'schedule_lock' for update" );
    }
}

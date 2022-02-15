package com.xxl.job.admin.core.lock;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;

import java.sql.*;

/**
 * Created on 2022/2/15.
 *
 * @author lan
 */
public class DbBasedLock {

    private String name;

    Connection conn = null;
    boolean connAutoCommit = false;
    PreparedStatement preparedStatement = null;

    DbBasedLock(String name) {
        this.name = name;
    }

    /**
     * 如果lockName不存在会插入到xxl_job_lock，竞争不到锁，会阻塞
     */
    public void lock() throws SQLException {
        if (!tryLock()) {
            unlock();
            ensureLockNameExist();
            if (!tryLock()) {
                throw new RuntimeException("lock " + name + " failed, unknown error");
            }
        }
    }

    private boolean tryLock() throws SQLException {
        conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
        connAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        preparedStatement = conn.prepareStatement(String.format("select * from xxl_job_lock where lock_name = '%s' for update", name));
        preparedStatement.execute();

        ResultSet resultSet = preparedStatement.getResultSet();

        return resultSet.next();
    }

    private void ensureLockNameExist() throws SQLException {
        try (Connection conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection()) {
            try (PreparedStatement prepareStatement = conn.prepareStatement(String.format("insert into xxl_job_lock (lock_name) values ('%s')", name))) {
                prepareStatement.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                //ignore
            } finally {
                preparedStatement.close();
            }
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
        }
    }

    public void unlock() {
        // commit
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                //ignore
            }
            try {
                conn.setAutoCommit(connAutoCommit);
            } catch (SQLException e) {
                //ignore
            }
            try {
                conn.close();
            } catch (SQLException e) {
                //ignore
            }
        }

        // close PreparedStatement
        if (null != preparedStatement) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }
}

package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author zheng
 */
public class LockHelper {
    private static final Logger logger = LoggerFactory.getLogger(LockHelper.class);

    private static boolean connAutoCommit;
    private static Connection conn = null;
    private static PreparedStatement preparedStatement = null;
    private static long nowTime = 0;
    private static long releaseTime = 0;
    private static long cycleTime = 0;

    /**
     * 加锁
     * @return
     */
    public static boolean tryLockGroup(){
        boolean lockSuccess;
        nowTime = System.currentTimeMillis();
        cycleTime = 0;

        do {
            try {
                conn = XxlJobAdminConfig.getAdminConfig().getDataSource().getConnection();
                connAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                preparedStatement = conn.prepareStatement("select * from xxl_job_lock where lock_name = 'group_lock' for update");
                preparedStatement.execute();
                cycleTime = System.currentTimeMillis();
                lockSuccess = true;
                break;
            } catch (SQLException ex) {
                lockSuccess = false;
                logger.error("加锁失败，将在500毫秒后开始重试", ex);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("加锁失败，重试线程被迫中断！", e);
                }
            }
        }while ((cycleTime - nowTime) < 3000);
        return lockSuccess;
    }

    /**
     * 解锁
     */
    public static void unLockGroup(){
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                logger.error("解锁时提交发生异常！", e);
            }
            try {
                conn.setAutoCommit(connAutoCommit);
            } catch (SQLException e) {
                logger.error("解锁时恢复数据库提交方式发生异常！", e);
            }
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("解锁时关闭数据库连接发生异常！", e);
            }
        }
        // close PreparedStatement
        if (null != preparedStatement) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                logger.error("解锁时关闭preparedStatement发生异常！", e);
            }
        }
        releaseTime = System.currentTimeMillis();
        logger.info("本次用锁耗时：{}毫秒", releaseTime - cycleTime);
    }
}

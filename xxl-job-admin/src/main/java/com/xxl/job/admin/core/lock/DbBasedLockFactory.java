package com.xxl.job.admin.core.lock;

/**
 * Created on 2022/2/15.
 *
 * @author lan
 */
public final class DbBasedLockFactory {

    public static DbBasedLock getLock(String lockName) {
        return new DbBasedLock(lockName);
    }
}

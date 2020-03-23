package com.xxl.job.admin.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author dudiao
 * @date 2020/3/23 下午 04:49
 */
@Entity
@Table(name = "xxl_job_lock")
public class XxlJobLock {

    /**
     * 锁名称
     */
    @Id
    @Column(name = "lock_name", length = 50)
    private String lockName;

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
}

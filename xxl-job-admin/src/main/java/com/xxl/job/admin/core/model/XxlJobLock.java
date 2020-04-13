package com.xxl.job.admin.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author YunSongLiu
 */
@Entity
@Table(name = "job_lock")
public class XxlJobLock {

    @Id
    @Column(name = "lockName",length = 50)
    private String lockName;

    public String getLockName() {
        return lockName;
    }

    public XxlJobLock setLockName(String lockName) {
        this.lockName = lockName;
        return this;
    }
}

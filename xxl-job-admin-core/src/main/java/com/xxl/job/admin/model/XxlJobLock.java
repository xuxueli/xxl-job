package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * xxl-job lock
 *
 * @author xuxueli  2016-1-12
 */
@TableName("xxl_job_lock")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XxlJobLock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 锁名称
     */
    private String lockName;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLockName() { return lockName; }
    public void setLockName(String lockName) { this.lockName = lockName; }
}
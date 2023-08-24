package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 任务锁
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Data
@TableName("XXL_JOB_LOCK")
public class LockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 锁名称
     */
    private String lockName;


}

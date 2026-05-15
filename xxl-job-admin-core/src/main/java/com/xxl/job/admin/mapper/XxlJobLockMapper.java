package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.model.XxlJobLock;
import org.apache.ibatis.annotations.*;

/**
 * job lock
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface XxlJobLockMapper extends BaseMapper<XxlJobLock> {

    /**
     * 获取调度锁（兼容旧方法名）
     */
    @Select("SELECT lock_name FROM xxl_job_lock WHERE lock_name = 'schedule_lock' FOR UPDATE")
    String scheduleLock();
}
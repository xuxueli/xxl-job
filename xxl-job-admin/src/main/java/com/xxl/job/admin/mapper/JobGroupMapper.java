package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.entity.JobGroup;
import com.xxl.job.admin.common.pojo.query.JobGroupQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 执行器组 Mapper 接口
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
public interface JobGroupMapper extends BaseMapper<JobGroup> {

    /**
     * 查询任务组
     *
     * @param query 查询
     * @return {@link List}<{@link JobGroup}>
     */
    List<JobGroup> queryJobGroup(JobGroupQuery query);

    /**
     * 查询任务组根据appName
     *
     * @param appName 应用程序名称
     * @return {@link JobGroup}
     */
    JobGroup queryJobGroupByAppName(@Param("appName") String appName);

    /**
     * 查询任务组根据地址类型
     *
     * @param addressType 执行器地址类型：0=自动注册、1=手动录入
     * @return {@link List}<{@link JobGroup}>
     */
    List<JobGroup> queryJobGroupByAddressType(@Param("addressType") Integer addressType);














}

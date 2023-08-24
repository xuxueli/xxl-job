package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.JobGroupDTO;
import com.xxl.job.admin.common.pojo.entity.JobGroup;
import com.xxl.job.admin.common.pojo.vo.JobGroupVO;
import com.xxl.job.admin.service.base.BaseService;

import java.util.List;

/**
 * <p>
 * 执行器组 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
public interface JobGroupService extends BaseService<JobGroup, JobGroup, JobGroupVO> {

    /**
     * 保存任务组
     *
     * @param jobGroupDTO 工作组DTO
     */
    void saveJobGroup(JobGroupDTO jobGroupDTO);

    /**
     * 保存任务组
     *
     * @param jobGroupDTO 工作组DTO
     */
    void updateJobGroup(JobGroupDTO jobGroupDTO);

    /**
     * 查询任务组根据地址类型
     *
     * @param addressType 执行器地址类型：0=自动注册、1=手动录入
     * @return {@link List}<{@link JobGroupVO}>
     */
    List<JobGroupVO> queryJobGroupByAddressType(Integer addressType);

    /**
     * 查询所有
     *
     * @return {@link List}<{@link JobGroupVO}>
     */
    List<JobGroupVO> findAll();







}

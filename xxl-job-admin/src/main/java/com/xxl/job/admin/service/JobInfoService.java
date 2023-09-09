package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.JobInfoDTO;
import com.xxl.job.admin.common.pojo.dto.TriggerJobDTO;
import com.xxl.job.admin.common.pojo.entity.JobInfo;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.service.base.BaseService;

import java.util.List;

/**
 * <p>
 * 任务信息 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface JobInfoService extends BaseService<JobInfo, JobInfo, JobInfoVO> {

    /**
     * 保存任务信息
     *
     * @param jobInfoDTO 任务DTO
     * @return {@link JobInfoVO}
     */
    JobInfoVO saveJobInfo(JobInfoDTO jobInfoDTO);

    /**
     * 更新任务信息
     *
     * @param jobInfoDTO 任务DTO
     * @return {@link JobInfoVO}
     */
    JobInfoVO updateJobInfo(JobInfoDTO jobInfoDTO);

    /**
     * 更新状态根据id
     *
     * @param id     id
     * @param status 调度状态：0-停止，1-运行
     */
    void updateStatusById(Long id, Integer status);

    /**
     * 根据任务组ID查询任务信息
     *
     * @param groupId 组id
     * @return {@link List}<{@link JobInfoVO}>
     */
    List<JobInfoVO> queryJobInfoByGroupId(Long groupId);

    /**
     * 更新GLUE根据id
     *
     * @param id              id
     * @param glueSource      GLUE源代码
     * @param glueType glue 类型
     * @param glueDescription GLUE描述
     */
    void updateGlueById(Long id, String glueType, String glueSource, String glueDescription);

    /**
     * 根据下次调度时间查询任务
     *
     * @param maxNextTime 下次调度时间
     * @param pageSize 数量
     * @return {@link List}<{@link JobInfoVO}>
     */
    List<JobInfoVO> queryJobInfoByTriggerNextTime(long maxNextTime, Integer pageSize);

    /**
     * 根据ID修改调度时间和状态
     *
     * @param id              ID
     * @param triggerLastTime 上次触发
     * @param triggerNextTime 下次触发
     * @param triggerStatus   触发状态
     */
    void updateTriggerTimeById(Long id, Long triggerLastTime, Long triggerNextTime, Integer triggerStatus);

    /**
     * 查询所有数
     *
     * @return {@link Long}
     */
    Long findAllCount();

    /**
     * 停止任务
     *
     * @param id id
     */
    void stopJob(Long id);

    /**
     * 开始任务
     *
     * @param id id
     */
    void startJob(Long id);

    /**
     * 触发器任务
     *
     * @param triggerJobDTO 触发任务DTO
     */
    void triggerJob(TriggerJobDTO triggerJobDTO);

    /**
     * 下一个触发时间
     *
     * @param id id
     * @return {@link List}<{@link String}>
     */
    List<String> nextTriggerTime(Long id);

    /**
     * 根据CRON表达式获取最近执行时间
     * @param cron CRON表达式
     * @return {@link List}<{@link String}>
     */
    List<String> cronLatestExecutionTime(String cron);




}

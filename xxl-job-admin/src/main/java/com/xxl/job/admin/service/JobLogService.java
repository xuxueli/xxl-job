package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.HandleLogDTO;
import com.xxl.job.admin.common.pojo.dto.JobLogCleanDTO;
import com.xxl.job.admin.common.pojo.dto.JobLogDTO;
import com.xxl.job.admin.common.pojo.dto.TriggerLogDTO;
import com.xxl.job.admin.common.pojo.entity.JobLog;
import com.xxl.job.admin.common.pojo.vo.JobLogReportVO;
import com.xxl.job.admin.common.pojo.vo.JobLogVO;
import com.xxl.job.admin.service.base.BaseService;
import com.xxl.job.core.pojo.vo.LogResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务日志信息 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface JobLogService extends BaseService<JobLog, JobLog, JobLogVO> {

    /**
     * 查询取消注册的任务的日志ID
     *
     * @param loseTime 浪费时间
     * @return {@link List}<{@link Long}>
     */
    List<Long> queryLostJobIds(Date loseTime);

    /**
     * 新增任务日志
     *
     * @param jobLogDTO 工作日志DTO
     * @return {@link Long}
     */
    Long saveJobLog(JobLogDTO jobLogDTO);

    /**
     * 更新触发器信息
     *
     * @param triggerLogDTO 触发日志DTO
     */
    void updateTriggerInfo(TriggerLogDTO triggerLogDTO);

    /**
     * 更新处理信息
     *
     * @param handleLogDTO 处理日志DTO
     */
    void updateHandleInfo(HandleLogDTO handleLogDTO);

    /**
     * 查询失败任务日志id
     *
     * @param pageSize 数量
     * @return {@link List}<{@link Long}>
     */
    List<Long> findFailJobLogIds(Integer pageSize);

    /**
     * 更新报警状态
     *
     * @param logId          日志id
     * @param oldAlarmStatus 旧报警状态
     * @param newAlarmStatus 新报警状态
     * @return int
     */
    int updateAlarmStatus(Long logId, Integer oldAlarmStatus, Integer newAlarmStatus);

    /**
     * 查询日志报告根据触发时间
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return {@link JobLogReportVO}
     */
    JobLogReportVO queryLogReportByTriggerTime(@Param("from") Date from, @Param("to") Date to);

    /**
     * 查询清除日志id
     *
     * @param groupId         任务组ID
     * @param jobIds           任务ID
     * @param clearBeforeTime 清除时间之前的
     * @param clearBeforeNum  清除数量之前的
     * @param pageSize        数量
     * @return {@link List}<{@link Long}>
     */
    List<Long> queryClearLogIds(Long groupId, List<Long> jobIds, Date clearBeforeTime, Long clearBeforeNum, Integer pageSize);

    /**
     * 清除日志
     *
     * @param logIds 日志id
     */
    void clearLog(List<Long> logIds);

    /**
     * 删除日志根据任务id
     *
     * @param jobId 任务id
     */
    void deleteLogByJobId(Long jobId);

    /**
     * 查询执行日志
     *
     * @param logId       日志id
     * @param fromLineNum 开始行
     * @return {@link LogResult}
     */
    LogResult catJobLog(Long logId, Integer fromLineNum);

    /**
     * 删除任务日志
     *
     * @param id id
     */
    void killJobLog(Long id);

    /**
     * 清理任务日志
     *
     * @param jobLogCleanDTO 日志DTO
     */
    void cleanJobLog(JobLogCleanDTO jobLogCleanDTO);




}

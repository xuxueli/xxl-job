package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.bo.JobLogReportBO;
import com.xxl.job.admin.common.pojo.entity.JobLog;
import com.xxl.job.admin.common.pojo.query.JobLogQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 任务日志信息 Mapper 接口
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface JobLogMapper extends BaseMapper<JobLog> {

    /**
     * 更新触发器信息
     *
     * @param jobLog 日志
     */
    void updateTriggerInfo(JobLog jobLog);

    /**
     * 更新处理信息
     *
     * @param jobLog 日志
     */
    void updateHandleInfo(JobLog jobLog);

    /**
     * 删除日志根据任务id
     *
     * @param jobId 任务id
     */
    void deleteLogByJobId(@Param("jobId") Long jobId);

    /**
     * 查询日志报告根据触发时间
     *
     * @param from 开始时间
     * @param to   结束时间
     * @return {@link JobLogReportBO}
     */
    JobLogReportBO queryLogReportByTriggerTime(@Param("from") Long from,
                                               @Param("to") Long to);

    /**
     * 查询清除日志id
     *
     * @param groupId        任务组ID
     * @param jobIds           任务ID
     * @param clearBeforeTime 清除时间之前的
     * @param clearBeforeNum  清除数量之前的
     * @param pageSize        页大小
     * @return {@link List}<{@link Long}>
     */
    List<Long> queryClearLogIds(@Param("groupId") Long groupId,
                                      @Param("jobIds") List<Long> jobIds,
                                      @Param("clearBeforeTime") Long clearBeforeTime,
                                      @Param("clearBeforeNum") Long clearBeforeNum,
                                      @Param("pageSize") Integer pageSize);

    /**
     * 清除日志
     *
     * @param logIds 日志id
     */
    void clearLog(@Param("logIds") List<Long> logIds);

    /**
     * 查询失败任务日志id
     *
     * @param pageSize 页大小
     * @return {@link List}<{@link Long}>
     */
    List<Long> findFailJobLogIds(@Param("pageSize") Integer pageSize);

    /**
     * 更新报警状态
     *
     * @param logId          日志id
     * @param oldAlarmStatus 旧报警状态
     * @param newAlarmStatus 新报警状态
     * @return int 修改数量
     */
    int updateAlarmStatus(@Param("logId") Long logId,
                                 @Param("oldAlarmStatus") Integer oldAlarmStatus,
                                 @Param("newAlarmStatus") Integer newAlarmStatus);

    /**
     * 查询取消注册的任务的日志ID
     *
     * @param loseTime 浪费时间
     * @return {@link List}<{@link Long}>
     */
    List<Long> queryLostJobIds(@Param("loseTime") Long loseTime);

    /**
     * 查询任务日志
     *
     * @param query 查询
     * @return {@link List}<{@link JobLog}>
     */
    List<JobLog> queryJobLog(JobLogQuery query);





}

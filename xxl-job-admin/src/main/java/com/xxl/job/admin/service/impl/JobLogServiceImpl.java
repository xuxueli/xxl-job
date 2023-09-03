package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.enums.TriggerTypeEnum;
import com.xxl.job.admin.common.pojo.dto.*;
import com.xxl.job.admin.common.pojo.entity.JobLog;
import com.xxl.job.admin.common.pojo.query.JobLogQuery;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.pojo.vo.JobLogReportVO;
import com.xxl.job.admin.common.pojo.vo.JobLogVO;
import com.xxl.job.admin.mapper.JobLogMapper;
import com.xxl.job.admin.service.ExecutorClient;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.admin.thread.TriggerThreadPool;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.pojo.dto.KillParam;
import com.xxl.job.core.pojo.dto.LogParam;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;
import com.xxl.job.core.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务日志信息 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Service
public class JobLogServiceImpl extends BaseServiceImpl<JobLogMapper, JobLog, JobLog, JobLogVO> implements JobLogService {

    private static final String TRIGGER_TIME = "trigger_time";

    @Autowired
    private JobLogMapper jobLogMapper;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private TriggerThreadPool jobTriggerThreadPool;

    @Autowired
    private ExecutorClient executorClient;

    @Override
    public List<JobLog> queryList(PageDTO pageDTO) {
        JobLogQuery query = new JobLogQuery();
        BeanUtil.copyProperties(pageDTO, query);
        if (StrUtil.isBlank(query.getOrderField())) query.setOrderField(TRIGGER_TIME);
        return jobLogMapper.queryJobLog(query);
    }

    @Override
    public JobLogVO queryById(Serializable id) {
        return this.objectConversion(this.getById(id));
    }

    @Override
    public List<Long> queryLostJobIds(Long loseTime) {
        return jobLogMapper.queryLostJobIds(loseTime);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Long syncJobLog(JobLogDTO jobLogDTO) {
        JobLog jobLog = ObjectUtil.isNotNull(jobLogDTO.getId())
                ? this.getById(jobLogDTO.getId()) : new JobLog();
        if (ObjectUtil.isEmpty(jobLog)) jobLog = new JobLog();
        BeanUtil.copyProperties(jobLogDTO, jobLog, CopyOptions.create().setIgnoreNullValue(Boolean.TRUE));
        this.saveOrUpdate(jobLog);
        return jobLog.getId();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateTriggerInfo(TriggerLogDTO triggerLogDTO) {
        JobLog jobLog = this.getById(triggerLogDTO.getId());
        if (ObjectUtil.isEmpty(jobLog)) return;
        BeanUtil.copyProperties(triggerLogDTO, jobLog);
        jobLogMapper.updateTriggerInfo(jobLog);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateHandleInfo(HandleLogDTO handleLogDTO) {
        JobLog jobLog = this.getById(handleLogDTO.getId());
        if (ObjectUtil.isEmpty(jobLog)) return;
        BeanUtil.copyProperties(handleLogDTO, jobLog);
        jobLog.setHandleMessage(finishJob(jobLog));
        jobLogMapper.updateHandleInfo(jobLog);
    }

    @Override
    public List<Long> findFailJobLogIds(Integer pageSize) {
        return jobLogMapper.findFailJobLogIds(pageSize);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int updateAlarmStatus(Long logId, Integer oldAlarmStatus, Integer newAlarmStatus) {
        if (ObjectUtil.isNotEmpty(this.getById(logId))) {
            return jobLogMapper.updateAlarmStatus(logId, oldAlarmStatus, newAlarmStatus);
        }
        return NumberConstant.ZERO;
    }

    @Override
    public JobLogReportVO queryLogReportByTriggerTime(Long from, Long to) {
        return BeanUtil.copyProperties(jobLogMapper.queryLogReportByTriggerTime(from, to), JobLogReportVO.class);
    }

    @Override
    public List<Long> queryClearLogIds(Long groupId,List<Long> jobIds, Long clearBeforeTime, Long clearBeforeNum, Integer pageSize) {
        if (CollectionUtil.isNotEmpty(jobIds) && jobIds.stream().anyMatch(a -> ObjectUtil.equals(NumberConstant.A_NEGATIVE.longValue(), a))) {
            jobIds.clear();
        }
        return jobLogMapper.queryClearLogIds(groupId, jobIds, clearBeforeTime, clearBeforeNum, pageSize);
    }

    @Override
    public void clearLog(List<Long> logIds) {
        if (CollectionUtil.isEmpty(logIds)) return;
        jobLogMapper.clearLog(logIds);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteLogByJobId(Long jobId) {
        jobLogMapper.deleteLogByJobId(jobId);
    }

    @Override
    public LogResult catJobLog(Long logId, Integer fromLineNum) {
        JobLog jobLog = this.getById(logId);
        LogParam logParam = new LogParam(jobLog.getTriggerTime(), logId, fromLineNum);
        LogResult logResult = executorClient.log(jobLog.getExecutorAddress(), logParam);
        if (ObjectUtil.isNotNull(logResult)
                && logResult.getFromLineNum() > logResult.getToLineNum() && jobLog.getHandleCode() > 0) {
            logResult.setEnd(Boolean.TRUE);
        }
        return logResult;
    }

    @Override
    public void killJobLog(Long id) {
        JobLog jobLog = this.getById(id);
        JobInfoVO jobInfoVO = null;
        if (ObjectUtil.isNotNull(id) && ObjectUtil.isNotNull(jobInfoVO = jobInfoService.queryById(jobLog.getJobId()))) {
           try {
               ResponseVO responseVO = executorClient.kill(jobLog.getExecutorAddress(), new KillParam(jobInfoVO.getId()));
               if (ResponseUtils.isSuccess(responseVO)) {
                   jobLog.setHandleCode(ResponseEnum.ERROR.getCode());
                   jobLog.setHandleMessage("人为操作，主动终止 :" + responseVO.getMessage());
                   jobLog.setHandleMessage(finishJob(jobLog));
                   jobLog.setHandleTime(DateUtil.current());
                   this.saveOrUpdate(jobLog);
               }
           }catch (Exception e) {
               log.error("killJobLog {}", e.getMessage());
           }
        }
    }

    @Override
    public JobLogVO objectConversion(JobLog jobLog) {
        JobLogVO jobLogVO = super.objectConversion(jobLog);
        if (ObjectUtil.isNotNull(jobLogVO)) {
            jobLogVO.setJob(jobInfoService.queryById(jobLog.getJobId()));
            jobLogVO.setGroup(jobGroupService.queryById(jobLog.getGroupId()));
        }
        return jobLogVO;
    }

    @Override
    public void cleanJobLog(JobLogCleanDTO jobLogCleanDTO) {
        Integer type = jobLogCleanDTO.getType();
        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtil.offsetMonth(new Date(), -1);	// 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtil.offsetMonth(new Date(), -3);	// 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtil.offsetMonth(new Date(), -6);	// 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtil.offsetMonth(new Date(), -1);	// 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;		// 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;		// 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;		// 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;	// 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;			// 清理所有日志数据
        } else {
            clearBeforeNum = 0;
        }

        List<Long> logIds = null;
        do {
            logIds = this.queryClearLogIds(jobLogCleanDTO.getGroupId(), jobLogCleanDTO.getJobIds(),
                    clearBeforeTime.getTime(), Convert.toLong(clearBeforeNum), 1000);
            if (CollectionUtil.isNotEmpty(logIds)) {
                clearLog(logIds);
            }
        } while (CollectionUtil.isNotEmpty(logIds));
    }

    private String finishJob(JobLog jobLog){

        // 1、handle success, to trigger child job
        StringBuilder triggerChildMessage = null;
        if (ObjectUtil.equals(ResponseEnum.SUCCESS.getCode(), jobLog.getHandleCode())) {
            JobInfoVO jobInfoVO = jobInfoService.queryById(jobLog.getJobId());
            if (ObjectUtil.isNotNull(jobInfoVO) && CollectionUtil.isNotEmpty(jobInfoVO.getChildJobIds())) {
                triggerChildMessage = new StringBuilder("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发子任务<<<<<<<<<<< </span><br>");
                List<Long> childJobIds = jobInfoVO.getChildJobIds();
                for (int i = 0; i < childJobIds.size(); i++) {
                    Long childJobId = childJobIds.get(i);
                    jobTriggerThreadPool.addTrigger(childJobId, TriggerTypeEnum.PARENT,
                            -1, null, null, null);

                    // add msg
                    triggerChildMessage.append(MessageFormat.format("{0}/{1} [任务ID={2}], 触发{3}, 触发备注: {4} <br>",
                            (i + 1),
                            childJobIds.size(),
                            childJobId,
                            ResponseEnum.SUCCESS.getCode(),
                            ResponseEnum.SUCCESS.getMessage()
                    ));

                }
            }
        }

        if (ObjectUtil.isNotNull(triggerChildMessage)) {
            return jobLog.getHandleMessage() + triggerChildMessage.toString();
        }

        // 2、fix_delay trigger next
        // on the way
        return jobLog.getHandleMessage();
    }



}

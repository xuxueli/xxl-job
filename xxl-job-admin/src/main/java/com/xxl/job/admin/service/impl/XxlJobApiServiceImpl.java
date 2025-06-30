package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.alarm.AlarmTypeEnum;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.model.bo.XxlJobInfoBo;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.service.XxlJobApiService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: Dao-yang.
 * @date: Created in 2025/6/30 11:07
 */
@Service
public class XxlJobApiServiceImpl implements XxlJobApiService {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobApiServiceImpl.class);

    @Resource
    private XxlJobService xxlJobService;

    @Override
    public ReturnT<String> add(XxlJobInfoBo jobInfo) {

        if (jobInfo.getEndTime() != null) {
            if (jobInfo.getEndExecutorHandler() == null) {
                logger.error("任务结束handler为空");
                throw new XxlJobException("任务结束handler为空");
            }
            if (jobInfo.getEndTime().before(new Date())) {
                logger.error("任务结束时间小于当前时间,endTime:{}", jobInfo.getEndTime());
                throw new XxlJobException("任务结束时间小于当前时间");
            }
        }

        XxlJobUser xxlJobUser = new XxlJobUser();
        xxlJobUser.setRole(1); // 1-管理员,不需权限控制
        ReturnT<String> added = xxlJobService.add(jobInfo, xxlJobUser);
        if (added.getCode() == ReturnT.SUCCESS_CODE && jobInfo.getEndTime() != null) {
            logger.info("添加定时删除任务,jobId:{},jobName:{}", jobInfo.getId(), jobInfo.getJobDesc());
            addEndXxlJobInfo(jobInfo, added, xxlJobUser);
        }
        return added;
    }

    private void addEndXxlJobInfo(XxlJobInfoBo jobInfo, ReturnT<String> added, XxlJobUser xxlJobUser) {
        XxlJobInfo xxlJobInfo = getEndXxlJobInfo(jobInfo, added);
        ReturnT<String> ret2 = xxlJobService.add(xxlJobInfo, xxlJobUser);
        if (ret2.getCode() == 200) {
            logger.info("定时删除任务添加成功,jobId:{}", ret2.getContent());
        }
    }

    private XxlJobInfo getEndXxlJobInfo(XxlJobInfoBo jobInfo, ReturnT<String> added) {
        String jobId = added.getContent();
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setJobDesc("定时删除-" + jobId + "-" + jobInfo.getJobDesc());
        xxlJobInfo.setAuthor("System");
        xxlJobInfo.setScheduleType(ScheduleTypeEnum.FIX_RATE.name());// 固定频率
        xxlJobInfo.setScheduleConf("30");// 间隔30秒
        xxlJobInfo.setExecutorHandler(jobInfo.getEndExecutorHandler());// 执行handler
        xxlJobInfo.setExecutorParam(jobId);// 执行参数(被结束的任务id)
        xxlJobInfo.setExecutorTimeout(0); // 执行超时时间
        xxlJobInfo.setExecutorFailRetryCount(1);// 失败重试次数
        xxlJobInfo.setTriggerStatus(1);// 触发状态:启动
        xxlJobInfo.setTriggerNextTime(jobInfo.getEndTime().getTime());
        xxlJobInfo.setJobGroup(jobInfo.getJobGroup());// 任务组(任务执行器ID)
        xxlJobInfo.setAlarmType(AlarmTypeEnum.NOT.getAlarmType());
        xxlJobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());
        xxlJobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.LEAST_FREQUENTLY_USED.name());
        xxlJobInfo.setMisfireStrategy(MisfireStrategyEnum.DO_NOTHING.name());
        xxlJobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        return xxlJobInfo;
    }

    @Override
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }

    @Override
    public ReturnT<String> start(int id) {
        return xxlJobService.start(id);
    }

    @Override
    public ReturnT<String> stop(int id) {
        return xxlJobService.stop(id);
    }
}

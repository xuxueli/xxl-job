package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.constant.TriggerStatus;
import com.xxl.job.admin.core.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.core.mapper.XxlJobLogMapper;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.core.scheduler.cron.CronExpression;
import com.xxl.job.admin.core.scheduler.misfire.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.thread.JobScheduleHelper;
import com.xxl.job.admin.core.scheduler.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.scheduler.type.ScheduleTypeEnum;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.PageModel;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JobInfo service implementation for xxl-job core module.
 * Refactored from XxlJobServiceImpl to remove web-layer dependencies.
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {
    private static final Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);

    private static final String CHILD_JOB_ID_SEPARATOR = ",";

    @Resource
    private XxlJobGroupMapper xxlJobGroupMapper;
    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;
    @Resource
    private XxlJobLogMapper xxlJobLogMapper;
    @Resource
    private XxlJobLogGlueMapper xxlJobLogGlueMapper;

    @Override
    public int add(XxlJobInfo jobInfo, int userId) {
        // valid base
        XxlJobGroup group = xxlJobGroupMapper.load(jobInfo.getJobGroup());
        if (group == null) {
            return 0;
        }
        if (StringTool.isBlank(jobInfo.getJobDesc())) {
            return 0;
        }
        if (StringTool.isBlank(jobInfo.getAuthor())) {
            return 0;
        }

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (!validateScheduleType(scheduleTypeEnum, jobInfo.getScheduleConf())) {
            return 0;
        }

        // valid job
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            return 0;
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && StringTool.isBlank(jobInfo.getExecutorHandler())) {
            return 0;
        }
        // fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // valid advanced
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return 0;
        }
        if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
            return 0;
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return 0;
        }

        // ChildJobId valid
        String normalizedChildJobId = normalizeChildJobIds(jobInfo.getChildJobId(), jobInfo.getId());
        if (normalizedChildJobId == null) {
            return 0;
        }
        jobInfo.setChildJobId(normalizedChildJobId);

        // add in db
        jobInfo.setAddTime(new Date());
        jobInfo.setUpdateTime(new Date());
        jobInfo.setGlueUpdatetime(new Date());
        // remove the whitespace
        jobInfo.setExecutorHandler(jobInfo.getExecutorHandler().trim());
        xxlJobInfoMapper.save(jobInfo);
        if (jobInfo.getId() < 1) {
            return 0;
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "jobinfo-add", GsonTool.toJson(jobInfo));

        return jobInfo.getId();
    }

    @Override
    public boolean update(XxlJobInfo jobInfo, int userId) {
        // valid base
        if (StringTool.isBlank(jobInfo.getJobDesc())) {
            return false;
        }
        if (StringTool.isBlank(jobInfo.getAuthor())) {
            return false;
        }

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        if (!validateScheduleType(scheduleTypeEnum, jobInfo.getScheduleConf())) {
            return false;
        }

        // valid advanced
        if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
            return false;
        }
        if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
            return false;
        }
        if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
            return false;
        }

        // ChildJobId valid
        String normalizedChildJobId = normalizeChildJobIds(jobInfo.getChildJobId(), jobInfo.getId());
        if (normalizedChildJobId == null) {
            return false;
        }

        // group valid
        XxlJobGroup jobGroup = xxlJobGroupMapper.load(jobInfo.getJobGroup());
        if (jobGroup == null) {
            return false;
        }

        // stage job info
        XxlJobInfo exists_jobInfo = xxlJobInfoMapper.loadById(jobInfo.getId());
        if (exists_jobInfo == null) {
            return false;
        }

        // next trigger time (5s after生效，避开预读周期)
        long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
        boolean scheduleDataNotChanged = jobInfo.getScheduleType().equals(exists_jobInfo.getScheduleType())
                && jobInfo.getScheduleConf().equals(exists_jobInfo.getScheduleConf());
        if (exists_jobInfo.getTriggerStatus() == TriggerStatus.RUNNING.getValue() && !scheduleDataNotChanged) {
            try {
                // generate next trigger time
                Date nextValidTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(jobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
                if (nextValidTime == null) {
                    return false;
                }
                nextTriggerTime = nextValidTime.getTime();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return false;
            }
        }

        exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setAuthor(jobInfo.getAuthor());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setScheduleType(jobInfo.getScheduleType());
        exists_jobInfo.setScheduleConf(jobInfo.getScheduleConf());
        exists_jobInfo.setMisfireStrategy(jobInfo.getMisfireStrategy());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        // remove the whitespace
        exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler().trim());
        exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
        exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
        exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
        exists_jobInfo.setChildJobId(normalizedChildJobId);
        exists_jobInfo.setTriggerNextTime(nextTriggerTime);

        exists_jobInfo.setUpdateTime(new Date());
        xxlJobInfoMapper.update(exists_jobInfo);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "jobinfo-update", GsonTool.toJson(exists_jobInfo));

        return true;
    }

    @Override
    public boolean remove(int id, int userId) {
        // valid job
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(id);
        if (xxlJobInfo == null) {
            return true;
        }

        xxlJobInfoMapper.delete(id);
        xxlJobLogMapper.delete(id);
        xxlJobLogGlueMapper.deleteByJobId(id);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "jobinfo-remove", id);

        return true;
    }

    @Override
    public boolean start(int id, int userId) {
        // load and valid
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(id);
        if (xxlJobInfo == null) {
            return false;
        }

        // valid ScheduleType: can not be none
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(xxlJobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
        if (ScheduleTypeEnum.NONE == scheduleTypeEnum) {
            return false;
        }

        // next trigger time (5s after生效，避开预读周期)
        long nextTriggerTime = 0;
        try {
            // generate next trigger time
            Date nextValidTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(xxlJobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));

            if (nextValidTime == null) {
                return false;
            }
            nextTriggerTime = nextValidTime.getTime();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        xxlJobInfo.setTriggerStatus(TriggerStatus.RUNNING.getValue());
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(nextTriggerTime);

        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfoMapper.update(xxlJobInfo);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "jobinfo-start", id);

        return true;
    }

    @Override
    public boolean stop(int id, int userId) {
        // load and valid
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(id);
        if (xxlJobInfo == null) {
            return false;
        }

        // stop
        xxlJobInfo.setTriggerStatus(TriggerStatus.STOPPED.getValue());
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(0);

        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfoMapper.update(xxlJobInfo);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "jobinfo-stop", id);

        return true;
    }

    @Override
    public boolean trigger(int jobId, int userId, String executorParam, String addressList) {
        // valid job
        XxlJobInfo xxlJobInfo = xxlJobInfoMapper.loadById(jobId);
        if (xxlJobInfo == null) {
            return false;
        }

        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        XxlJobAdminBootstrap.getInstance().getJobTriggerPoolHelper().trigger(jobId, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorId = {}, type = {}, content = {}",
                userId, "jobinfo-trigger", jobId);

        return true;
    }

    @Override
    public PageModel<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        // page list
        List<XxlJobInfo> list = xxlJobInfoMapper.pageList(offset, pagesize, jobGroup, triggerStatus, jobDesc, executorHandler, author);
        int list_count = xxlJobInfoMapper.pageListCount(offset, pagesize, jobGroup, triggerStatus, jobDesc, executorHandler, author);

        // package result
        PageModel<XxlJobInfo> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(list_count);

        return pageModel;
    }

    @Override
    public List<String> generateNextTriggerTime(XxlJobInfo jobInfo) {
        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                // generate next trigger time
                ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
                lastTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(jobInfo, lastTime);

                // collect data
                if (lastTime != null) {
                    result.add(DateTool.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> generateNextTriggerTime error. jobInfo = {}, error: {} ", GsonTool.toJson(jobInfo), e.getMessage());
        }
        return result;
    }

    /**
     * Validate schedule type and schedule config.
     * Returns true if valid, false otherwise.
     */
    private boolean validateScheduleType(ScheduleTypeEnum scheduleTypeEnum, String scheduleConf) {
        if (scheduleTypeEnum == null) {
            return false;
        }
        if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
            return scheduleConf != null && CronExpression.isValidExpression(scheduleConf);
        } else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE) {
            if (scheduleConf == null) {
                return false;
            }
            try {
                int fixSecond = Integer.parseInt(scheduleConf);
                return fixSecond >= 1;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate and normalize child job IDs.
     * Returns normalized childJobId string if valid, null if invalid.
     * If checkSelfId is -1, skip self-reference check (for add operation).
     */
    private String normalizeChildJobIds(String childJobId, int checkSelfId) {
        if (StringTool.isBlank(childJobId)) {
            return childJobId;
        }
        String[] childJobIds = childJobId.split(CHILD_JOB_ID_SEPARATOR);
        List<String> validChildJobIds = new ArrayList<>();
        for (String childJobIdItem : childJobIds) {
            if (StringTool.isNotBlank(childJobIdItem) && StringTool.isNumeric(childJobIdItem)) {
                int childId = Integer.parseInt(childJobIdItem);
                if (checkSelfId != -1 && childId == checkSelfId) {
                    return null;
                }
                XxlJobInfo childJobInfo = xxlJobInfoMapper.loadById(childId);
                if (childJobInfo == null) {
                    return null;
                }
                validChildJobIds.add(childJobIdItem);
            } else {
                return null;
            }
        }
        if (validChildJobIds.isEmpty()) {
            return "";
        }
        return String.join(CHILD_JOB_ID_SEPARATOR, validChildJobIds);
    }
}
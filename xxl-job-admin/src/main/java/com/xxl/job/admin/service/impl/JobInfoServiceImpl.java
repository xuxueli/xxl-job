package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.enums.ScheduleTypeEnum;
import com.xxl.job.admin.common.enums.TriggerTypeEnum;
import com.xxl.job.admin.common.exceptions.XxlJobAdminException;
import com.xxl.job.admin.common.pojo.dto.JobInfoDTO;
import com.xxl.job.admin.common.pojo.dto.PageDTO;
import com.xxl.job.admin.common.pojo.dto.TriggerJobDTO;
import com.xxl.job.admin.common.pojo.entity.JobInfo;
import com.xxl.job.admin.common.pojo.query.JobInfoQuery;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.utils.CronUtils;
import com.xxl.job.admin.mapper.JobInfoMapper;
import com.xxl.job.admin.service.GlueLogService;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.admin.thread.ScheduleThread;
import com.xxl.job.admin.thread.TriggerThreadPool;
import com.xxl.job.core.enums.GlueTypeEnum;
import com.xxl.job.core.enums.ResponseEnum;
import com.xxl.job.core.utils.CronExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务信息 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Service
public class JobInfoServiceImpl extends BaseServiceImpl<JobInfoMapper, JobInfo, JobInfo, JobInfoVO> implements JobInfoService {

    @Autowired
    private JobInfoMapper jobInfoMapper;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private GlueLogService glueLogService;

    @Autowired
    private TriggerThreadPool jobTriggerThreadPool;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean delete(Serializable id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        if (ObjectUtil.isNotNull(this.getById(id))) {
            jobLogService.deleteLogByJobId(Convert.toLong(id));
            glueLogService.deleteGlueLogByJobId(Convert.toLong(id));
        }
        return super.delete(id);
    }

    @Override
    public JobInfoVO queryById(Serializable id) {
        return this.objectConversion(this.getById(id));
    }

    @Override
    public List<JobInfo> queryList(PageDTO pageDTO) {
        JobInfoQuery query = new JobInfoQuery();
        BeanUtil.copyProperties(pageDTO, query);
        return jobInfoMapper.queryJobInfo(query);
    }

    @Override
    public JobInfoVO objectConversion(JobInfo jobInfo) {
        JobInfoVO jobInfoVO = super.objectConversion(jobInfo);
        if (ObjectUtil.isNotNull(jobInfoVO)) {
            jobInfoVO.setJobGroup(jobGroupService.queryById(jobInfo.getGroupId()));
            Optional.ofNullable(jobInfo.getChildJobId())
                    .ifPresent(a -> jobInfoVO.setChildJobIds(StrUtil.split(a, StrUtil.COMMA).stream()
                            .map(Convert::toLong).collect(Collectors.toList())));
        }
        return jobInfoVO;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public JobInfoVO saveJobInfo(JobInfoDTO jobInfoDTO) {
        validate(jobInfoDTO);
        String childJobIds = getChildJobIds(jobInfoDTO.getChildJobIds());
        JobInfo jobInfo = jobInfoMapper.queryJobInfoByName(jobInfoDTO.getName());
        Assert.isNull(jobInfo, ResponseEnum.THE_TASK_ALREADY_EXISTS.getMessage());
        jobInfo = new JobInfo();
        BeanUtil.copyProperties(jobInfoDTO, jobInfo);
        jobInfo.setChildJobId(childJobIds);
        jobInfo.setCreatedTime(DateUtil.date());
        jobInfo.setGlueUpdatedTime(DateUtil.date());

        Date nextTriggerTime = jobInfo.getTriggerNextTime();
        if (ObjectUtil.equals(NumberConstant.ONE, jobInfo.getTriggerStatus())) {
            Date nextValidTime = CronUtils.generateNextValidTime(jobInfoDTO.getScheduleType(), jobInfoDTO.getScheduleConf(),
                    new Date(System.currentTimeMillis() + ScheduleThread.PRE_READ_MS));
            Assert.notNull(nextValidTime, ResponseEnum.THE_CRON_EXPRESSION_FORMAT_IS_INCORRECT.getMessage());
            nextTriggerTime = nextValidTime;
        }
        jobInfo.setTriggerLastTime(DateUtil.date(1));
        jobInfo.setTriggerNextTime(nextTriggerTime);
        this.saveOrUpdate(jobInfo);
        return this.objectConversion(jobInfo);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public JobInfoVO updateJobInfo(JobInfoDTO jobInfoDTO) {
        validate(jobInfoDTO);
        JobInfo jobInfo = this.getById(jobInfoDTO.getId());
        Assert.notNull(jobInfo, ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());

        if (!StrUtil.equals(jobInfo.getName(), jobInfoDTO.getName())) {
            Assert.isNull(jobInfoMapper.queryJobInfoByName(jobInfoDTO.getName()),
                    ResponseEnum.THE_TASK_ALREADY_EXISTS.getMessage());
        }

        String childJobIds = getChildJobIds(jobInfoDTO.getChildJobIds());

        Date nextTriggerTime = jobInfo.getTriggerNextTime();
        boolean scheduleDataNotChanged = StrUtil.equals(jobInfo.getScheduleType(), jobInfoDTO.getScheduleType())
                && StrUtil.equals(jobInfo.getScheduleConf(), jobInfoDTO.getScheduleConf());
        if (ObjectUtil.equals(NumberConstant.ONE, jobInfo.getTriggerStatus()) && !scheduleDataNotChanged) {
            Date nextValidTime = CronUtils.generateNextValidTime(jobInfoDTO.getScheduleType(), jobInfoDTO.getScheduleConf(),
                    new Date(System.currentTimeMillis() + ScheduleThread.PRE_READ_MS));
            Assert.notNull(nextValidTime, ResponseEnum.THE_CRON_EXPRESSION_FORMAT_IS_INCORRECT.getMessage());
            nextTriggerTime = nextValidTime;
        }

        BeanUtil.copyProperties(jobInfoDTO, jobInfo);
        jobInfo.setUpdatedTime(DateUtil.date());
        boolean compare = DateUtil.compare(jobInfo.getTriggerLastTime(), DateUtil.date(1)) > 0;
        jobInfo.setTriggerLastTime(compare ? jobInfo.getTriggerLastTime() : DateUtil.date(1));
        jobInfo.setTriggerNextTime(nextTriggerTime);
        jobInfo.setChildJobId(childJobIds);

        this.saveOrUpdate(jobInfo);
        return this.objectConversion(jobInfo);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateStatusById(Long id, Integer status) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        Assert.notNull(this.getById(id), ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        jobInfoMapper.updateStatusById(id, status);
    }

    @Override
    public List<JobInfoVO> queryJobInfoByGroupId(Long groupId) {
        Assert.notNull(groupId, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        return this.objectConversion(jobInfoMapper.queryJobByGroupId(groupId));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateGlueById(Long id, String glueType, String glueSource, String glueDescription) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        jobInfoMapper.updateGlueById(id, glueType, glueSource, glueDescription,
                DateUtil.formatDateTime(DateUtil.date()), DateUtil.formatDateTime(DateUtil.date()));
    }

    @Override
    public List<JobInfoVO> queryJobInfoByTriggerNextTime(Date maxNextTime, Integer pageSize) {
        return this.objectConversion(jobInfoMapper.queryJobInfoByTriggerNextTime(DateUtil.formatDateTime(maxNextTime), pageSize));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateTriggerTimeById(Long id, Date triggerLastTime, Date triggerNextTime, Integer triggerStatus) {
        if (ObjectUtil.isNotNull(this.getById(id))) {
            jobInfoMapper.updateTriggerTimeById(id, DateUtil.formatDateTime(triggerLastTime), DateUtil.formatDateTime(triggerNextTime),
                    triggerStatus, DateUtil.formatDateTime(DateUtil.date()));
        }
    }

    @Override
    public Long findAllCount() {
        return jobInfoMapper.findAllCount();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void stopJob(Long id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        Assert.notNull(this.getById(id), ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        this.updateTriggerTimeById(id, DateUtil.date(1), DateUtil.date(1), NumberConstant.ZERO);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void startJob(Long id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        JobInfo jobInfo = this.getById(id);
        Assert.notNull(jobInfo, ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());

        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType());
        Assert.isFalse(ScheduleTypeEnum.NONE.equals(scheduleTypeEnum), ResponseEnum.THE_CURRENT_SCHEDULING_TYPE_CANNOT_BE_STARTED.getMessage());

        // next trigger time (5s后生效，避开预读周期)
        Date nextValidTime = CronUtils.generateNextValidTime(jobInfo.getScheduleType(), jobInfo.getScheduleConf(),
                new Date(System.currentTimeMillis() + ScheduleThread.PRE_READ_MS));
        Assert.notNull(nextValidTime, ResponseEnum.THE_CRON_EXPRESSION_FORMAT_IS_INCORRECT.getMessage());
        this.updateTriggerTimeById(id, DateUtil.date(1), nextValidTime, NumberConstant.ONE);
    }

    @Override
    public void triggerJob(TriggerJobDTO triggerJobDTO) {
        Assert.notNull(this.getById(triggerJobDTO.getJobInfoId()),
                ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        jobTriggerThreadPool.addTrigger(triggerJobDTO.getJobInfoId(), TriggerTypeEnum.MANUAL, NumberConstant.A_NEGATIVE,
                null, triggerJobDTO.getExecutorParam(), triggerJobDTO.getAddresses());
    }

    @Override
    public List<String> nextTriggerTime(Long id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        JobInfo jobInfo = this.getById(id);
        Assert.notNull(jobInfo, ResponseEnum.THE_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());

        List<String> result = new ArrayList<>();
        Date lastTime = DateUtil.date();
        for (int i = 0; i < 5; i++) {
            lastTime = CronUtils.generateNextValidTime(jobInfo.getScheduleType(), jobInfo.getScheduleConf(), lastTime);
            if (ObjectUtil.isNotNull(lastTime)) {
                result.add(DateUtil.formatDateTime(lastTime));
            }
        }
        return result;
    }

    @Override
    public List<String> cronLatestExecutionTime(String cron) {
        Assert.notBlank(cron, ResponseEnum.THE_CRON_EXPRESSION_CANNOT_BE_EMPTY.getMessage());
        cron = StrUtil.replaceIgnoreCase(cron, StrUtil.BACKSLASH, StrUtil.EMPTY);
        List<String> result = new ArrayList<>();
        Date lastTime = DateUtil.date();
        for (int i = 0; i < 5; i++) {
            lastTime = CronUtils.generateNextValidTime(ScheduleTypeEnum.CRON.name(), cron, lastTime);
            if (ObjectUtil.isNotNull(lastTime)) {
                result.add(DateUtil.formatDateTime(lastTime));
            }
        }
        return result;
    }

    /**
     * 获取子任务id
     *
     * @param childJobIds 子任务ID集合
     * @return {@link String}
     */
    private String getChildJobIds(List<Long> childJobIds) {
        if (CollectionUtil.isEmpty(childJobIds)) return null;
        for (Long childJobId : childJobIds) {
            Assert.notNull(this.getById(childJobId),
                    ResponseEnum.THE_CHILD_TASK_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        }
        return CollectionUtil.join(childJobIds, StrUtil.COMMA);
    }

    /**
     * 验证
     *
     * @param jobInfoDTO 任务信息DTO
     */
    private void validate(JobInfoDTO jobInfoDTO) {
        Assert.notNull(jobGroupService.queryById(jobInfoDTO.getGroupId()),
                ResponseEnum.THE_TASK_GROUP_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());

        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfoDTO.getScheduleType());
        if (ScheduleTypeEnum.CRON.equals(scheduleTypeEnum)) {
            if (StrUtil.isBlank(jobInfoDTO.getScheduleConf())
                    || !CronExpression.isValidExpression(jobInfoDTO.getScheduleConf())) {
                throw new XxlJobAdminException(ResponseEnum.THE_CRON_EXPRESSION_FORMAT_IS_INCORRECT);
            }
        } else if (ScheduleTypeEnum.FIX_RATE.equals(scheduleTypeEnum)) {
            Assert.notBlank(jobInfoDTO.getScheduleConf(), ResponseEnum.THE_SCHEDULING_CONFIGURATION_CANNOT_BE_EMPTY.getMessage());
            Long fixSecond = Convert.toLong(jobInfoDTO.getScheduleConf());
            Assert.isFalse(fixSecond < 1, ResponseEnum.THE_SCHEDULING_CONFIGURATION_CANNOT_BE_SMALLER_THAN_1.getMessage());
        }

        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(jobInfoDTO.getGlueType());
        if (GlueTypeEnum.BEAN.equals(glueTypeEnum) && StrUtil.isBlank(jobInfoDTO.getExecutorHandler())) {
            throw new XxlJobAdminException(ResponseEnum.TASK_HANDLER_CANNOT_BE_EMPTY);
        }
        // 》fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL.equals(glueTypeEnum) && StrUtil.isNotBlank(jobInfoDTO.getGlueSource())) {
            jobInfoDTO.setGlueSource(jobInfoDTO.getGlueSource().replaceAll("\r", ""));
        }
    }

}
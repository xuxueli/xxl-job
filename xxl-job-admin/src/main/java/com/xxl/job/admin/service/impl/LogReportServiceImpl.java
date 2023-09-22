package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.constants.QueryConstant;
import com.xxl.job.admin.common.pojo.dto.LogReportDTO;
import com.xxl.job.admin.common.pojo.entity.LogReport;
import com.xxl.job.admin.common.pojo.vo.ChartInfoVO;
import com.xxl.job.admin.common.pojo.vo.DashboardInfoVO;
import com.xxl.job.admin.common.pojo.vo.JobGroupVO;
import com.xxl.job.admin.common.pojo.vo.LogReportVO;
import com.xxl.job.admin.mapper.LogReportMapper;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.LogReportService;
import com.xxl.job.admin.service.UserInfoService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 任务日志报表 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Service
public class LogReportServiceImpl extends BaseServiceImpl<LogReportMapper, LogReport, LogReport, LogReportVO> implements LogReportService {

    @Autowired
    private LogReportMapper logReportMapper;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void syncLogReport(LogReportDTO logReportDTO) {
        LogReport logReport = logReportMapper.queryLogReportByDay(DateUtil.formatDate(logReportDTO.getTriggerDay()));
        if (ObjectUtil.isEmpty(logReport)) logReport = new LogReport();
        BeanUtil.copyProperties(logReportDTO, logReport, QueryConstant.ID);
        this.saveOrUpdate(logReport);
    }

    @Override
    public List<LogReportVO> queryLogReportByTriggerDay(Date from, Date to) {
        return this.objectConversion(logReportMapper.queryLogReportByTriggerDay(DateUtil.formatDate(from), DateUtil.formatDate(to)));
    }

    @Override
    public LogReportVO queryLogReportByDay(Date day) {
        return this.objectConversion(logReportMapper.queryLogReportByDay(DateUtil.formatDate(day)));
    }

    @Override
    public ChartInfoVO chartInfo(Date start, Date end) {
        start = DateUtil.beginOfDay(start);
        end = DateUtil.endOfDay(end);

        List<String> triggerDayList = new ArrayList<>();
        List<Long> triggerDayCountRunningList = new ArrayList<>();
        List<Long> triggerDayCountSucList = new ArrayList<>();
        List<Long> triggerDayCountFailList = new ArrayList<>();

        Long triggerCountRunningTotal = NumberConstant.ZERO.longValue();
        Long triggerCountSucTotal = NumberConstant.ZERO.longValue();
        Long triggerCountFailTotal = NumberConstant.ZERO.longValue();

        List<LogReport> logReports = logReportMapper.queryLogReportByTriggerDay(DateUtil.formatDate(start), DateUtil.formatDate(end));

        if (CollectionUtil.isNotEmpty(logReports)) {
            for (LogReport item : logReports) {
                String day = DateUtil.formatDate(item.getTriggerDay());
                Long triggerDayCountRunning = item.getRunningCount();
                Long triggerDayCountSuc = item.getSucCount();
                Long triggerDayCountFail = item.getFailCount();

                triggerDayList.add(day);
                triggerDayCountRunningList.add(triggerDayCountRunning);
                triggerDayCountSucList.add(triggerDayCountSuc);
                triggerDayCountFailList.add(triggerDayCountFail);

                triggerCountRunningTotal += triggerDayCountRunning;
                triggerCountSucTotal += triggerDayCountSuc;
                triggerCountFailTotal += triggerDayCountFail;
            }
        } else {
            List<DateTime> dateTimes = DateUtil.rangeToList(DateUtil.date(start), DateUtil.date(end), DateField.DAY_OF_MONTH);
            for (DateTime dateTime : dateTimes) {
                triggerDayList.add(DateUtil.formatDate(dateTime));
                triggerDayCountRunningList.add(NumberConstant.ZERO.longValue());
                triggerDayCountSucList.add(NumberConstant.ZERO.longValue());
                triggerDayCountFailList.add(NumberConstant.ZERO.longValue());
            }
        }

        ChartInfoVO chartInfoVO = new ChartInfoVO();
        chartInfoVO.setDays(triggerDayList);
        chartInfoVO.setRunCount(triggerDayCountRunningList);
        chartInfoVO.setSucCount(triggerDayCountSucList);
        chartInfoVO.setFailCount(triggerDayCountFailList);
        chartInfoVO.setRunTotal(triggerCountRunningTotal);
        chartInfoVO.setSucTotal(triggerCountSucTotal);
        chartInfoVO.setFailTotal(triggerCountFailTotal);

        return chartInfoVO;
    }

    @Override
    public DashboardInfoVO dashboardInfo() {

        Long jobInfoCount = jobInfoService.findAllCount();
        long jobLogCount = NumberConstant.ZERO.longValue();
        Long jobLogSuccessCount = NumberConstant.ZERO.longValue();
        LogReport logReport = logReportMapper.queryAll();
        if (ObjectUtil.isNotNull(logReport)) {
            jobLogCount = logReport.getRunningCount() + logReport.getSucCount() + logReport.getFailCount();
            jobLogSuccessCount = logReport.getSucCount();
        }

        // executor count
        Set<String> executorAddressSet = CollectionUtil.newHashSet();
        List<JobGroupVO> jobGroups = jobGroupService.findAll();
        if (CollectionUtil.isNotEmpty(jobGroups)) {
            jobGroups.forEach(a -> {
                if (StrUtil.isNotBlank(a.getAddresses())) {
                    executorAddressSet.addAll(StrUtil.split(a.getAddresses(), StrUtil.COMMA));
                }
            });
        }

        DashboardInfoVO dashboardInfoVO = new DashboardInfoVO();
        dashboardInfoVO.setJobInfoCount(jobInfoCount);
        dashboardInfoVO.setJobLogCount(jobLogCount);
        dashboardInfoVO.setJobLogSuccessCount(jobLogSuccessCount);
        dashboardInfoVO.setExecutorCount(executorAddressSet.size());
        dashboardInfoVO.setUserInfoCount(userInfoService.count());

        return dashboardInfoVO;
    }
}

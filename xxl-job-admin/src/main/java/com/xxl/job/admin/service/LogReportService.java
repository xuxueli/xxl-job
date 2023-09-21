package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.LogReportDTO;
import com.xxl.job.admin.common.pojo.entity.LogReport;
import com.xxl.job.admin.common.pojo.vo.ChartInfoVO;
import com.xxl.job.admin.common.pojo.vo.DashboardInfoVO;
import com.xxl.job.admin.common.pojo.vo.LogReportVO;
import com.xxl.job.admin.service.base.BaseService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务日志报表 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface LogReportService extends BaseService<LogReport, LogReport, LogReportVO> {

    /**
     * 同步日志报告
     *
     * @param logReportDTO 日志报告DTO
     */
    void syncLogReport(LogReportDTO logReportDTO);

    /**
     * 根据触发时间查询日志报告
     *
     * @param triggerDayFrom 触发开始时间, 天
     * @param triggerDayTo   触发结束时间, 天
     * @return {@link List}<{@link LogReportVO}>
     */
    List<LogReportVO> queryLogReportByTriggerDay(Date triggerDayFrom, Date triggerDayTo);

    /**
     * 查询日志报告根据一天
     *
     * @param day 一天
     * @return {@link LogReportVO}
     */
    LogReportVO queryLogReportByDay(Date day);

    /**
     * 仪表盘信息
     *
     * @return {@link DashboardInfoVO}
     */
    DashboardInfoVO dashboardInfo();

    /**
     * 图表信息
     *
     * @param start 开始日期 yyyy-MM-dd
     * @param end   结束日期 yyyy-MM-dd
     * @return {@link ChartInfoVO}
     */
    ChartInfoVO chartInfo(Date start, Date end);






}

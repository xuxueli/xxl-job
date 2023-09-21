package com.xxl.job.admin.controller;

import cn.hutool.core.date.DateUtil;
import com.xxl.job.admin.common.pojo.vo.ChartInfoVO;
import com.xxl.job.admin.common.pojo.vo.DashboardInfoVO;
import com.xxl.job.admin.service.LogReportService;
import com.xxl.job.core.pojo.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 任务日志报表 前端控制器
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Validated
@RestController
@Api(tags = "报表管理")
@RequestMapping("/log-report")
public class LogReportController extends AbstractController {

    @Autowired
    private LogReportService logReportService;

    @ApiOperation("查询调度报表")
    @GetMapping(value = "/trigger/{start}/{end}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "start", dataTypeClass = String.class, value = "开始时间", required = true),
            @ApiImplicitParam(paramType = "path", name = "end", dataTypeClass = String.class, value = "结束时间", required = true),
    })
    public ResponseVO<ChartInfoVO> chartInfo(@PathVariable("start") @NotBlank(message = "开始时间不能为空") String start,
                                             @PathVariable("end") @NotBlank(message = "结束时间不能为空") String end) {
        log.info("chartInfo {}, {}", start, end);
        return ResponseVO.success(logReportService.chartInfo(DateUtil.parseDate(start), DateUtil.parseDate(end)));
    }

    @ApiOperation("查询运行报表")
    @GetMapping(value = "/run", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<DashboardInfoVO> dashboardInfo() {
        log.info("dashboardInfo {}", System.currentTimeMillis());
        return ResponseVO.success(logReportService.dashboardInfo());
    }

















}

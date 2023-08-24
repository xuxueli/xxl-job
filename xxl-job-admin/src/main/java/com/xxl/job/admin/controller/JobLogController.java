package com.xxl.job.admin.controller;


import cn.hutool.extra.validation.ValidationUtil;
import com.xxl.job.admin.common.pojo.dto.JobLogCleanDTO;
import com.xxl.job.admin.common.pojo.dto.JobLogFilterDTO;
import com.xxl.job.admin.common.pojo.vo.JobLogVO;
import com.xxl.job.admin.common.pojo.vo.PageVO;
import com.xxl.job.admin.service.JobLogService;
import com.xxl.job.core.pojo.vo.LogResult;
import com.xxl.job.core.pojo.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 任务日志信息 前端控制器
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Validated
@RestController
@Api(tags = "日志管理")
@RequestMapping("/log")
public class JobLogController {

    @Autowired
    private JobLogService jobLogService;

    @ApiOperation("查询日志")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<PageVO<JobLogVO>> queryJobLog(@Validated JobLogFilterDTO filterDTO) {
        log.info("queryJobLog {}", filterDTO.toString());
        ValidationUtil.validate(filterDTO);
        return ResponseVO.success(jobLogService.page(filterDTO));
    }

    @ApiOperation("查询执行日志")
    @GetMapping(value = "/cat/{logId}/{fromLineNum}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "logId", dataTypeClass = Long.class, value = "日志ID", required = true),
            @ApiImplicitParam(paramType = "path", name = "fromLineNum", dataTypeClass = Integer.class, value = "开始行数（默认：1）", required = true),
    })
    public ResponseVO<LogResult> catJobLog(@PathVariable("logId") @NotNull(message = "日志ID不能为空") Long logId,
                                           @PathVariable("fromLineNum") @NotNull(message = "开始行数不能为空") Integer fromLineNum) {
        log.info("queryJobLogCat {}, {}", logId, fromLineNum);
        return ResponseVO.success(jobLogService.catJobLog(logId, fromLineNum));
    }

    @ApiOperation("删除日志任务")
    @DeleteMapping(value = "/task/{logId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "logId", dataTypeClass = Long.class, value = "日志ID", required = true),
    })
    public ResponseVO<Void> killJobLog(@PathVariable("logId") @NotNull(message = "日志ID不能为空") Long logId) {
        log.info("killJobLog {}", logId);
        jobLogService.killJobLog(logId);
        return ResponseVO.success();
    }

    @ApiOperation("清理日志")
    @DeleteMapping(value = "/clean", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> cleanJobLog(@RequestBody @Valid JobLogCleanDTO jobLogCleanDTO) {
        log.info("cleanJobLog {}", jobLogCleanDTO.toString());
        jobLogService.cleanJobLog(jobLogCleanDTO);
        return ResponseVO.success();
    }
















}

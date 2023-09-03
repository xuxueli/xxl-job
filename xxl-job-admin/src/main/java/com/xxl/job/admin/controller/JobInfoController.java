package com.xxl.job.admin.controller;

import cn.hutool.extra.validation.ValidationUtil;
import com.xxl.job.admin.common.pojo.dto.JobInfoDTO;
import com.xxl.job.admin.common.pojo.dto.JobInfoFilterDTO;
import com.xxl.job.admin.common.pojo.dto.TriggerJobDTO;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.common.pojo.vo.PageVO;
import com.xxl.job.admin.service.JobInfoService;
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
import java.util.List;

/**
 * <p>
 * 任务信息 前端控制器
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Validated
@RestController
@Api(tags = "任务管理")
@RequestMapping("/job")
public class JobInfoController extends AbstractController {

    @Autowired
    private JobInfoService jobInfoService;

    @ApiOperation("添加任务")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<JobInfoVO> saveJobInfo(@Validated @RequestBody JobInfoDTO jobInfoDTO) {
        log.info("saveJobInfo {}", jobInfoDTO.toString());
        jobInfoDTO.setCreatedUser(getAccount());
        return ResponseVO.success(jobInfoService.saveJobInfo(jobInfoDTO));
    }

    @ApiOperation("修改任务")
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<JobInfoVO> updateJobInfo(@Validated @RequestBody JobInfoDTO jobInfoDTO) {
        log.info("updateJobInfo {}", jobInfoDTO.toString());
        jobInfoDTO.setUpdatedUser(getAccount());
        return ResponseVO.success(jobInfoService.updateJobInfo(jobInfoDTO));
    }

    @ApiOperation("查询任务")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<PageVO<JobInfoVO>> queryJobInfo(@Validated JobInfoFilterDTO filterDTO) {
        log.info("queryJobInfo {}", filterDTO.toString());
        ValidationUtil.validate(filterDTO);
        return ResponseVO.success(jobInfoService.page(filterDTO));
    }

    @ApiOperation("删除任务")
    @DeleteMapping(value = "/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "jobId", dataTypeClass = Long.class, value = "任务id", required = true),
    })
    public ResponseVO<Void> deleteJobInfo(@PathVariable("jobId") @NotNull(message = "任务ID不能为空") Long jobId) {
        log.info("deleteJobInfo {}", jobId);
        jobInfoService.delete(jobId);
        return ResponseVO.success();
    }

    @ApiOperation("根据ID查询任务")
    @GetMapping(value = "/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "jobId", dataTypeClass = Long.class, value = "任务id", required = true),
    })
    public ResponseVO<JobInfoVO> findJobInfoById(@PathVariable("jobId") @NotNull(message = "任务ID不能为空") Long jobId) {
        log.info("findJobInfoById {}", jobId);
        return ResponseVO.success(jobInfoService.queryById(jobId));
    }

    @ApiOperation("停止任务")
    @PatchMapping(value = "/stop/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "jobId", dataTypeClass = Long.class, value = "任务id", required = true),
    })
    public ResponseVO<Void> stopJob(@PathVariable("jobId") @NotNull(message = "任务ID不能为空") Long jobId) {
        log.info("stopJob {}", jobId);
        jobInfoService.stopJob(jobId);
        return ResponseVO.success();
    }

    @ApiOperation("启动任务")
    @PatchMapping(value = "/start/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "jobId", dataTypeClass = Long.class, value = "任务id", required = true),
    })
    public ResponseVO<Void> startJob(@PathVariable("jobId") @NotNull(message = "任务ID不能为空") Long jobId) {
        log.info("startJob {}", jobId);
        jobInfoService.startJob(jobId);
        return ResponseVO.success();
    }

    @ApiOperation("手动执行一次任务")
    @PatchMapping(value = "/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> triggerJob(@RequestBody @Valid TriggerJobDTO triggerJobDTO) {
        log.info("triggerJob {}", triggerJobDTO.toString());
        jobInfoService.triggerJob(triggerJobDTO);
        return ResponseVO.success();
    }

    @ApiOperation("获取下次执行时间")
    @GetMapping(value = "/next-trigger/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "jobId", dataTypeClass = Long.class, value = "任务id", required = true),
    })
    public ResponseVO<List<String>> nextTriggerTime(@PathVariable("jobId") @NotNull(message = "任务ID不能为空") Long jobId) {
        log.info("nextTriggerTime {}", jobId);
        return ResponseVO.success(jobInfoService.nextTriggerTime(jobId));
    }

    @ApiOperation("批量删除任务")
    @DeleteMapping(value = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> deleteJobInfos(@RequestBody @Validated List<Long> ids) {
        log.info("deleteJobInfos {}", ids);
        jobInfoService.delete(ids);
        return ResponseVO.success();
    }









}

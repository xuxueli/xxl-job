package com.xxl.job.admin.controller;

import com.xxl.job.admin.common.pojo.dto.GlueLogDTO;
import com.xxl.job.admin.common.pojo.vo.GlueLogVO;
import com.xxl.job.admin.service.GlueLogService;
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

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * GLUE日志 前端控制器
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Validated
@RestController
@Api(tags = "GLUE日志管理")
@RequestMapping("/glue-log")
public class GlueLogController extends AbstractController {

    @Autowired
    private GlueLogService glueLogService;

    @ApiOperation("根据任务ID查询Glue日志")
    @GetMapping(value = "/job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "jobId", dataTypeClass = Long.class, value = "任务id", required = true),
    })
    public ResponseVO<List<GlueLogVO>> findGlueLogByJobId(@PathVariable("jobId") @NotNull(message = "任务ID不能为空") Long jobId) {
        log.info("findGlueLogByJobId {}", jobId);
        return ResponseVO.success(glueLogService.findGlueLogByJobId(jobId));
    }

    @ApiOperation("新增GLUE")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> saveGlueLog(@Validated @RequestBody GlueLogDTO glueLogDTO) {
        log.info("saveGlueLog {}", glueLogDTO.getDescription());
        glueLogDTO.setCreatedUser(getAccount());
        glueLogService.saveGlue(glueLogDTO);
        return ResponseVO.success();
    }















}

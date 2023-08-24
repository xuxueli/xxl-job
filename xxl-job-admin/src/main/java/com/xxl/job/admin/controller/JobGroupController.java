package com.xxl.job.admin.controller;

import cn.hutool.extra.validation.ValidationUtil;
import com.xxl.job.admin.common.pojo.dto.JobGroupDTO;
import com.xxl.job.admin.common.pojo.dto.JobGroupFilterDTO;
import com.xxl.job.admin.common.pojo.vo.JobGroupVO;
import com.xxl.job.admin.common.pojo.vo.PageVO;
import com.xxl.job.admin.service.JobGroupService;
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

/**
 * <p>
 * 任务组管理 前端控制器
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@RequestMapping("/group")
@Slf4j
@Validated
@RestController
@Api(tags = "任务组管理")
public class JobGroupController extends AbstractController {

    @Autowired
    private JobGroupService jobGroupService;

    @ApiOperation("添加任务组")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> saveJobGroup(@Validated @RequestBody JobGroupDTO jobGroupDTO) {
        log.info("saveJobGroup {}", jobGroupDTO.toString());
        jobGroupDTO.setCreatedUser(getAccount());
        jobGroupService.saveJobGroup(jobGroupDTO);
        return ResponseVO.success();
    }

    @ApiOperation("修改任务组")
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<Void> updateJobGroup(@Validated @RequestBody JobGroupDTO jobGroupDTO) {
        log.info("updateJobGroup {}", jobGroupDTO.toString());
        jobGroupDTO.setUpdatedUser(getAccount());
        jobGroupService.updateJobGroup(jobGroupDTO);
        return ResponseVO.success();
    }

    @ApiOperation("查询任务组")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseVO<PageVO<JobGroupVO>> queryJobGroup(@Validated JobGroupFilterDTO filterDTO) {
        log.info("queryJobGroup {}", filterDTO.toString());
        ValidationUtil.validate(filterDTO);
        return ResponseVO.success(jobGroupService.page(filterDTO));
    }

    @ApiOperation("删除任务组")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", dataTypeClass = Long.class, value = "任务组id", required = true),
    })
    public ResponseVO<Void> deleteJobGroup(@PathVariable("id") @NotNull(message = "任务组ID不能为空") Long id) {
        log.info("deleteJobGroup {}", id);
        jobGroupService.delete(id);
        return ResponseVO.success();
    }

    @ApiOperation("根据ID查询任务组")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", dataTypeClass = Long.class, value = "任务组id", required = true),
    })
    public ResponseVO<JobGroupVO> findJobGroupById(@PathVariable("id") @NotNull(message = "任务组ID不能为空") Long id) {
        log.info("findJobGroupById {}", id);
        return ResponseVO.success(jobGroupService.queryById(id));
    }












}

package com.xxl.job.writing.controller;

import com.xxl.job.writing.dto.TaskCreateDTO;
import com.xxl.job.writing.exception.BusinessException;
import com.xxl.job.writing.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务控制器
 * 实现任务相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
@Tag(name = "任务管理", description = "写作任务管理相关接口")
public class TaskController {

    /**
     * 发布任务
     * POST /api/task
     */
    @PostMapping
    @Operation(summary = "发布任务", description = "用户发布一个新的写作任务")
    public Result<Long> publishTask(@Validated @RequestBody TaskCreateDTO taskCreateDTO) {
        log.info("用户发布任务: {}", taskCreateDTO);
        throw BusinessException.notImplemented("Publish task");
    }

    /**
     * 获取任务报价列表
     * GET /api/task/{id}/quotes
     */
    @GetMapping("/{id}/quotes")
    @Operation(summary = "获取任务报价列表", description = "获取指定任务的所有专家报价")
    public Result<Object> getTaskQuotes(@PathVariable Long id) {
        log.info("获取任务报价列表，任务ID: {}", id);
        throw BusinessException.notImplemented("Get task quotes");
    }

    /**
     * 选择专家
     * POST /api/task/{id}/select-expert
     */
    @PostMapping("/{id}/select-expert")
    @Operation(summary = "选择专家", description = "用户从报价中选择一位专家接单")
    public Result<Void> selectExpert(@PathVariable Long id, @RequestParam Long quoteId) {
        log.info("用户选择专家，任务ID: {}, 报价ID: {}", id, quoteId);
        throw BusinessException.notImplemented("Select expert");
    }

    /**
     * 专家接单
     * POST /api/task/{id}/accept
     */
    @PostMapping("/{id}/accept")
    @Operation(summary = "专家接单", description = "专家主动接取任务（在用户未选定情况下）")
    public Result<Void> acceptTask(@PathVariable Long id, @RequestParam Long expertId) {
        log.info("专家接单，任务ID: {}, 专家ID: {}", id, expertId);
        throw BusinessException.notImplemented("Accept task");
    }

    /**
     * 获取任务详情
     * GET /api/task/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "获取指定任务的详细信息")
    public Result<Object> getTaskDetail(@PathVariable Long id) {
        log.info("获取任务详情，任务ID: {}", id);
        throw BusinessException.notImplemented("Get task detail");
    }

    /**
     * 取消任务
     * POST /api/task/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消任务", description = "取消指定任务")
    public Result<Void> cancelTask(@PathVariable Long id, @RequestParam String reason) {
        log.info("取消任务，任务ID: {}, 原因: {}", id, reason);
        throw BusinessException.notImplemented("Cancel task");
    }
}

package com.xxl.job.writing.controller;

import com.xxl.job.writing.dto.TaskCreateDTO;
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
        try {
            log.info("用户发布任务: {}", taskCreateDTO);

            // TODO: 实现发布任务逻辑
            // 1. 验证用户身份和权限
            // 2. 创建任务记录
            // 3. 初始化任务状态为"已发布"
            // 4. 返回任务ID

            Long taskId = 1L; // 示例ID
            return Result.success(taskId);

        } catch (Exception e) {
            log.error("发布任务失败", e);
            return Result.error("发布任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务报价列表
     * GET /api/task/{id}/quotes
     */
    @GetMapping("/{id}/quotes")
    @Operation(summary = "获取任务报价列表", description = "获取指定任务的所有专家报价")
    public Result<Object> getTaskQuotes(@PathVariable Long id) {
        try {
            log.info("获取任务报价列表，任务ID: {}", id);

            // TODO: 实现获取报价列表逻辑
            // 1. 验证任务存在性和用户权限
            // 2. 查询任务的报价列表
            // 3. 返回报价信息

            return Result.success();

        } catch (Exception e) {
            log.error("获取任务报价列表失败", e);
            return Result.error("获取任务报价列表失败: " + e.getMessage());
        }
    }

    /**
     * 选择专家
     * POST /api/task/{id}/select-expert
     */
    @PostMapping("/{id}/select-expert")
    @Operation(summary = "选择专家", description = "用户从报价中选择一位专家接单")
    public Result<Void> selectExpert(@PathVariable Long id, @RequestParam Long quoteId) {
        try {
            log.info("用户选择专家，任务ID: {}, 报价ID: {}", id, quoteId);

            // TODO: 实现选择专家逻辑
            // 1. 验证任务状态（必须为"已回复"状态）
            // 2. 验证报价存在且未被选择
            // 3. 使用分布式锁确保同一任务仅能被一位专家接单
            // 4. 更新任务状态为"已接单"
            // 5. 更新报价状态为"已选择"
            // 6. 创建订单记录
            // 7. 发送短信通知用户支付

            return Result.success();

        } catch (Exception e) {
            log.error("选择专家失败", e);
            return Result.error("选择专家失败: " + e.getMessage());
        }
    }

    /**
     * 专家接单
     * POST /api/task/{id}/accept
     */
    @PostMapping("/{id}/accept")
    @Operation(summary = "专家接单", description = "专家主动接取任务（在用户未选定情况下）")
    public Result<Void> acceptTask(@PathVariable Long id, @RequestParam Long expertId) {
        try {
            log.info("专家接单，任务ID: {}, 专家ID: {}", id, expertId);

            // TODO: 实现专家接单逻辑
            // 1. 验证专家身份和权限
            // 2. 验证任务状态（必须为"已发布"或"已回复"状态）
            // 3. 使用分布式锁确保同一任务仅能被一位专家接单
            // 4. 验证专家接单限制（是否超过最大接单数）
            // 5. 更新任务状态为"已接单"
            // 6. 创建订单记录
            // 7. 发送短信通知用户支付

            return Result.success();

        } catch (Exception e) {
            log.error("专家接单失败", e);
            return Result.error("专家接单失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务详情
     * GET /api/task/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "获取指定任务的详细信息")
    public Result<Object> getTaskDetail(@PathVariable Long id) {
        try {
            log.info("获取任务详情，任务ID: {}", id);

            // TODO: 实现获取任务详情逻辑
            // 1. 验证任务存在性
            // 2. 查询任务详细信息
            // 3. 根据用户角色返回不同的信息（用户只能看到自己的任务，专家可以看到公开任务）

            return Result.success();

        } catch (Exception e) {
            log.error("获取任务详情失败", e);
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

    /**
     * 取消任务
     * POST /api/task/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消任务", description = "取消指定任务")
    public Result<Void> cancelTask(@PathVariable Long id, @RequestParam String reason) {
        try {
            log.info("取消任务，任务ID: {}, 原因: {}", id, reason);

            // TODO: 实现取消任务逻辑
            // 1. 验证用户权限（只有任务发布者可以取消）
            // 2. 验证任务状态（只有特定状态可以取消）
            // 3. 更新任务状态为"已取消"
            // 4. 如果已接单，需要处理订单退款
            // 5. 记录取消原因

            return Result.success();

        } catch (Exception e) {
            log.error("取消任务失败", e);
            return Result.error("取消任务失败: " + e.getMessage());
        }
    }
}
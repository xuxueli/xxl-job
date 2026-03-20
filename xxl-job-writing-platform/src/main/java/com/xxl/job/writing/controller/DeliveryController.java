package com.xxl.job.writing.controller;

import com.xxl.job.writing.dto.DeliveryCreateDTO;
import com.xxl.job.writing.exception.BusinessException;
import com.xxl.job.writing.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 交付控制器
 * 实现交付成果相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/delivery")
@Tag(name = "交付管理", description = "成果交付和验收相关接口")
public class DeliveryController {

    /**
     * 提交交付成果
     * POST /api/delivery
     */
    @PostMapping
    @Operation(summary = "提交交付成果", description = "专家提交写作成果")
    public Result<Long> submitDelivery(@Validated @RequestBody DeliveryCreateDTO deliveryCreateDTO) {
        log.info("专家提交交付成果，订单ID: {}", deliveryCreateDTO.getOrderId());
        throw BusinessException.notImplemented("Submit delivery");
    }

    /**
     * 验收交付成果
     * POST /api/delivery/{id}/accept
     */
    @PostMapping("/{id}/accept")
    @Operation(summary = "验收交付成果", description = "用户验收专家提交的成果")
    public Result<Void> acceptDelivery(@PathVariable Long id,
                                       @RequestParam(required = false) Integer rating,
                                       @RequestParam(required = false) String comment) {
        log.info("用户验收交付成果，交付ID: {}, 评分: {}, 评价: {}", id, rating, comment);
        throw BusinessException.notImplemented("Accept delivery");
    }

    /**
     * 要求修改交付成果
     * POST /api/delivery/{id}/request-modify
     */
    @PostMapping("/{id}/request-modify")
    @Operation(summary = "要求修改交付成果", description = "用户要求专家修改交付的成果")
    public Result<Void> requestModify(@PathVariable Long id, @RequestParam String requirement) {
        log.info("用户要求修改交付成果，交付ID: {}, 修改要求: {}", id, requirement);
        throw BusinessException.notImplemented("Request delivery modification");
    }

    /**
     * 获取交付详情
     * GET /api/delivery/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取交付详情", description = "获取指定交付的详细信息")
    public Result<Object> getDeliveryDetail(@PathVariable Long id) {
        log.info("获取交付详情，交付ID: {}", id);
        throw BusinessException.notImplemented("Get delivery detail");
    }

    /**
     * 重新提交修改后的成果
     * POST /api/delivery/{id}/resubmit
     */
    @PostMapping("/{id}/resubmit")
    @Operation(summary = "重新提交修改后的成果", description = "专家提交修改后的成果")
    public Result<Void> resubmitDelivery(@PathVariable Long id, @Validated @RequestBody DeliveryCreateDTO deliveryCreateDTO) {
        log.info("专家重新提交修改后的成果，交付ID: {}, 订单ID: {}", id, deliveryCreateDTO.getOrderId());
        throw BusinessException.notImplemented("Resubmit delivery");
    }
}

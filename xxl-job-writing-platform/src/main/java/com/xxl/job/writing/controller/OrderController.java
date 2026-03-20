package com.xxl.job.writing.controller;

import com.xxl.job.writing.dto.PayOrderDTO;
import com.xxl.job.writing.exception.BusinessException;
import com.xxl.job.writing.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 实现订单相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理", description = "订单支付和管理相关接口")
public class OrderController {

    /**
     * 支付订单
     * POST /api/order/{id}/pay
     */
    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单", description = "用户支付指定订单")
    public Result<Void> payOrder(@PathVariable Long id, @Validated @RequestBody PayOrderDTO payOrderDTO) {
        log.info("用户支付订单，订单ID: {}, 支付方式: {}", id, payOrderDTO.getPayMethod());
        throw BusinessException.notImplemented("Pay order");
    }

    /**
     * 获取订单详情
     * GET /api/order/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "获取指定订单的详细信息")
    public Result<Object> getOrderDetail(@PathVariable Long id) {
        log.info("获取订单详情，订单ID: {}", id);
        throw BusinessException.notImplemented("Get order detail");
    }

    /**
     * 申请退款
     * POST /api/order/{id}/refund
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "申请退款", description = "用户申请订单退款")
    public Result<Void> applyRefund(@PathVariable Long id, @RequestParam String reason) {
        log.info("用户申请退款，订单ID: {}, 原因: {}", id, reason);
        throw BusinessException.notImplemented("Apply refund");
    }

    /**
     * 获取用户订单列表
     * GET /api/order/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户订单列表", description = "获取指定用户的所有订单")
    public Result<Object> getUserOrders(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("获取用户订单列表，用户ID: {}, 页码: {}, 页大小: {}", userId, page, size);
        throw BusinessException.notImplemented("Get user orders");
    }

    /**
     * 获取专家订单列表
     * GET /api/order/expert/{expertId}
     */
    @GetMapping("/expert/{expertId}")
    @Operation(summary = "获取专家订单列表", description = "获取指定专家的所有订单")
    public Result<Object> getExpertOrders(@PathVariable Long expertId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("获取专家订单列表，专家ID: {}, 页码: {}, 页大小: {}", expertId, page, size);
        throw BusinessException.notImplemented("Get expert orders");
    }
}

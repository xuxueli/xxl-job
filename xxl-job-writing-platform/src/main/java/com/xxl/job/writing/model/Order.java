package com.xxl.job.writing.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
public class Order {
    /**
     * 订单ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 接单专家ID
     */
    private Long expertId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单编号（唯一业务编号）
     */
    private String orderNo;

    /**
     * 订单状态：1-待支付，2-已支付，3-已取消
     */
    private Integer status;

    /**
     * 支付状态：0-未支付，1-支付成功，2-支付失败，3-退款中，4-已退款
     */
    private Integer payStatus;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付方式：1-微信支付，2-支付宝，3-银行卡
     */
    private Integer payMethod;

    /**
     * 支付流水号
     */
    private String payTransactionNo;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
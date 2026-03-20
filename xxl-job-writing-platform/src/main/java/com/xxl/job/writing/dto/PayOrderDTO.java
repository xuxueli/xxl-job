package com.xxl.job.writing.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 支付订单DTO
 */
@Data
public class PayOrderDTO {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 支付方式：1-微信支付，2-支付宝，3-银行卡
     */
    @NotNull(message = "支付方式不能为空")
    private Integer payMethod;

    /**
     * 支付渠道（微信/支付宝的具体支付方式）
     */
    private String payChannel;
}
package com.xxl.job.writing.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 支付订单DTO
 */
@Data
public class PayOrderDTO {
    /**
     * 支付方式：1-微信支付，2-支付宝，3-银行卡
     */
    @NotNull(message = "支付方式不能为空")
    @Min(value = 1, message = "支付方式最小值为1")
    @Max(value = 3, message = "支付方式最大值为3")
    private Integer payMethod;

    /**
     * 支付渠道（微信/支付宝的具体支付方式）
     */
    private String payChannel;
}

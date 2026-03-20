package com.xxl.job.writing.constant;

/**
 * 支付状态常量
 */
public class PayStatus {
    /**
     * 未支付
     */
    public static final int NOT_PAID = 0;

    /**
     * 支付成功
     */
    public static final int SUCCESS = 1;

    /**
     * 支付失败
     */
    public static final int FAILED = 2;

    /**
     * 退款中
     */
    public static final int REFUNDING = 3;

    /**
     * 已退款
     */
    public static final int REFUNDED = 4;
}
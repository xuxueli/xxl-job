package com.xxl.job.writing.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this(500, message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    // 常用业务异常
    public static final int TASK_NOT_FOUND = 1001;
    public static final int TASK_STATUS_INVALID = 1002;
    public static final int TASK_ALREADY_ACCEPTED = 1003;
    public static final int USER_PERMISSION_DENIED = 1004;
    public static final int EXPERT_NOT_FOUND = 1005;
    public static final int EXPERT_TASK_LIMIT_EXCEEDED = 1006;
    public static final int QUOTE_NOT_FOUND = 1007;
    public static final int QUOTE_ALREADY_EXISTS = 1008;
    public static final int ORDER_NOT_FOUND = 1009;
    public static final int ORDER_STATUS_INVALID = 1010;
    public static final int PAYMENT_FAILED = 1011;
    public static final int DELIVERY_NOT_FOUND = 1012;
    public static final int FILE_UPLOAD_FAILED = 1013;
    public static final int DISTRIBUTED_LOCK_FAILED = 1014;
    public static final int INVALID_REQUEST = 1400;
    public static final int NOT_IMPLEMENTED = 1501;

    // 快速创建业务异常的方法
    public static BusinessException taskNotFound() {
        return new BusinessException(TASK_NOT_FOUND, "任务不存在");
    }

    public static BusinessException taskStatusInvalid() {
        return new BusinessException(TASK_STATUS_INVALID, "任务状态无效");
    }

    public static BusinessException taskAlreadyAccepted() {
        return new BusinessException(TASK_ALREADY_ACCEPTED, "任务已被接单");
    }

    public static BusinessException userPermissionDenied() {
        return new BusinessException(USER_PERMISSION_DENIED, "用户权限不足");
    }

    public static BusinessException expertNotFound() {
        return new BusinessException(EXPERT_NOT_FOUND, "专家不存在");
    }

    public static BusinessException expertTaskLimitExceeded() {
        return new BusinessException(EXPERT_TASK_LIMIT_EXCEEDED, "专家接单数量超过限制");
    }

    public static BusinessException quoteNotFound() {
        return new BusinessException(QUOTE_NOT_FOUND, "报价不存在");
    }

    public static BusinessException quoteAlreadyExists() {
        return new BusinessException(QUOTE_ALREADY_EXISTS, "您已对该任务报价");
    }

    public static BusinessException orderNotFound() {
        return new BusinessException(ORDER_NOT_FOUND, "订单不存在");
    }

    public static BusinessException orderStatusInvalid() {
        return new BusinessException(ORDER_STATUS_INVALID, "订单状态无效");
    }

    public static BusinessException paymentFailed() {
        return new BusinessException(PAYMENT_FAILED, "支付失败");
    }

    public static BusinessException deliveryNotFound() {
        return new BusinessException(DELIVERY_NOT_FOUND, "交付记录不存在");
    }

    public static BusinessException fileUploadFailed() {
        return new BusinessException(FILE_UPLOAD_FAILED, "文件上传失败");
    }

    public static BusinessException distributedLockFailed() {
        return new BusinessException(DISTRIBUTED_LOCK_FAILED, "系统繁忙，请稍后重试");
    }

    public static BusinessException invalidRequest(String message) {
        return new BusinessException(INVALID_REQUEST, message);
    }

    public static BusinessException notImplemented(String operation) {
        return new BusinessException(NOT_IMPLEMENTED, operation + " is not implemented yet");
    }
}

package com.xxl.job.writing.constant;

/**
 * 任务状态常量
 */
public class TaskStatus {
    /**
     * 已发布（未有人接单）
     */
    public static final int PUBLISHED = 10;

    /**
     * 已回复（有报价未选定）
     */
    public static final int REPLIED = 20;

    /**
     * 已接单（已选定专家，待支付）
     */
    public static final int ACCEPTED = 30;

    /**
     * 已支付（用户已支付，专家可开始工作）
     */
    public static final int PAID = 40;

    /**
     * 进行中（专家正在写作）
     */
    public static final int IN_PROGRESS = 50;

    /**
     * 已交付（专家已提交成果）
     */
    public static final int DELIVERED = 60;

    /**
     * 已完成（用户验收通过）
     */
    public static final int COMPLETED = 70;

    /**
     * 已取消
     */
    public static final int CANCELLED = 80;
}
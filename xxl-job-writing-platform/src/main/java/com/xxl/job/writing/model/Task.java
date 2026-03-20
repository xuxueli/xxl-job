package com.xxl.job.writing.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
public class Task {
    /**
     * 任务ID
     */
    private Long id;

    /**
     * 发布用户ID
     */
    private Long userId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述/详细要求
     */
    private String description;

    /**
     * 任务类型：1-文章写作，2-文案创作，3-翻译，4-润色修改，5-其他
     */
    private Integer taskType;

    /**
     * 预算价格
     */
    private BigDecimal budget;

    /**
     * 实际成交价格
     */
    private BigDecimal actualPrice;

    /**
     * 任务状态：10-已发布，20-已回复，30-已接单，40-已支付，50-进行中，60-已交付，70-已完成，80-已取消
     */
    private Integer status;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 流程说明（可复用默认流程）
     */
    private String processDescription;

    /**
     * 预计完成字数
     */
    private Integer expectedWordCount;

    /**
     * 领域标签（JSON数组格式存储）
     */
    private String tags;

    /**
     * 附件文件路径（JSON数组格式存储）
     */
    private String attachments;

    /**
     * 接单专家ID（任务被接单后填充）
     */
    private Long expertId;

    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 交付时间
     */
    private LocalDateTime deliverTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

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
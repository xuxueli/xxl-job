package com.xxl.job.writing.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报价实体类
 */
@Data
public class Quote {
    /**
     * 报价ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 专家ID
     */
    private Long expertId;

    /**
     * 报价金额
     */
    private BigDecimal amount;

    /**
     * 报价备注（专家优势、经验等）
     */
    private String remark;

    /**
     * 预计完成天数
     */
    private Integer estimatedDays;

    /**
     * 报价状态：0-已报价，1-已选择，2-已拒绝，3-已过期
     */
    private Integer status;

    /**
     * 是否被用户选择
     */
    private Boolean selected;

    /**
     * 选择时间
     */
    private LocalDateTime selectTime;

    /**
     * 报价时间
     */
    private LocalDateTime quoteTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
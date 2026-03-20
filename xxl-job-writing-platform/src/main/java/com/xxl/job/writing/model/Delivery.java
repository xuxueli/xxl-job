package com.xxl.job.writing.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 交付实体类
 */
@Data
public class Delivery {
    /**
     * 交付ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 专家ID
     */
    private Long expertId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件路径（JSON数组格式存储多个文件）
     */
    private String filePaths;

    /**
     * 注意事项
     */
    private String notes;

    /**
     * 使用说明
     */
    private String instructions;

    /**
     * 交付状态：0-已交付，1-已验收通过，2-需要修改，3-已拒绝
     */
    private Integer status;

    /**
     * 用户评分（1-5分）
     */
    private Integer userRating;

    /**
     * 用户评价
     */
    private String userComment;

    /**
     * 交付时间
     */
    private LocalDateTime deliverTime;

    /**
     * 验收时间
     */
    private LocalDateTime acceptTime;

    /**
     * 修改要求
     */
    private String modifyRequirement;

    /**
     * 修改次数
     */
    private Integer modifyCount;

    /**
     * 最终交付时间
     */
    private LocalDateTime finalDeliverTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
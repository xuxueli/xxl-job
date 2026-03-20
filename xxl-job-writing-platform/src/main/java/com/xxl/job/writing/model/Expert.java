package com.xxl.job.writing.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 专家实体类
 */
@Data
public class Expert {
    /**
     * 专家ID（与用户ID关联）
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 资质证书信息（JSON格式存储）
     */
    private String qualifications;

    /**
     * 擅长领域（JSON数组格式存储）
     */
    private String expertFields;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 历史评分（平均分）
     */
    private BigDecimal averageScore;

    /**
     * 总完成任务数
     */
    private Integer completedTaskCount;

    /**
     * 当前进行中任务数
     */
    private Integer ongoingTaskCount;

    /**
     * 接单限制（最大同时接单数）
     */
    private Integer taskLimit;

    /**
     * 专家等级：1-初级，2-中级，3-高级
     */
    private Integer level;

    /**
     * 认证状态：0-未认证，1-已认证，2-认证中，3-认证失败
     */
    private Integer certificationStatus;

    /**
     * 认证时间
     */
    private LocalDateTime certificationTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
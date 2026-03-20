package com.xxl.job.writing.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务创建DTO
 */
@Data
public class TaskCreateDTO {
    /**
     * 任务标题
     */
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 100, message = "任务标题长度不能超过100个字符")
    private String title;

    /**
     * 任务描述/详细要求
     */
    @NotBlank(message = "任务描述不能为空")
    @Size(max = 2000, message = "任务描述长度不能超过2000个字符")
    private String description;

    /**
     * 任务类型：1-文章写作，2-文案创作，3-翻译，4-润色修改，5-其他
     */
    @NotNull(message = "任务类型不能为空")
    @Min(value = 1, message = "任务类型最小值为1")
    @Max(value = 5, message = "任务类型最大值为5")
    private Integer taskType;

    /**
     * 预算价格
     */
    @NotNull(message = "预算价格不能为空")
    @DecimalMin(value = "0.01", message = "预算价格必须大于0")
    private BigDecimal budget;

    /**
     * 截止时间
     */
    @NotNull(message = "截止时间不能为空")
    @Future(message = "截止时间必须是将来的时间")
    private LocalDateTime deadline;

    /**
     * 预计完成字数
     */
    @Min(value = 100, message = "预计完成字数不能少于100字")
    private Integer expectedWordCount;

    /**
     * 领域标签
     */
    private List<String> tags;

    /**
     * 流程说明（可复用默认流程）
     */
    @Size(max = 1000, message = "流程说明长度不能超过1000个字符")
    private String processDescription;
}
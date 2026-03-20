package com.xxl.job.writing.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 报价创建DTO
 */
@Data
public class QuoteCreateDTO {
    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * 报价金额
     */
    @NotNull(message = "报价金额不能为空")
    @DecimalMin(value = "0.01", message = "报价金额必须大于0")
    private BigDecimal amount;

    /**
     * 报价备注（专家优势、经验等）
     */
    @Size(max = 500, message = "报价备注长度不能超过500个字符")
    private String remark;

    /**
     * 预计完成天数
     */
    @NotNull(message = "预计完成天数不能为空")
    @Min(value = 1, message = "预计完成天数不能少于1天")
    @Max(value = 365, message = "预计完成天数不能超过365天")
    private Integer estimatedDays;
}
package com.xxl.job.writing.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 交付创建DTO
 */
@Data
public class DeliveryCreateDTO {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 文件路径列表
     */
    @NotNull(message = "文件不能为空")
    private List<String> filePaths;

    /**
     * 注意事项
     */
    @NotBlank(message = "注意事项不能为空")
    private String notes;

    /**
     * 使用说明
     */
    @NotBlank(message = "使用说明不能为空")
    private String instructions;
}
package com.xxl.job.admin.common.pojo.dto;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 数据源驱动 DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-12
 */
@ApiModel("数据源驱动 参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceDriverDTO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @NotBlank(message = "类型 不能为空")
    @ApiModelProperty(value = "类型", required = true)
    private String type;

    /**
     * 是否是DB类型(0:否,1:是), 默认：0
     */
    @NotNull(message = "是否是DB类型 不能为空")
    @ApiModelProperty(value = "是否是DB类型(0:否,1:是), 默认：0", required = true)
    private Integer db;

    /**
     * 驱动
     */
    @ApiModelProperty(value = "驱动类(db:1,必填)")
    private String driverClass;


}

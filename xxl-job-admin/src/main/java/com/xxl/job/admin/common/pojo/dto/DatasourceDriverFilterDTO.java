package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 数据源驱动过滤查询参数DTO
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("数据源驱动过滤查询参数")
public class DatasourceDriverFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String type;


    /**
     * 是否是DB类型(0:否,1:是), 默认：0
     */
    @ApiModelProperty(value = "是否是DB类型(0:否,1:是), 默认：0")
    private Integer db;


}

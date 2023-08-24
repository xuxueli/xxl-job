package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 数据源驱动
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-12
 */
@ApiModel("数据源驱动信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class DatasourceDriverVO extends Base implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 是否是DB类型(0:否,1:是), 默认：0
     */
    @ApiModelProperty(value = "是否是DB类型(0:否,1:是), 默认：0")
    private Integer db;

    /**
     * 驱动
     */
    @ApiModelProperty("驱动类")
    private String driverClass;


}

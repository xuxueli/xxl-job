package com.xxl.job.admin.common.pojo.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共属性父类
 *
 * @author Rong.Jia
 * @date 2023/01/31
 */
@Data
public class Base implements Serializable {

    private static final long serialVersionUID = -7519418012137093264L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    protected Long id;

    /**
     * 添加人
     */
    @ApiModelProperty("添加人")
    protected String createdUser;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    protected String updatedUser;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    protected String description;

    /**
     * 添加时间
     */
    @ApiModelProperty("添加时间")
    protected Long createdTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    protected Long updatedTime;


}

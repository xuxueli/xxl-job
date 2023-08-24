package com.xxl.job.admin.common.pojo.dto;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  分页查询数据传输对象
 * @date 2019/02/19 18:00:22
 * @author Rong.Jia
 */
@Data
@ApiModel("分页查询数据参数对象")
public class PageDTO implements Serializable {

    private static final long serialVersionUID = 4512708627615719846L;

    /**
     * 每页数据数量
     */
    @ApiModelProperty(value = "每页数据数量 (默认20)")
    @Min(value = 0,message = "参数必须大于0")
    private Integer pageSize;

    /**
     * 页码(开始页:1, 默认1，-1：所有)
     */
    @ApiModelProperty(value = "页码(开始页:0, 默认 0，-1：所有)",required = true)
    @NotNull(message = "参数必传，传-1代表不分页")
    @Min(value = -1,message = "参数必须大于-1")
    private Integer currentPage;

    /**
     * 排序类型（asc,desc）
     */
    @ApiModelProperty("排序类型（asc,desc） 默认 desc")
    private String orderType;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private String orderField;

    /**
     * 开始时间
     */
    @ApiModelProperty(" 开始时间")
    @Min(value = 0,message = "开始时间必须大于0")
    private Long startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(" 结束时间")
    @Min(value = 0,message = "结束时间必须大于0")
    private Long endTime;

    public Integer getCurrentPage() {
        return ObjectUtil.isNull(currentPage) ? 0 : currentPage;
    }
}

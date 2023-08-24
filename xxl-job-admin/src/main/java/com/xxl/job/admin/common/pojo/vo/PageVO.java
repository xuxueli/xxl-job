package com.xxl.job.admin.common.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询表现层对象
 *
 * @author Rong.Jia
 * @date 2021/12/20
 */
@Data
@ApiModel("分页查询表现层对象")
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = -3826579718601386597L;

    /**
     * 总条数
     */
    @ApiModelProperty("总条数")
    private Number total;

    /**
     * 每页显示条数
     */
    @ApiModelProperty("每页显示条数")
    private Number pageSize;

    /**
     * 总页数
     */
    @ApiModelProperty("总页数")
    private Number totalPages;

    /**
     * 页码
     */
    @ApiModelProperty("页码")
    private Number currentPage;

    /**
     * 查询数据列表
     */
    @ApiModelProperty("查询数据列表")
    private List<T> records = Collections.emptyList();

    /**
     * 当前页是否为第一页
     */
    @ApiModelProperty("当前页是否为第一页")
    private Boolean isFirst;

    /**
     * 当前页是否为最后一页
     */
    @ApiModelProperty("当前页是否为最后一页")
    private Boolean isLast;

    /**
     * 如果有下一页
     */
    @ApiModelProperty("如果有下一页")
    private Boolean hasNext;

    /**
     * 如果有上一页
     */
    @ApiModelProperty("如果有上一页")
    private Boolean hasPrevious;


}

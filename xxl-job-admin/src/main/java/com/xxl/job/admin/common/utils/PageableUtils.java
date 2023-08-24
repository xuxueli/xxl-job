package com.xxl.job.admin.common.utils;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.xxl.job.admin.common.pojo.dto.PageDTO;
import com.xxl.job.admin.common.pojo.dto.SortDTO;

import java.util.StringJoiner;

/**
 *  分页封装类
 * @author Rong.Jia
 * @date 2018/6/20 10:58
 */
public class PageableUtils {

    /**
     * 获取基础分页对象
     * @param pageDTO 分页对象
     */
    public static void basicPage(PageDTO pageDTO) {

        basicPage(pageDTO.getCurrentPage(), pageDTO.getPageSize(), pageDTO.getOrderType(), pageDTO.getOrderField());
    }

    /**
     * 获取基础分页对象
     * @param currentPage 获取第几页
     * @param pageSize 每页条数
     * @param orderField 排序字段
     * @param orderType 排序类型
     */
    public static void basicPage(Integer currentPage, Integer pageSize, String orderType, String orderField) {

        basicPage(currentPage, pageSize, new SortDTO(orderType, orderField));
    }

    /**
     * 获取基础分页对象
     * @param currentPage 获取第几页
     * @param pageSize 每页条数
     */
    public static void basicPage(Integer currentPage, Integer pageSize) {

        basicPage(currentPage, pageSize, SortDTO.DEFAULT_ORDER_TYPE, SortDTO.DEFAULT_ORDER_FIELD);
    }

    /**
     * 获取基础分页对象
     * @param page 获取第几页
     * @param size 每页条数
     * @param dto 排序对象
     */
    public static void basicPage(Integer page, Integer size, SortDTO dto) {

        page = (page == null || page < 0) ? 0 : page;

        size = (size == null || size <= 0) ? 20 : size;

        PageHelper.startPage(page, size, basicSort(dto.getOrderType(), dto.getOrderField()));
    }

    /**
     * 获取基础分页对象，每页条数默认20条
     *  - 默认以id降序排序
     * @param page 获取第几页
     */
    public static void basicPage(Integer page) {

        basicPage(page, 0, new SortDTO(SortDTO.DEFAULT_ORDER_TYPE, SortDTO.DEFAULT_ORDER_FIELD));
    }

    /**
     * 获取基础分页对象，每页条数默认20条
     * @param page 获取第几页
     * @param dto 排序对象
     */
    public static void basicPage(Integer page, SortDTO dto) {
        basicPage(page, 0, dto);
    }

    /**
     * 获取基础分页对象，排序方式默认降序
     * @param page 获取第几页
     * @param size 每页条数
     * @param orderField 排序字段
     */
    public static void basicPage(Integer page, Integer size, String orderField) {
        basicPage(page, size, new SortDTO(SortDTO.DEFAULT_ORDER_TYPE, orderField));
    }

    /**
     * 获取基础分页对象
     *  - 每页条数默认20条
     *  - 排序方式默认降序
     * @param page 获取第几页
     * @param orderField 排序字段
     */
    public static void basicPage(Integer page, String orderField) {
        basicPage(page, 0, new SortDTO(SortDTO.DEFAULT_ORDER_TYPE, orderField));
    }

    public static String basicSort() {
        return basicSort(SortDTO.DEFAULT_ORDER_TYPE, SortDTO.DEFAULT_ORDER_FIELD);
    }

    public static String basicSort(String orderType, String orderField) {

        if (StrUtil.isBlank(orderType)) {
            orderType = SortDTO.DEFAULT_ORDER_TYPE;
        }

        if (StrUtil.isBlank(orderField)) {
            orderField =  SortDTO.DEFAULT_ORDER_FIELD;
        }

        StringJoiner stringJoiner = new StringJoiner(StrUtil.SPACE);
        stringJoiner.add(orderField).add(orderType);

        return stringJoiner.toString();
    }

}

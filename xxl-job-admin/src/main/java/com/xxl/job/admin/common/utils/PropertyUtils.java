package com.xxl.job.admin.common.utils;

import com.github.pagehelper.PageInfo;
import com.xxl.job.admin.common.pojo.vo.PageVO;

import java.util.List;

/**
 * 属性工具类
 * @author Rong.Jia
 * @date 2020/01/14 09:22
 */
public class PropertyUtils {

    /**
     * 复制属性
     *
     * @param page    分页查询结果对象
     * @param records VO对象集合
     * @return {@link PageVO}<{@link T}>
     */
    public static <T> PageVO<T> copyProperties(PageInfo<?> page, List<T> records) {
        PageVO<T> pageVO = new PageVO<>();
        copyProperties(page, pageVO, records);
        return pageVO;
    }

    /**
     * 复制属性
     *
     * @param page    分页查询结果对象
     * @param pageVO  分页查询结果vo对象
     * @param records VO对象集合
     */
    public static <T> void copyProperties(PageInfo<?> page, PageVO<T> pageVO, List<T> records) {

        pageVO.setTotalPages(page.getPages());
        pageVO.setHasNext(page.isHasNextPage());
        pageVO.setHasPrevious(page.isHasPreviousPage());
        pageVO.setIsFirst(page.isIsFirstPage());
        pageVO.setIsLast(page.isIsLastPage());
        pageVO.setTotal((int)page.getTotal());
        pageVO.setCurrentPage(page.getPageNum());
        pageVO.setPageSize(page.getSize());
        pageVO.setRecords(records);

    }

    /**
     * 复制属性
     *
     * @param page   分页查询结果对象
     * @param pageVO 分页查询结果vo对象
     */
    public static <T> void copyProperties(PageInfo<?> page, PageVO<T> pageVO) {
        copyProperties(page, pageVO, pageVO.getRecords());
    }





}

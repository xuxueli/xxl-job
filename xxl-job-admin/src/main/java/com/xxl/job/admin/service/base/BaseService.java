package com.xxl.job.admin.service.base;

import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.common.pojo.dto.PageDTO;
import com.xxl.job.admin.common.pojo.vo.PageVO;

import java.io.Serializable;
import java.util.List;

/**
 * 业务层 （父级）接口
 *
 * @param <T>  目标对象
 * @param <S>  源对象
 * @param <E>  实体类
 * @author Rong.Jia
 * @date 2020/05/21 18:58
 */
public interface BaseService<E, S, T> extends IService<E> {

    /**
     * 分页查询
     *
     * @param pageDTO 页面DTO
     * @return {@link PageVO}<{@link T}>
     */
    PageVO<T> page(PageDTO pageDTO);

    /**
     * 查询列表
     *
     * @param pageDTO 页面DTO
     * @return {@link List}<{@link S}>
     */
    List<S> queryList(PageDTO pageDTO);

    /**
     * 删除一条信息
     *
     * @param id id
     * @return {@link Boolean}
     */
    Boolean delete(Serializable id);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return {@link T}
     */
    T queryById(Serializable id);

    /**
     *  对象转换
     * @param s  源对象
     * @return 目标对象
     */
    T objectConversion(S s);

    /**
     *  对象转换
     * @param sList  源对象集合
     * @return 目标对象集合
     */
    List<T> objectConversion(List<S> sList);

    /**
     *  对象转换
     * @param s  源对象
     * @param ignoreProperties 不拷贝的的属性列表
     * @return 目标对象
     */
    T objectConversion(S s, String... ignoreProperties);

    /**
     *  对象转换
     * @param sList  源对象集合
     * @param ignoreProperties 不拷贝的的属性列表
     * @return 目标对象集合
     */
    List<T> objectConversion(List<S> sList, String... ignoreProperties);

    /**
     * 对象转换
     *
     * @param sList       源对象集合
     * @param copyOptions 复制选项
     * @return {@link List}<{@link T}> 目标对象集合
     */
    List<T> objectConversion(List<S> sList, CopyOptions copyOptions);

    /**
     * 对象转换
     *
     * @param copyOptions 复制选项
     * @param s           源对象
     * @return {@link T} 目标对象
     */
    T objectConversion(S s, CopyOptions copyOptions);




}

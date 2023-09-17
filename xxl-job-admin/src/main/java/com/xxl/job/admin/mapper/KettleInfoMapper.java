package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.bo.KettleMaxVersionBO;
import com.xxl.job.admin.common.pojo.entity.KettleInfo;
import com.xxl.job.admin.common.pojo.query.KettleInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * kettle信息 Mapper 接口
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
public interface KettleInfoMapper extends BaseMapper<KettleInfo> {

    /**
     * 根据名称，版本查询kettle信息
     *
     * @param name 名称
     * @param version 版本
     * @return {@link KettleInfo}
     */
    KettleInfo findKettleByNameAndVersion(@Param("name") String name, @Param("version") String version);

    /**
     * 根据名称查询最大的版本号
     *
     * @param name 名称
     * @return {@link KettleMaxVersionBO}
     */
    KettleMaxVersionBO findMaxVersionByName(@Param("name") String name);

    /**
     * 查询kettle
     *
     * @param query 查询对象
     * @return {@link List}<{@link KettleInfo}>
     */
    List<KettleInfo> findKettle(KettleInfoQuery query);

    /**
     * 根据编码查询kettle
     *
     * @param code 编码
     * @return {@link KettleInfo}
     */
    KettleInfo findKettleByCode(@Param("code") String code);

    /**
     * 根据系列查询kettle
     *
     * @param series 系列
     * @return {@link List}<{@link KettleInfo}>
     */
    List<KettleInfo> findKettleBySeries(@Param("series") String series);


}

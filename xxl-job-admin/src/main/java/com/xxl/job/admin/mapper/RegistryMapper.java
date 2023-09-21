package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.entity.Registry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 任务注册信息 Mapper 接口
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface RegistryMapper extends BaseMapper<Registry> {

    /**
     * 查询死注册表根据更新时间
     *
     * @param nowTime 现在时间
     * @return {@link List}<{@link Long}> 注册主键
     */
    List<Long> findDeadRegistryByUpdatedTime(@Param("nowTime") Long nowTime);

    /**
     * 删除死注册表根据id
     *
     * @param ids 主键集合
     */
    void deleteDeadRegistryByIds(@Param("ids") List<Long> ids);

    /**
     * 查询所有
     *
     * @param nowTime 现在时间
     * @return {@link List}<{@link Registry}>
     */
    List<Registry> findAll(@Param("nowTime") Long nowTime);

    /**
     * 注册表更新
     *
     * @param registryGroup 注册组
     * @param registryKey   注册表键
     * @param registryValue 注册表值
     * @param updateTime    更新时间
     */
    void updateRegistry(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue,
                              @Param("updateTime") Long updateTime);

    /**
     * 注册表删除
     *
     * @param registryGroup 注册组
     * @param registryKey   注册表键
     * @param registryValue 注册表值
     */
    void deleteRegistry(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue);

    /**
     * 注册查询
     *
     * @param registryGroup 注册组
     * @param registryKey   注册表键
     * @param registryValue 注册表值
     * @return {@link Registry}
     */
    Registry findRegistry(@Param("registryGroup") String registryGroup,
                        @Param("registryKey") String registryKey,
                        @Param("registryValue") String registryValue);

    /**
     * 插入新
     *
     * @param registry 注册信息
     */
    @Override
    int insert(Registry registry);


}

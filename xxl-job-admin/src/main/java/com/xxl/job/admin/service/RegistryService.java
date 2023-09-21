package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.common.pojo.entity.Registry;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务注册信息 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface RegistryService extends IService<Registry> {

    /**
     * 查询死注册表根据更新时间
     *
     * @param nowTime 现在时间
     * @return {@link List}<{@link Long}>
     */
    List<Long> findDeadRegistryByUpdatedTime(Date nowTime);

    /**
     * 删除死注册表根据id
     *
     * @param ids 主键集合
     */
    void deleteDeadRegistryByIds(List<Long> ids);

    /**
     * 查询所有
     *
     * @param nowTime 现在时间
     * @return {@link List}<{@link Registry}>
     */
    List<Registry> findAll(Date nowTime);

    /**
     * 注册表更新
     *
     * @param registryGroup 注册组
     * @param registryKey   注册表键
     * @param registryValue 注册表值
     * @param updateTime    更新时间
     */
    void updateRegistry(String registryGroup, String registryKey, String registryValue, Date updateTime);

    /**
     * 注册表删除
     *
     * @param registryGroup 注册组
     * @param registryKey   注册表键
     * @param registryValue 注册表值
     */
    void deleteRegistry(String registryGroup, String registryKey, String registryValue);

    /**
     * 同步注册
     *
     * @param registryGroup 注册组
     * @param registryKey   注册键
     * @param registryValue 注册值
     */
    void syncRegistry(String registryGroup, String registryKey, String registryValue);



}

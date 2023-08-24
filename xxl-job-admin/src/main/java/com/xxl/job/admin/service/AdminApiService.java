package com.xxl.job.admin.service;

import com.xxl.job.core.pojo.dto.HandleCallbackParam;
import com.xxl.job.core.pojo.dto.RegistryParam;

import java.util.List;

/**
 * 管理api服务
 *
 * @author Rong.Jia
 * @date 2023/05/15
 */
public interface AdminApiService {

    /**
     * 回调
     *
     * @param callbackParamList 回调参数列表
     */
    void callback(List<HandleCallbackParam> callbackParamList);

    /**
     * 注册
     *
     * @param registryParam 注册参数
     */
    void registry(RegistryParam registryParam);

    /**
     * 解除注册
     * @param registryParam 注册参数
     */
    void unRegistry(RegistryParam registryParam);







}

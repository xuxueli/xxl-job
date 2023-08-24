package com.xxl.job.executor.service;

import com.xxl.job.core.pojo.dto.HandleCallbackParam;

import java.util.List;

/**
 * 管理客户端
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
public interface AdminClient {

    /**
     * 回调
     *
     * @param callbackParamList 回调参数列表
     */
    void callback(List<HandleCallbackParam> callbackParamList);

    /**
     * 回调
     *
     * @param address           调用地址
     * @param callbackParamList 回调参数列表
     */
    void callback(String address, List<HandleCallbackParam> callbackParamList);

    /**
     * 注册
     *
     */
    void registry();

    /**
     * 解除注册
     *
     */
    void unRegistry();


}

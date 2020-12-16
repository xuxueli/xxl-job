package com.xxl.job.core.biz;

import java.util.Set;

/**
 * admin 地址处理器
 *
 * @author L.cm
 */
public interface AdminAddressesResolver {

    /**
     * 地址处理
     *
     * @param adminAddresses admin 端地址
     * @return 地址列表
     */
    Set<String> resolver(String adminAddresses);

}

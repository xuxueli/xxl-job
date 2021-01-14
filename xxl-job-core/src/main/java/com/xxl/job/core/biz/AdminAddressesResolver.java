package com.xxl.job.core.biz;

import net.dreamlu.mica.core.utils.CharPool;
import net.dreamlu.mica.core.utils.StringPool;

import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 修复 admin 地址
     *
     * @param adminList admin 地址列表
     * @return 地址列表
     */
    static Set<String> fixUrl(Set<String> adminList) {
        return adminList.stream()
                .map(url -> url.endsWith(StringPool.SLASH) ? url : url + CharPool.SLASH)
                .collect(Collectors.toSet());
    }

}

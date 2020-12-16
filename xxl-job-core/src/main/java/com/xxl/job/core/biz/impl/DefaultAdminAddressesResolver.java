package com.xxl.job.core.biz.impl;

import com.xxl.job.core.biz.AdminAddressesResolver;
import net.dreamlu.mica.core.utils.StringUtil;

import java.util.Set;

/**
 * 默认的地址处理器
 *
 * @author L.cm
 */
public class DefaultAdminAddressesResolver implements AdminAddressesResolver {
    private final Set<String> adminList;

    public DefaultAdminAddressesResolver(String adminAddresses) {
        this.adminList = StringUtil.commaDelimitedListToSet(adminAddresses);
    }

    @Override
    public Set<String> resolver(String addresses) {
        return adminList;
    }
}

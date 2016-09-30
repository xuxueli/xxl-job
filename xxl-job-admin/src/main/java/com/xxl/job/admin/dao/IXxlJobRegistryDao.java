package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface IXxlJobRegistryDao {
    List<XxlJobRegistry> findRegistrys(String registryGroup, String registryKey);
}

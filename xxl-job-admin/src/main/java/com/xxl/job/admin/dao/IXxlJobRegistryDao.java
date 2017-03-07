package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Mapper
public interface IXxlJobRegistryDao {
    public int removeDead(int timeout);

    public List<XxlJobRegistry> findAll(int timeout);

    public int registryUpdate(String registryGroup, String registryKey, String registryValue);

    public int registrySave(String registryGroup, String registryKey, String registryValue);

}

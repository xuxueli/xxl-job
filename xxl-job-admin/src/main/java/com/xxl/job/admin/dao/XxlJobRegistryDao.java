package com.xxl.job.admin.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.xxl.job.admin.core.model.XxlJobRegistry;

/**
 * Created by xuxueli on 16/9/30.
 */
@Mapper
public interface XxlJobRegistryDao {

    public int removeDead(@Param("time") Date time);

    public List<XxlJobRegistry> findAll(@Param("time") Date time);

    public int registryUpdate(@Param("registryGroup") String registryGroup, @Param("registryKey") String registryKey,
            @Param("registryValue") String registryValue);

    public int registrySave(@Param("registryGroup") String registryGroup, @Param("registryKey") String registryKey,
            @Param("registryValue") String registryValue);

    public int registryDelete(@Param("registryGroup") String registGroup, @Param("registryKey") String registryKey,
            @Param("registryValue") String registryValue);

}

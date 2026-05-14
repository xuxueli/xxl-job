package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.model.XxlJobRegistry;

import java.util.Date;
import java.util.List;

public interface JobRegistryService extends IService<XxlJobRegistry> {

    List<Integer> findDead(int timeout, Date nowTime);

    int removeDead(List<Integer> ids);

    List<XxlJobRegistry> findAll(int timeout, Date nowTime);

    int registrySaveOrUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime);

    int registryDelete(String registryGroup, String registryKey, String registryValue);

    int removeByRegistryGroupAndKey(String registryGroup, String registryKey);
}
package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.dao.XxlJobRegistryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class XxlJobRegistryService extends ServiceImpl<XxlJobRegistryMapper, XxlJobRegistry> {

    public List<Integer> findDead(@Param("timeout") int timeout,
                                  @Param("nowTime") Date nowTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.SECOND, -timeout);
        QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().lt(XxlJobRegistry::getUpdateTime, calendar.getTime());
        List<XxlJobRegistry> ilist = list(queryWrapper);
        return ilist.stream().map(XxlJobRegistry::getId).collect(Collectors.toList());
    }

    public List<XxlJobRegistry> findAll(@Param("timeout") int timeout,
                                        @Param("nowTime") Date nowTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowTime);
        calendar.add(Calendar.SECOND, -timeout);
        QueryWrapper<XxlJobRegistry> queryWrappe = new QueryWrapper<>();
        queryWrappe.lambda().gt(XxlJobRegistry::getUpdateTime, calendar.getTime());
        return list(queryWrappe);
    }

    public boolean registryUpdate(@Param("registryGroup") String registryGroup,
                                  @Param("registryKey") String registryKey,
                                  @Param("registryValue") String registryValue,
                                  @Param("updateTime") Date updateTime) {
        XxlJobRegistry xxlJobRegistry = new XxlJobRegistry();
        xxlJobRegistry.setUpdateTime(updateTime);
        QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobRegistry::getRegistryGroup, registryGroup)
                .eq(XxlJobRegistry::getRegistryKey, registryKey)
                .eq(XxlJobRegistry::getRegistryValue, registryValue);
        return update(xxlJobRegistry, queryWrapper);
    }

    public boolean registrySave(@Param("registryGroup") String registryGroup,
                                @Param("registryKey") String registryKey,
                                @Param("registryValue") String registryValue,
                                @Param("updateTime") Date updateTime) {
        XxlJobRegistry xxlJobRegistry = new XxlJobRegistry();
        xxlJobRegistry.setRegistryKey(registryKey);
        xxlJobRegistry.setRegistryGroup(registryGroup);
        xxlJobRegistry.setRegistryValue(registryValue);
        xxlJobRegistry.setUpdateTime(updateTime);
        return save(xxlJobRegistry);
    }

    public boolean registryDelete(@Param("registryGroup") String registryGroup,
                                  @Param("registryKey") String registryKey,
                                  @Param("registryValue") String registryValue) {
        QueryWrapper<XxlJobRegistry> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(XxlJobRegistry::getRegistryGroup, registryGroup)
                .eq(XxlJobRegistry::getRegistryKey, registryKey)
                .eq(XxlJobRegistry::getRegistryValue, registryValue);
        return remove(queryWrapper);
    }
}

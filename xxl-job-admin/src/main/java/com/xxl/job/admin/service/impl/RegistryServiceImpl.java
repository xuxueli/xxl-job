package com.xxl.job.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.common.pojo.entity.Registry;
import com.xxl.job.admin.mapper.RegistryMapper;
import com.xxl.job.admin.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务注册信息 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Slf4j
@Service
public class RegistryServiceImpl extends ServiceImpl<RegistryMapper, Registry> implements RegistryService {

    @Autowired
    private RegistryMapper registryMapper;

    @Override
    public List<Long> findDeadRegistryByUpdatedTime(Date nowTime) {
        return registryMapper.findDeadRegistryByUpdatedTime(DateUtil.formatDateTime(nowTime));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteDeadRegistryByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) return;
        registryMapper.deleteDeadRegistryByIds(ids);
    }

    @Override
    public List<Registry> findAll(Date nowTime) {
        return registryMapper.findAll(DateUtil.formatDateTime(nowTime));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateRegistry(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        registryMapper.updateRegistry(registryGroup, registryKey, registryValue, DateUtil.formatDateTime(updateTime));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void deleteRegistry(String registryGroup, String registryKey, String registryValue) {
        registryMapper.deleteRegistry(registryGroup, registryKey, registryValue);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void syncRegistry(String registryGroup, String registryKey, String registryValue) {
        Registry registry = registryMapper.findRegistry(registryGroup, registryKey, registryValue);
        if (ObjectUtil.isEmpty(registry)) registry = new Registry();
        registry.setRegistryGroup(registryGroup);
        registry.setRegistryKey(registryKey);
        registry.setRegistryValue(registryValue);
        registry.setUpdatedTime(DateUtil.date());
        this.saveOrUpdate(registry);
    }
}

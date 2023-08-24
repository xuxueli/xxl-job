package com.xxl.job.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xxl.job.admin.common.constants.NumberConstant;
import com.xxl.job.admin.common.pojo.dto.JobGroupDTO;
import com.xxl.job.admin.common.pojo.dto.PageDTO;
import com.xxl.job.admin.common.pojo.entity.JobGroup;
import com.xxl.job.admin.common.pojo.entity.Registry;
import com.xxl.job.admin.common.pojo.query.JobGroupQuery;
import com.xxl.job.admin.common.pojo.vo.JobGroupVO;
import com.xxl.job.admin.common.pojo.vo.JobInfoVO;
import com.xxl.job.admin.mapper.JobGroupMapper;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.JobInfoService;
import com.xxl.job.admin.service.RegistryService;
import com.xxl.job.admin.service.base.impl.BaseServiceImpl;
import com.xxl.job.core.enums.RegistryConfig;
import com.xxl.job.core.enums.RegistryType;
import com.xxl.job.core.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 执行器组 服务实现类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-11
 */
@Slf4j
@Service
public class JobGroupServiceImpl extends BaseServiceImpl<JobGroupMapper, JobGroup, JobGroup, JobGroupVO> implements JobGroupService {

    @Autowired
    private JobGroupMapper jobGroupMapper;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private RegistryService registryService;

    @Override
    public JobGroupVO queryById(Serializable id) {
        return this.objectConversion(this.getById(id));
    }

    @Override
    public List<JobGroup> queryList(PageDTO pageDTO) {
        JobGroupQuery query = new JobGroupQuery();
        BeanUtil.copyProperties(pageDTO, query);
        return jobGroupMapper.queryJobGroup(query);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveJobGroup(JobGroupDTO jobGroupDTO) {
        JobGroup jobGroup = jobGroupMapper.queryJobGroupByAppName(jobGroupDTO.getAppName());
        Assert.isNull(jobGroup, ResponseEnum.THE_TASK_GROUP_ALREADY_EXISTS.getMessage());
        jobGroup = new JobGroup();
        BeanUtil.copyProperties(jobGroupDTO, jobGroup);
        jobGroup.setCreatedTime(DateUtil.current());
        jobGroup.setAddressList(joinAddresses(jobGroupDTO.getAddressType(), jobGroupDTO.getAppName(), jobGroupDTO.getAddresses()));
        this.saveOrUpdate(jobGroup);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void updateJobGroup(JobGroupDTO jobGroupDTO) {
        JobGroup jobGroup = this.getById(jobGroupDTO.getId());
        Assert.notNull(jobGroup, ResponseEnum.THE_TASK_GROUP_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        if (!StrUtil.equals(jobGroup.getAppName(), jobGroupDTO.getAppName())) {
            Assert.isNull(jobGroupMapper.queryJobGroupByAppName(jobGroupDTO.getAppName()),
                    ResponseEnum.THE_TASK_GROUP_ALREADY_EXISTS.getMessage());
        }
        BeanUtil.copyProperties(jobGroupDTO, jobGroup);
        jobGroup.setUpdatedTime(DateUtil.current());
        jobGroup.setAddressList(joinAddresses(jobGroupDTO.getAddressType(), jobGroupDTO.getAppName(), jobGroupDTO.getAddresses()));
        this.saveOrUpdate(jobGroup);
    }

    @Override
    public List<JobGroupVO> queryJobGroupByAddressType(Integer addressType) {
        return this.objectConversion(jobGroupMapper.queryJobGroupByAddressType(addressType));
    }

    @Override
    public List<JobGroupVO> findAll() {
        return this.objectConversion(this.list());
    }

    @Override
    public Boolean delete(Serializable id) {
        Assert.notNull(id, ResponseEnum.THE_ID_CANNOT_BE_EMPTY.getMessage());
        Assert.notNull(this.getById(id),
                ResponseEnum.THE_TASK_GROUP_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED.getMessage());
        List<JobInfoVO> jobInfos = jobInfoService.queryJobInfoByGroupId(Convert.toLong(id));
        Assert.isFalse(CollectionUtil.isNotEmpty(jobInfos),
                ResponseEnum.THE_TASK_GROUP_HAS_A_TASK_ASSOCIATION_PROCEDURE.getMessage());

        return this.removeById(id);
    }

    @Override
    public JobGroupVO objectConversion(JobGroup jobGroup) {
        JobGroupVO jobGroupVO = super.objectConversion(jobGroup);
        if (ObjectUtil.isNotEmpty(jobGroupVO)) {
            jobGroupVO.setAddresses(StrUtil.split(jobGroup.getAddressList(), StrUtil.COMMA));
        }
        return jobGroupVO;
    }

    /**
     * 拼接地址
     *
     * @param type      执行器地址类型：0=自动注册、1=手动录入
     * @param appName   APP 名称
     * @param addresses 地址
     * @return {@link String}
     */
    private String joinAddresses(Integer type, String appName, List<String> addresses) {
        if (ObjectUtil.equals(NumberConstant.ONE, type)) {
            Assert.isFalse(CollectionUtil.isEmpty(addresses),
                    ResponseEnum.THE_ACTUATOR_ADDRESS_CANNOT_BE_EMPTY.getMessage());
        } else {
            List<Registry> registries = registryService.findAll(DateUtil.offsetSecond(DateUtil.date(),
                    RegistryConfig.DEAD_TIMEOUT.getValue() * NumberConstant.A_NEGATIVE).getTime());
            if (CollectionUtil.isNotEmpty(registries)) {
                List<String> registryValues = registries.stream()
                        .filter(a -> RegistryType.EXECUTOR.name().equals(a.getRegistryGroup())
                                && StrUtil.equals(appName, a.getRegistryKey())
                                && StrUtil.isNotBlank(a.getRegistryValue()))
                        .map(Registry::getRegistryValue)
                        .collect(Collectors.toList());

                registryValues.forEach(a -> {
                    if (StrUtil.contains(a, StrUtil.COMMA)) {
                        addresses.addAll(StrUtil.split(a, StrUtil.COMMA));
                    } else {
                        addresses.add(a);
                    }
                });
            }
        }
        if (CollectionUtil.isEmpty(addresses)) return null;
        return CollectionUtil.join(CollectionUtil.newHashSet(addresses), StrUtil.COMMA);
    }

}

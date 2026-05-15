package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.exception.XxlException;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.tool.response.PageModel;

import java.util.Date;
import java.util.List;

public interface JobGroupService extends IService<XxlJobGroup> {

    List<XxlJobGroup> findAll();

    List<XxlJobGroup> findByAddressType(int address_type);

    XxlJobGroup load(int id);

    int add(XxlJobGroup jobGroup) throws XxlException;

    int update(XxlJobGroup jobGroup) throws XxlException;

    boolean update(Integer id, String appName, String title, Integer addressType, String addressList, Date updateTime) throws XxlException;

    int remove(List<Integer> ids) throws XxlException;

    List<String> findRegistryByAppName(String appNameParam);

    PageModel<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title);

    void removeByRegistryByKey(String registryGroup, String registryKey);
}
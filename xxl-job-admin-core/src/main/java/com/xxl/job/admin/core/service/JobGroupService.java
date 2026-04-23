package com.xxl.job.admin.core.service;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.tool.response.PageModel;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> findAll();

    XxlJobGroup load(int id);

    int save(XxlJobGroup jobGroup) throws XxlException;

    int update(XxlJobGroup jobGroup) throws XxlException;

    int remove(List<Integer> ids) throws XxlException;

    List<String> findRegistryByAppName(String appNameParam);

    PageModel<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title);

    void removeByRegistryByKey(String registryGroup, String registryKey);
}
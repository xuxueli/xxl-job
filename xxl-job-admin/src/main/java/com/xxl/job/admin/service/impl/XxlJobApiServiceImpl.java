package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.service.XxlJobApiService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author: Dao-yang.
 * @date: Created in 2025/6/30 11:07
 */
@Service
public class XxlJobApiServiceImpl implements XxlJobApiService {

    private static final Logger logger = LoggerFactory.getLogger(XxlJobApiServiceImpl.class);

    @Resource
    private XxlJobService xxlJobService;

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) {

        XxlJobUser xxlJobUser = new XxlJobUser();
        xxlJobUser.setRole(1); // 1-管理员,不需权限控制
        return xxlJobService.add(jobInfo, xxlJobUser);
    }
    @Override
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }

    @Override
    public ReturnT<String> start(int id) {
        return xxlJobService.start(id);
    }

    @Override
    public ReturnT<String> stop(int id) {
        return xxlJobService.stop(id);
    }
}

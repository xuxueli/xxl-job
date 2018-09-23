package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.ApiAdminBiz;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import javax.annotation.Resource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ApiAdminBizImpl extends AdminBizImpl implements ApiAdminBiz {
    @Resource
    private XxlJobService xxlJobService;

    @Override
    public ReturnT<String> add(XxlJobInfo jobInfo) {
        return xxlJobService.add(jobInfo);
    }

    @Override
    public ReturnT<String> update(XxlJobInfo jobInfo) {
        return xxlJobService.update(jobInfo);
    }

    @Override
    public ReturnT<String> remove(int id) {
        return xxlJobService.remove(id);
    }

    @Override
    public ReturnT<String> pause(int id) {
        return xxlJobService.pause(id);
    }

    @Override
    public ReturnT<String> resume(int id) {
        return xxlJobService.resume(id);
    }
}

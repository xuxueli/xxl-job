package com.xxl.job.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxl.job.admin.model.XxlJobLogGlue;

public interface JobLogGlueService extends IService<XxlJobLogGlue>{

    java.util.List<XxlJobLogGlue> findByJobId(int jobId);

    void removeOld(int id, int limit);

    int deleteByJobId(int jobId);
}

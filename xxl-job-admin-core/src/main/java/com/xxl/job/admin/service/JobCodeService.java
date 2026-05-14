package com.xxl.job.admin.service;

import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLogGlue;

import java.util.List;


public interface JobCodeService {

    XxlJobInfo getValidJobInfo(int jobId);

    XxlJobInfo getValidExistsJob(int id, String glueSource, String glueRemark);
    
    boolean updateCode(XxlJobInfo existsJobInfo, String glueSource, String glueRemark);

    List<XxlJobLogGlue> findJobLogGlues(int jobId);
}
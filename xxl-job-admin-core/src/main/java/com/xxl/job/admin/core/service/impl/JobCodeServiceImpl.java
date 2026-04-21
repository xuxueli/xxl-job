package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.core.service.JobCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class JobCodeServiceImpl implements JobCodeService {
    private static final Logger logger = LoggerFactory.getLogger(JobCodeServiceImpl.class);

    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;
    @Resource
    private XxlJobLogGlueMapper xxlJobLogGlueMapper;

    @Override
    public XxlJobInfo loadWithCode(int jobId) {
        XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(jobId);
        if (jobInfo == null) {
            return null;
        }
        return jobInfo;
    }

    @Override
    public boolean updateCode(int jobId, String glueSource, String glueRemark, int userId) {
        XxlJobInfo existsJobInfo = xxlJobInfoMapper.loadById(jobId);
        if (existsJobInfo == null) {
            return false;
        }

        // update job info
        existsJobInfo.setGlueSource(glueSource);
        existsJobInfo.setGlueRemark(glueRemark);
        existsJobInfo.setGlueUpdatetime(new Date());
        xxlJobInfoMapper.update(existsJobInfo);

        // save glue history
        XxlJobLogGlue jobLogGlue = new XxlJobLogGlue();
        jobLogGlue.setJobId(jobId);
        jobLogGlue.setGlueType(existsJobInfo.getGlueType());
        jobLogGlue.setGlueSource(glueSource);
        jobLogGlue.setGlueRemark(glueRemark);
        jobLogGlue.setAddTime(new Date());
        jobLogGlue.setUpdateTime(new Date());
        xxlJobLogGlueMapper.save(jobLogGlue);

        // remove old history
        xxlJobLogGlueMapper.removeOld(jobId, 30);

        logger.info("JobCodeServiceImpl.updateCode success, jobId: {}, glueRemark: {}, userId: {}",
                jobId, glueRemark, userId);
        return true;
    }

    @Override
    public List<XxlJobLogGlue> findJobLogGlues(int jobId) {
        return xxlJobLogGlueMapper.findByJobId(jobId);
    }
}
package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.core.service.JobCodeService;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.admin.core.service.JobLogGlueService;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.tool.core.StringTool;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class JobCodeServiceImpl implements JobCodeService {
    @Resource
    private JobInfoService jobInfoService;

    @Resource
    private JobLogGlueService jobLogGlueService;

    @Override
    public XxlJobInfo getValidJobInfo(int jobId) {
        XxlJobInfo jobInfo = jobInfoService.getById(jobId);

		if (jobInfo == null) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_invalid"));
		}
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_invalid"));
		}

        return jobInfo;
    }

    @Override
    public List<XxlJobLogGlue> findJobLogGlues(int jobId) {
        return jobLogGlueService.findByJobId(jobId);
    }

    @Override
    public XxlJobInfo getValidExistsJob(int id, String glueSource, String glueRemark) {
        // valid
		if (StringTool.isBlank(glueSource)) {
			throw new XxlException(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_source") );
		}
		if (glueRemark==null) {
			throw new XxlException(I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark") );
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			throw new XxlException(I18nUtil.getString("jobinfo_glue_remark_limit"));
		}
		XxlJobInfo existsJobInfo = jobInfoService.getById(id);
		if (existsJobInfo == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_glue_jobid_invalid"));
		}
        return existsJobInfo;
    }

    @Override
    @Transactional
    public boolean updateCode(XxlJobInfo existsJobInfo, String glueSource, String glueRemark) {
        // update job info
        existsJobInfo.setGlueSource(glueSource);
        existsJobInfo.setGlueRemark(glueRemark);
        existsJobInfo.setGlueUpdatetime(new Date());
        existsJobInfo.setUpdateTime(new Date());
        jobInfoService.updateById(existsJobInfo);

        // save glue history
        XxlJobLogGlue jobLogGlue = new XxlJobLogGlue();
        jobLogGlue.setJobId(existsJobInfo.getId());
        jobLogGlue.setGlueType(existsJobInfo.getGlueType());
        jobLogGlue.setGlueSource(glueSource);
        jobLogGlue.setGlueRemark(glueRemark);
        jobLogGlue.setAddTime(new Date());
        jobLogGlue.setUpdateTime(new Date());
        jobLogGlueService.save(jobLogGlue);

        // remove old history
        jobLogGlueService.removeOld(existsJobInfo.getId(), 30);

        return true;
    }

}
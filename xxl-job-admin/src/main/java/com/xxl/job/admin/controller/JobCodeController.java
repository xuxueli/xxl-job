package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogGlueDao;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * job code controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobcode")
public class JobCodeController {
	
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	@Resource
	private IXxlJobLogGlueDao xxlJobLogGlueDao;

	@RequestMapping
	public String index(Model model, int jobId) {
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
		List<XxlJobLogGlue> jobLogGlues = xxlJobLogGlueDao.findByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException("抱歉，任务不存在.");
		}

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "jobcode/jobcode.index";
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(Model model, int id, String glueSource, String glueRemark) {
		// valid
		if (glueRemark==null) {
			return new ReturnT<String>(500, "请输入备注");
		}
		if (glueRemark.length()<6 || glueRemark.length()>100) {
			return new ReturnT<String>(500, "备注长度应该在6至100之间");
		}
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(id);
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, "参数异常");
		}
		
		// log old code
		XxlJobLogGlue xxlJobLogGlue = new XxlJobLogGlue();
		xxlJobLogGlue.setJobId(exists_jobInfo.getId());
		xxlJobLogGlue.setGlueSource(exists_jobInfo.getGlueSource());
		xxlJobLogGlue.setGlueRemark(exists_jobInfo.getGlueRemark());
		xxlJobLogGlueDao.save(xxlJobLogGlue);
		
		// update new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdatetime(new Date());
		xxlJobInfoDao.update(exists_jobInfo);

		// remove code backup more than 30
		xxlJobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);

		return ReturnT.SUCCESS;
	}
	
}

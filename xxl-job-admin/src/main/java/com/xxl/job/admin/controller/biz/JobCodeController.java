package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLogGlue;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	private XxlJobInfoMapper xxlJobInfoMapper;
	@Resource
	private XxlJobLogGlueMapper xxlJobLogGlueMapper;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam("jobId") int jobId) {
		XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(jobId);
		List<XxlJobLogGlue> jobLogGlues = xxlJobLogGlueMapper.findByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
		}

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());

		// Glue类型-字典
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "jobcode/jobcode.index";
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(HttpServletRequest request,
								@RequestParam("id") int id,
								@RequestParam("glueSource") String glueSource,
								@RequestParam("glueRemark") String glueRemark) {

		// valid
		if (glueRemark==null) {
			return ReturnT.ofFail( (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")) );
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return ReturnT.ofFail(I18nUtil.getString("jobinfo_glue_remark_limit"));
		}
		XxlJobInfo existsJobInfo = xxlJobInfoMapper.loadById(id);
		if (existsJobInfo == null) {
			return ReturnT.ofFail( I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, existsJobInfo.getJobGroup());

		// update new code
		existsJobInfo.setGlueSource(glueSource);
		existsJobInfo.setGlueRemark(glueRemark);
		existsJobInfo.setGlueUpdatetime(new Date());

		existsJobInfo.setUpdateTime(new Date());
		xxlJobInfoMapper.update(existsJobInfo);

		// log old code
		XxlJobLogGlue xxlJobLogGlue = new XxlJobLogGlue();
		xxlJobLogGlue.setJobId(existsJobInfo.getId());
		xxlJobLogGlue.setGlueType(existsJobInfo.getGlueType());
		xxlJobLogGlue.setGlueSource(glueSource);
		xxlJobLogGlue.setGlueRemark(glueRemark);

		xxlJobLogGlue.setAddTime(new Date());
		xxlJobLogGlue.setUpdateTime(new Date());
		xxlJobLogGlueMapper.save(xxlJobLogGlue);

		// remove code backup more than 30
		xxlJobLogGlueMapper.removeOld(existsJobInfo.getId(), 30);

		return ReturnT.ofSuccess();
	}
	
}

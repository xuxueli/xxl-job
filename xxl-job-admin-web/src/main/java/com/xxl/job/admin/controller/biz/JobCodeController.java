package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.core.service.JobCodeService;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * job code controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobcode")
public class JobCodeController {
	private static final Logger logger = LoggerFactory.getLogger(JobCodeController.class);

	@Resource
	private JobCodeService jobCodeService;
	
	@Resource
	private JobInfoService jobInfoService;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam("jobId") int jobId) {
		XxlJobInfo jobInfo = jobCodeService.getValidJobInfo(jobId);

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());

		List<XxlJobLogGlue> jobLogGlues = jobCodeService.findJobLogGlues(jobId);

		// Glue类型-字典
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "biz/job.code";
	}

	@RequestMapping("/save")
	@ResponseBody
	public Response<String> save(HttpServletRequest request,
								 @RequestParam("id") int id,
								 @RequestParam("glueSource") String glueSource,
								 @RequestParam("glueRemark") String glueRemark) {
		XxlJobInfo existsJobInfo = jobCodeService.getValidExistsJob(id, glueSource, glueRemark);

		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, existsJobInfo.getJobGroup());

		jobCodeService.updateCode(existsJobInfo, glueSource, glueRemark);
	
		logger.info(">>>>>>>>>>> xxl-job operation log: operator = {}, type = {}, content = {}",
				loginInfo.getUserName(), "jobcode-update", "jobId=" + id);
		return Response.ofSuccess();
	}
}
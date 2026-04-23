package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.exception.XxlJobException;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.admin.core.service.JobLogService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {

	@Resource
	private JobGroupService jobGroupService;
	@Resource
	private JobInfoService jobInfoService;
	@Resource
	private JobLogService jobLogService;

	@RequestMapping
	public String index(HttpServletRequest request,
						Model model,
						@RequestParam(value = "jobGroup", required = false, defaultValue = "0") Integer jobGroup,
						@RequestParam(value = "jobId", required = false, defaultValue = "0") Integer jobId) {

		// 1、init JobGroupList
		List<XxlJobGroup> jobGroupListTotal = jobGroupService.findAll();

		// filter JobGroupList
		List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
		if (CollectionTool.isEmpty(jobGroupList)) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}
		List<Integer> jobGroupIds = jobGroupList.stream().map(XxlJobGroup::getId).toList();

		// 2、check jobId
		if (jobId > 0) {
			XxlJobInfo jobInfo = jobInfoService.getJobInfoById(jobId);
			if (jobInfo == null) {
				throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_invalid"));
			}
			jobGroup = jobInfo.getJobGroup();
		}

		// 3、init jobGroup, default first 1
		if (!jobGroupIds.contains(jobGroup)) {
			jobGroup = jobGroupList.get(0).getId();
		}

		// 4、init jobInfoList
		List<XxlJobInfo> jobInfoList = jobInfoService.getJobsByGroupId(jobGroup);
		List<Integer> jobIds = jobInfoList.stream().map(XxlJobInfo::getId).toList();

		// 5、init JobId, default 0
		if (!jobIds.contains(jobId)) {
			jobId = 0;
		}

		// write
		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobInfoList", jobInfoList);
		model.addAttribute("jobGroup", jobGroup);
		model.addAttribute("jobId", jobId);

		return "biz/log.list";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Response<PageModel<XxlJobLog>> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int offset,
										@RequestParam(required = false, defaultValue = "10") int pagesize,
										@RequestParam int jobGroup,
										@RequestParam int jobId,
										@RequestParam int logStatus,
										@RequestParam String filterTime) {

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

		// page query
		PageModel<XxlJobLog> pageModel = jobLogService.pageList(offset, pagesize, jobGroup, jobId, logStatus, filterTime);

		return Response.ofSuccess(pageModel);
	}

	

	@RequestMapping("/logKill")
	@ResponseBody
	public Response<String> logKill(HttpServletRequest request, @RequestParam("id") long id){
		// valid JobGroup permission
		XxlJobLog log = jobLogService.load(id);
		JobGroupPermissionUtil.validJobGroupPermission(request, log.getJobGroup());

		// call service
		return jobLogService.kill(id, group -> {
			JobGroupPermissionUtil.validJobGroupPermission(request, group);
			return true;
		});
	}

	@RequestMapping("/clearLog")
	@ResponseBody
	public Response<String> clearLog(HttpServletRequest request,
									@RequestParam("jobGroup") int jobGroup,
									@RequestParam("jobId") int jobId,
									@RequestParam("type") int type){
		// valid JobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

		// call service
		int ret = jobLogService.clearLog(jobGroup, jobId, type);
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/logDetailPage")
	public String logDetailPage(HttpServletRequest request, @RequestParam("id") long id, Model model){

		// load log (service throws XxlException if not found, caught by GlobalExceptionHandler)
		XxlJobLog jobLog = jobLogService.loadAndValidate(id);

		// valid permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobLog.getJobGroup());

		// load jobInfo
		XxlJobInfo jobInfo = jobInfoService.getJobInfoById(jobLog.getJobId());

		// data
		model.addAttribute("triggerCode", jobLog.getTriggerCode());
		model.addAttribute("handleCode", jobLog.getHandleCode());
		model.addAttribute("logId", jobLog.getId());
		model.addAttribute("jobInfo", jobInfo);
		return "biz/log.detail";
	}

	@RequestMapping("/logDetailCat")
	@ResponseBody
	public Response<LogResult> logDetailCat(@RequestParam("logId") long logId, @RequestParam("fromLineNum") int fromLineNum){
		// get log detail from service (validates log exists, throws XxlException if not found)

		return jobLogService.getLogDetailCat(logId, fromLineNum);
	}

}
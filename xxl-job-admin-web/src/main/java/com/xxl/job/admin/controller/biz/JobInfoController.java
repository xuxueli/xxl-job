package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.scheduler.exception.XxlJobException;
import com.xxl.job.admin.core.scheduler.misfire.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.type.ScheduleTypeEnum;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.sso.core.model.LoginInfo;
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
@RequestMapping("/jobinfo")
public class JobInfoController {
	@Resource
	private JobGroupService jobGroupService;

	@Resource
	private JobInfoService jobInfoService;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(value = "jobGroup", required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典
		model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// 调度类型
		model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// 调度过期策略

		// 执行器列表
		List<XxlJobGroup> jobGroupListTotal = jobGroupService.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
		if (CollectionTool.isEmpty(jobGroupList)) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		// parse jobGroup
		if (!(CollectionTool.isNotEmpty(jobGroupList)
				&& jobGroupList.stream().map(XxlJobGroup::getId).toList().contains(jobGroup))) {
			jobGroup = -1;
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "biz/job.list";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Response<PageModel<XxlJobInfo>> pageList(HttpServletRequest request,
													@RequestParam(required = false, defaultValue = "0") int offset,
													@RequestParam(required = false, defaultValue = "10") int pagesize,
													@RequestParam int jobGroup,
													@RequestParam int triggerStatus,
													@RequestParam String jobDesc,
													@RequestParam String executorHandler,
													@RequestParam String author) {

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

		// page
		return Response.ofSuccess(
			jobInfoService.pageList(
				offset, pagesize, jobGroup,
				triggerStatus, jobDesc, executorHandler, author)
		);
	}

	@RequestMapping("/insert")
	@ResponseBody
	public Response<String> add(HttpServletRequest request, XxlJobInfo jobInfo) {
		// valid permission (SSO related, keep in controller)
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());
		String userName = loginInfo.getUserName();

		// call service (validation logic is in service)
		int ret = jobInfoService.add(jobInfo, userName, group -> JobGroupPermissionUtil.hasJobGroupPermission(loginInfo, group));
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/update")
	@ResponseBody
	public Response<String> update(HttpServletRequest request, XxlJobInfo jobInfo) {
		// valid permission (SSO related, keep in controller)
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());
		String userName = loginInfo.getUserName();

		// call service (validation logic is in service)
		int ret = jobInfoService.update(jobInfo, userName, group -> JobGroupPermissionUtil.hasJobGroupPermission(loginInfo, group));
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/delete")
	@ResponseBody
	public Response<String> delete(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {
		// valid permission (SSO related, keep in controller)
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, -1);
		String userName = loginInfo.getUserName();

		// call service (validation is in service)
		int ret = jobInfoService.remove(ids, userName, group -> JobGroupPermissionUtil.hasJobGroupPermission(loginInfo, group));
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/stop")
	@ResponseBody
	public Response<String> pause(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {
		// valid permission (SSO related, keep in controller)
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, -1);
		String userName = loginInfo.getUserName();

		// call service (validation is in service)
		int ret = jobInfoService.stop(ids, userName, group -> JobGroupPermissionUtil.hasJobGroupPermission(loginInfo, group));
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/start")
	@ResponseBody
	public Response<String> start(HttpServletRequest request, @RequestParam("ids[]") List<Integer> ids) {
		// valid permission (SSO related, keep in controller)
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, -1);
		String userName = loginInfo.getUserName();

		// call service (validation is in service)
		int ret = jobInfoService.start(ids, userName, group -> JobGroupPermissionUtil.hasJobGroupPermission(loginInfo, group));
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/trigger")
	@ResponseBody
	public Response<String> triggerJob(HttpServletRequest request,
									  @RequestParam("id") int id,
									  @RequestParam("executorParam") String executorParam,
									  @RequestParam("addressList") String addressList) {
		// valid permission (SSO related, keep in controller)
		LoginInfo loginInfo = JobGroupPermissionUtil.validJobGroupPermission(request, -1);
		String userName = loginInfo.getUserName();

		// call service
		int ret = jobInfoService.trigger(id, userName, executorParam, addressList, group -> JobGroupPermissionUtil.hasJobGroupPermission(loginInfo, group));
		return ret > 0 ? Response.ofSuccess() : Response.ofFail();
	}

	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public Response<List<String>> nextTriggerTime(@RequestParam("scheduleType") String scheduleType,
												 @RequestParam("scheduleConf") String scheduleConf) {

		// call service (validation and logic is in service)
		List<String> result = jobInfoService.generateNextTriggerTime(scheduleType, scheduleConf);
		return Response.ofSuccess(result);
	}

}
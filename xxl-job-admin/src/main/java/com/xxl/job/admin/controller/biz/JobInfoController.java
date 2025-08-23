package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.constant.Consts;
import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.scheduler.exception.XxlJobException;
import com.xxl.job.admin.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.scheduler.scheduler.MisfireStrategyEnum;
import com.xxl.job.admin.scheduler.scheduler.ScheduleTypeEnum;
import com.xxl.job.admin.scheduler.thread.JobScheduleHelper;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;
import com.xxl.sso.core.helper.XxlSsoHelper;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	private static Logger logger = LoggerFactory.getLogger(JobInfoController.class);

	@Resource
	private XxlJobGroupMapper xxlJobGroupMapper;
	@Resource
	private XxlJobService xxlJobService;
	
	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(value = "jobGroup", required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典
		model.addAttribute("ScheduleTypeEnum", ScheduleTypeEnum.values());	    				// 调度类型
		model.addAttribute("MisfireStrategyEnum", MisfireStrategyEnum.values());	    			// 调度过期策略

		// 执行器列表
		List<XxlJobGroup> jobGroupListTotal =  xxlJobGroupMapper.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = filterJobGroupByPermission(request, jobGroupListTotal);
		if (jobGroupList==null || jobGroupList.isEmpty()) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(HttpServletRequest request,
										@RequestParam(value = "start", required = false, defaultValue = "0") int start,
										@RequestParam(value = "length", required = false, defaultValue = "10") int length,
										@RequestParam("jobGroup") int jobGroup,
										@RequestParam("triggerStatus") int triggerStatus,
										@RequestParam("jobDesc") String jobDesc,
										@RequestParam("executorHandler") String executorHandler,
										@RequestParam("author") String author) {

		// valid jobGroup permission
		validJobGroupPermission(request, jobGroup);

		// page
		return xxlJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(HttpServletRequest request, XxlJobInfo jobInfo) {
		// valid permission
		LoginInfo loginInfo = validJobGroupPermission(request, jobInfo.getJobGroup());

		// opt
		return xxlJobService.add(jobInfo, loginInfo);
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(HttpServletRequest request, XxlJobInfo jobInfo) {
		// valid permission
		LoginInfo loginInfo = validJobGroupPermission(request, jobInfo.getJobGroup());

		// opt
		return xxlJobService.update(jobInfo, loginInfo);
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(HttpServletRequest request, @RequestParam("id") int id) {
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.remove(id, loginInfoResponse.getData());
	}
	
	@RequestMapping("/stop")
	@ResponseBody
	public ReturnT<String> pause(HttpServletRequest request, @RequestParam("id") int id) {
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.stop(id, loginInfoResponse.getData());
	}
	
	@RequestMapping("/start")
	@ResponseBody
	public ReturnT<String> start(HttpServletRequest request, @RequestParam("id") int id) {
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.start(id, loginInfoResponse.getData());
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(HttpServletRequest request,
									  @RequestParam("id") int id,
									  @RequestParam("executorParam") String executorParam,
									  @RequestParam("addressList") String addressList) {
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		return xxlJobService.trigger(loginInfoResponse.getData(), id, executorParam, addressList);
	}

	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public ReturnT<List<String>> nextTriggerTime(@RequestParam("scheduleType") String scheduleType,
												 @RequestParam("scheduleConf") String scheduleConf) {

		XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
		paramXxlJobInfo.setScheduleType(scheduleType);
		paramXxlJobInfo.setScheduleConf(scheduleConf);

		List<String> result = new ArrayList<>();
		try {
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = JobScheduleHelper.generateNextValidTime(paramXxlJobInfo, lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("nextTriggerTime error. scheduleType = {}, scheduleConf= {}", scheduleType, scheduleConf, e);
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, (I18nUtil.getString("schedule_type")+I18nUtil.getString("system_unvalid")) + e.getMessage());
		}
		return ReturnT.ofSuccess(result);

	}


	// -------------------- tool --------------------

	/**
	 * check if has jobgroup permission
	 */
	public static boolean hasJobGroupPermission(LoginInfo loginInfo, int jobGroup){
		if (XxlSsoHelper.hasRole(loginInfo, Consts.ADMIN_ROLE).isSuccess()) {
			return true;
		} else {
			List<String> jobGroups = (loginInfo.getExtraInfo()!=null && loginInfo.getExtraInfo().containsKey("jobGroups"))
					? List.of(StringTool.tokenizeToArray(loginInfo.getExtraInfo().get("jobGroups"), ",")) :new ArrayList<>();
			return jobGroups.contains(String.valueOf(jobGroup));
		}
	}

	/**
	 * valid jobGroup permission
	 */
	public static LoginInfo validJobGroupPermission(HttpServletRequest request, int jobGroup) {
		Response<LoginInfo> loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);
		if (!(loginInfoResponse.isSuccess() && hasJobGroupPermission(loginInfoResponse.getData(), jobGroup))) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username="+ loginInfoResponse.getData().getUserName() +"]");
		}
		return loginInfoResponse.getData();
	}

	/**
	 * filter jobGroupList by permission
	 */
	public static List<XxlJobGroup> filterJobGroupByPermission(HttpServletRequest request, List<XxlJobGroup> jobGroupListTotal){
		Response<LoginInfo>  loginInfoResponse = XxlSsoHelper.loginCheckWithAttr(request);

		if (XxlSsoHelper.hasRole(loginInfoResponse.getData(), Consts.ADMIN_ROLE).isSuccess()) {
			return jobGroupListTotal;
		} else {
			List<String> jobGroups = (loginInfoResponse.getData().getExtraInfo()!=null && loginInfoResponse.getData().getExtraInfo().containsKey("jobGroups"))
					? List.of(StringTool.tokenizeToArray(loginInfoResponse.getData().getExtraInfo().get("jobGroups"), ",")) :new ArrayList<>();

			return jobGroupListTotal
					.stream()
					.filter(jobGroup -> jobGroups.contains(String.valueOf(jobGroup.getId())))
					.toList();
		}
	}
	
}

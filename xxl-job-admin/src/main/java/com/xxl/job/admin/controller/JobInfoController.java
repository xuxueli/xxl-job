package com.xxl.job.admin.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.Page;
import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.JobGroupService;
import com.xxl.job.admin.service.LoginService;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.util.DateUtil;

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
	private XxlJobService xxlJobService;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	    // 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	    // 阻塞处理策略-字典

		// 执行器列表
		List<XxlJobGroup> jobGroupList_all = jobGroupService.select(null, new XxlJobGroup());

		// filter group
		List<XxlJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList == null || jobGroupList.size() == 0) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}

	public static List<XxlJobGroup> filterJobGroupByRole(HttpServletRequest request, List<XxlJobGroup> jobGroupList_all) {
		List<XxlJobGroup> jobGroupList = new ArrayList<>();
		if (jobGroupList_all == null || jobGroupList_all.isEmpty())
			return jobGroupList;

		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
		if (loginUser.getRole() == 1) {
			jobGroupList = jobGroupList_all;
		} else {
			List<String> groupIdStrs = new ArrayList<>();
			String pmsn = loginUser.getPermission();
			if (pmsn != null && (pmsn = pmsn.trim()).length() > 0) {
				groupIdStrs = Arrays.asList(pmsn.split(","));
			}
			for (XxlJobGroup groupItem : jobGroupList_all) {
				if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
					jobGroupList.add(groupItem);
				}
			}
		}
		return jobGroupList;
	}

	public static void validPermission(HttpServletRequest request, int jobGroup) {
		XxlJobUser loginUser = (XxlJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
		if (!loginUser.validPermission(jobGroup)) {
			throw new RuntimeException(I18nUtil.getString("system_permission_limit") + "[username=" + loginUser.getUsername() + "]");
		}
	}

	@ResponseBody
	@RequestMapping("/pageList")
	public Map<String, Object> pageList(XxlJobInfo j,
	                                    @RequestParam(required = false, defaultValue = "0") int start,
	                                    @RequestParam(required = false, defaultValue = "10") int length) {
		int pageNum = start / length + 1, pageSize = length;
		Page<XxlJobInfo> rs = xxlJobService.select(new Page<XxlJobInfo>(pageNum, pageSize), j);
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", rs.getTotal()); // 总记录数
		maps.put("recordsFiltered", rs.getTotal()); // 过滤后的总记录数
		maps.put("data", rs); // 分页列表
		return maps;
	}

	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<Integer> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}

	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<Integer> remove(int id) {
		return xxlJobService.remove(id);
	}

	@RequestMapping("/stop")
	@ResponseBody
	public ReturnT<String> pause(int id) {
		return xxlJobService.stop(id);
	}

	@RequestMapping("/start")
	@ResponseBody
	public ReturnT<String> start(int id) {
		return xxlJobService.start(id);
	}

	@RequestMapping("/trigger")
	@ResponseBody
	// @PermissionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/nextTriggerTime")
	@ResponseBody
	public ReturnT<List<String>> nextTriggerTime(String cron) {
		List<String> result = new ArrayList<>();
		try {
			CronExpression cronExpression = new CronExpression(cron);
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = cronExpression.getNextValidTimeAfter(lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (ParseException e) {
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
		}
		return new ReturnT<List<String>>(result);
	}

	@ResponseBody
	@RequestMapping(value = "/add4appName", method = RequestMethod.POST)
	public ReturnT<Integer> add4appName(@RequestBody XxlJobInfo j) {
		Integer id = xxlJobService.add4appName(j);
		return new ReturnT<>(id);
	}

	@ResponseBody
	@RequestMapping(value = "/update4appName", method = RequestMethod.POST)
	public ReturnT<Integer> update4appName(@RequestBody XxlJobInfo j) {
		if (StringUtils.isEmpty(j.getAppName())
				&& (StringUtils.isEmpty(j.getExecutorParam()) || StringUtils.isEmpty(j.getExecutorHandler())))  //以执行器名称 和 任务名称、参数，确定某部分数据
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_opt_fail"));

		Integer num = xxlJobService.update4appName(j);
		return new ReturnT<>(num);
	}

	@ResponseBody
	@RequestMapping(value = "/rm4appName", method = RequestMethod.POST)
	public ReturnT<Integer> rm4appName(@RequestBody XxlJobInfo j) {
		if (StringUtils.isEmpty(j.getAppName())
				&& (StringUtils.isEmpty(j.getExecutorParam()) || StringUtils.isEmpty(j.getExecutorHandler())))  //以执行器名称 和 任务名称、参数，确定某部分数据
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("system_opt_fail"));

		Integer num = xxlJobService.rm4appName(j);
		return new ReturnT<>(num);
	}
}

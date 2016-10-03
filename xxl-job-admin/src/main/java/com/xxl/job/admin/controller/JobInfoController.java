package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.ReturnT;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.service.IXxlJobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {

	@Resource
	private IXxlJobGroupDao xxlJobGroupDao;
	@Resource
	private IXxlJobService xxlJobService;
	
	@RequestMapping
	public String index(Model model) {

		// 任务组
		List<XxlJobGroup> jobGroupList =  xxlJobGroupDao.findAll();

		model.addAttribute("JobGroupList", jobGroupList);
		return "jobinfo/jobinfo.index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, String executorHandler, String filterTime) {
		
		return xxlJobService.pageList(start, length, jobGroup, executorHandler, filterTime);
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(int jobGroup, String jobCron, String jobDesc, String author, String alarmEmail,
			String executorAppname, String executorAddress, String executorHandler, String executorParam,
			int glueSwitch, String glueSource, String glueRemark, String childJobKey) {
		
		return xxlJobService.add(jobGroup, jobCron, jobDesc, author, alarmEmail,
				executorAddress, executorHandler, executorParam,
				glueSwitch, glueSource, glueRemark, childJobKey);
	}
	
	@RequestMapping("/reschedule")
	@ResponseBody
	public ReturnT<String> reschedule(int jobGroup, String jobName, String jobCron, String jobDesc, String author, String alarmEmail,
			String executorAppname, String executorAddress, String executorHandler, String executorParam,
			int glueSwitch, String childJobKey) {

		return xxlJobService.reschedule(jobGroup, jobName, jobCron, jobDesc, author, alarmEmail,
				executorAddress, executorHandler, executorParam, glueSwitch, childJobKey);
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int jobGroup, String jobName) {
		return xxlJobService.remove(jobGroup, jobName);
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(int jobGroup, String jobName) {
		return xxlJobService.pause(jobGroup, jobName);
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<String> resume(int jobGroup, String jobName) {
		return xxlJobService.resume(jobGroup, jobName);
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(int jobGroup, String jobName) {
		return xxlJobService.triggerJob(jobGroup, jobName);
	}
	
}

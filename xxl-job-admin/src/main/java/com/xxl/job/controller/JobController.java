package com.xxl.job.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.util.JacksonUtil;
import com.xxl.job.core.model.ReturnT;
import com.xxl.job.core.model.XxlJobInfo;
import com.xxl.job.core.util.DynamicSchedulerUtil;
import com.xxl.job.dao.IXxlJobInfoDao;
import com.xxl.job.service.job.HttpJobBean;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/job")
public class JobController {
	
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	
	@RequestMapping
	public String index(Model model) {
		//List<Map<String, Object>> jobList = DynamicSchedulerUtil.getJobList();
		//model.addAttribute("jobList", jobList);
		return "job/index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			String jobName, String filterTime) {
		
		// page list
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobName, null, null);
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobName, null, null);
		
		// fill job info
		if (list!=null && list.size()>0) {
			for (XxlJobInfo jobInfo : list) {
				DynamicSchedulerUtil.fillJobInfo(jobInfo);
			}
		}
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
	    maps.put("data", list);  					// 分页列表
		return maps;
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(HttpServletRequest request) {
		String triggerKeyName = null;
		String cronExpression = null;
		Map<String, String> jobData = new HashMap<String, String>();
		
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		Set<Map.Entry<String, String[]>> paramSet = request.getParameterMap().entrySet();
		for (Entry<String, String[]> param : paramSet) {
			if (param.getKey().equals("triggerKeyName")) {
				triggerKeyName = param.getValue()[0];
			} else if (param.getKey().equals("cronExpression")) {
				cronExpression = param.getValue()[0];
			} else {
				jobData.put(param.getKey(), (String) (param.getValue().length>0?param.getValue()[0]:param.getValue()));
			}
		}
		
		// triggerKeyName
		if (StringUtils.isBlank(triggerKeyName)) {
			return new ReturnT<String>(500, "请输入“任务key”");
		}
		
		// cronExpression
		if (StringUtils.isBlank(cronExpression)) {
			return new ReturnT<String>(500, "请输入“任务corn”");
		}
		if (!CronExpression.isValidExpression(cronExpression)) {
			return new ReturnT<String>(500, "“任务corn”不合法");
		}
		
		// jobData
		if (jobData.get(HandlerRepository.job_desc)==null || jobData.get(HandlerRepository.job_desc).toString().trim().length()==0) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (jobData.get(HandlerRepository.job_url)==null || jobData.get(HandlerRepository.job_url).toString().trim().length()==0) {
			return new ReturnT<String>(500, "请输入“任务URL”");
		}
		if (jobData.get(HandlerRepository.handleName)==null || jobData.get(HandlerRepository.handleName).toString().trim().length()==0) {
			return new ReturnT<String>(500, "请输入“任务handler”");
		}
		
		// jobClass
		Class<? extends Job> jobClass = HttpJobBean.class;
		
		try {
			// add job 2 quartz
			boolean result = DynamicSchedulerUtil.addJob(triggerKeyName, cronExpression, jobClass, null);
			if (!result) {
				return new ReturnT<String>(500, "任务ID重复，请更换确认");
			}
			// Backup to the database
			XxlJobInfo jobInfo = new XxlJobInfo();
			jobInfo.setJobName(triggerKeyName);
			jobInfo.setJobCron(cronExpression);
			jobInfo.setJobClass(jobClass.getName());
			jobInfo.setJobData(JacksonUtil.writeValueAsString(jobData));
			xxlJobInfoDao.save(jobInfo);
			
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/reschedule")
	@ResponseBody
	public ReturnT<String> reschedule(String triggerKeyName, String cronExpression) {
		// triggerKeyName
		if (StringUtils.isBlank(triggerKeyName)) {
			return new ReturnT<String>(500, "请输入“任务key”");
		}
		// cronExpression
		if (StringUtils.isBlank(cronExpression)) {
			return new ReturnT<String>(500, "请输入“任务corn”");
		}
		if (!CronExpression.isValidExpression(cronExpression)) {
			return new ReturnT<String>(500, "“任务corn”不合法");
		}
		try {
			DynamicSchedulerUtil.rescheduleJob(triggerKeyName, cronExpression);
			
			// update
			XxlJobInfo jobInfo = xxlJobInfoDao.load(triggerKeyName);
			if (jobInfo!=null) {
				jobInfo.setJobCron(cronExpression);
				xxlJobInfoDao.update(jobInfo);
			}
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String triggerKeyName) {
		try {
			if (triggerKeyName!=null) {
				DynamicSchedulerUtil.removeJob(triggerKeyName);
				xxlJobInfoDao.delete(triggerKeyName);
				return ReturnT.SUCCESS;
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(String triggerKeyName) {
		try {
			DynamicSchedulerUtil.pauseJob(triggerKeyName);
			// update
			XxlJobInfo jobInfo = xxlJobInfoDao.load(triggerKeyName);
			if (jobInfo!=null) {
				jobInfo.setJobStatus("PAUSED");
				xxlJobInfoDao.update(jobInfo);
			}
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<String> resume(String triggerKeyName) {
		try {
			DynamicSchedulerUtil.resumeJob(triggerKeyName);
			// update
			XxlJobInfo jobInfo = xxlJobInfoDao.load(triggerKeyName);
			if (jobInfo!=null) {
				jobInfo.setJobStatus("NORMAL");
				xxlJobInfoDao.update(jobInfo);
			}
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(String triggerKeyName) {
		try {
			DynamicSchedulerUtil.triggerJob(triggerKeyName);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
}

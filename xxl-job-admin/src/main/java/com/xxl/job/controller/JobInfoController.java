package com.xxl.job.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.util.JacksonUtil;
import com.xxl.job.core.constant.Constants.JobGroupEnum;
import com.xxl.job.core.model.ReturnT;
import com.xxl.job.core.model.XxlJobInfo;
import com.xxl.job.core.util.DynamicSchedulerUtil;
import com.xxl.job.dao.IXxlJobInfoDao;
import com.xxl.job.dao.IXxlJobLogDao;
import com.xxl.job.dao.IXxlJobLogGlueDao;
import com.xxl.job.service.job.RemoteHttpJobBean;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	@Resource
	public IXxlJobLogDao xxlJobLogDao;
	@Resource
	private IXxlJobLogGlueDao xxlJobLogGlueDao;
	
	@RequestMapping
	public String index(Model model) {
		model.addAttribute("JobGroupList", JobGroupEnum.values());			// 任务组列表
		return "jobinfo/index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			String jobGroup, String jobName, String filterTime) {
		
		// page list
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(start, length, jobGroup, jobName);
		int list_count = xxlJobInfoDao.pageListCount(start, length, jobGroup, jobName);
		
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
	public ReturnT<String> add(String jobGroup, String jobName, String jobCron, String jobDesc,
			String handler_address, String handler_name, String handler_params, 
			String author, String alarmEmail, int alarmThreshold, int glueSwitch) {
		
		// valid
		if (JobGroupEnum.match(jobGroup) == null) {
			return new ReturnT<String>(500, "请选择“任务组”");
		}
		if (StringUtils.isBlank(jobName)) {
			return new ReturnT<String>(500, "请输入“任务名”");
		}
		if (!CronExpression.isValidExpression(jobCron)) {
			return new ReturnT<String>(500, "“corn”不合法");
		}
		if (StringUtils.isBlank(jobDesc)) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(handler_address)) {
			return new ReturnT<String>(500, "请输入“机器地址”");
		}
		if (glueSwitch==0 && StringUtils.isBlank(handler_name)) {
			return new ReturnT<String>(500, "请输入“执行器”");
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (alarmThreshold < 0) {
			alarmThreshold = 0;
		}
		
		try {
			if (DynamicSchedulerUtil.checkExists(jobName, jobGroup)) {
				return new ReturnT<String>(500, "此任务已存在，请更换任务组或任务名");
			}
		} catch (SchedulerException e1) {
			e1.printStackTrace();
			return new ReturnT<String>(500, "此任务已存在，请更换任务组或任务名");
		}
		
		// parse jobDataMap
		HashMap<String, String> jobDataMap = new HashMap<String, String>();
		jobDataMap.put(HandlerRepository.HANDLER_ADDRESS, handler_address);
		jobDataMap.put(HandlerRepository.HANDLER_NAME, handler_name);
		jobDataMap.put(HandlerRepository.HANDLER_PARAMS, handler_params);
		
		// Backup to the database
		XxlJobInfo jobInfo = new XxlJobInfo();
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setJobName(jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setJobClass(RemoteHttpJobBean.class.getName());
		jobInfo.setJobData(JacksonUtil.writeValueAsString(jobDataMap));
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setAlarmThreshold(alarmThreshold);
		jobInfo.setGlueSwitch(glueSwitch);
		jobInfo.setGlueSource(null);
		jobInfo.setGlueRemark(null);
		xxlJobInfoDao.save(jobInfo);
		
		try {
			// add job 2 quartz
			boolean result = DynamicSchedulerUtil.addJob(jobInfo);
			if (result) {
				return ReturnT.SUCCESS;
			} else {
				xxlJobInfoDao.delete(jobGroup, jobName);
				return new ReturnT<String>(500, "新增任务失败");
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/reschedule")
	@ResponseBody
	public ReturnT<String> reschedule(String jobGroup, String jobName, String jobCron, String jobDesc,
			String handler_address, String handler_name, String handler_params, 
			String author, String alarmEmail, int alarmThreshold, int glueSwitch) {
		
		// valid
		if (JobGroupEnum.match(jobGroup) == null) {
			return new ReturnT<String>(500, "请选择“任务组”");
		}
		if (StringUtils.isBlank(jobName)) {
			return new ReturnT<String>(500, "请输入“任务名”");
		}
		if (!CronExpression.isValidExpression(jobCron)) {
			return new ReturnT<String>(500, "“corn”不合法");
		}
		if (StringUtils.isBlank(jobDesc)) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		if (StringUtils.isBlank(handler_address)) {
			return new ReturnT<String>(500, "请输入“机器地址”");
		}
		if (glueSwitch==0 && StringUtils.isBlank(handler_name)) {
			return new ReturnT<String>(500, "请输入“执行器”");
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
		}
		if (alarmThreshold < 0) {
			alarmThreshold = 0;
		}
		
		// parse jobDataMap
		HashMap<String, String> jobDataMap = new HashMap<String, String>();
		jobDataMap.put(HandlerRepository.HANDLER_ADDRESS, handler_address);
		jobDataMap.put(HandlerRepository.HANDLER_NAME, handler_name);
		jobDataMap.put(HandlerRepository.HANDLER_PARAMS, handler_params);
		
		XxlJobInfo jobInfo = xxlJobInfoDao.load(jobGroup, jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setJobData(JacksonUtil.writeValueAsString(jobDataMap));
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setAlarmThreshold(alarmThreshold);
		jobInfo.setGlueSwitch(glueSwitch);
		
		try {
			// fresh quartz
			DynamicSchedulerUtil.rescheduleJob(jobInfo);
			
			// fresh db
			xxlJobInfoDao.update(jobInfo);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.removeJob(jobName, jobGroup);
			xxlJobInfoDao.delete(jobGroup, jobName);
			xxlJobLogDao.delete(jobGroup, jobName);
			xxlJobLogGlueDao.delete(jobGroup, jobName);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.pauseJob(jobName, jobGroup);	// jobStatus do not store
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<String> resume(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.resumeJob(jobName, jobGroup);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(String jobGroup, String jobName) {
		try {
			DynamicSchedulerUtil.triggerJob(jobName, jobGroup);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
}

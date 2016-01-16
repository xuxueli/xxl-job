package com.xxl.job.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import com.xxl.job.core.constant.Constants.JobGroupEnum;
import com.xxl.job.core.model.ReturnT;
import com.xxl.job.core.model.XxlJobInfo;
import com.xxl.job.core.util.DynamicSchedulerUtil;
import com.xxl.job.dao.IXxlJobInfoDao;
import com.xxl.job.service.job.RemoteHttpJobBean;
import com.xxl.job.service.job.impl.DemoConcurrentJobBean;
import com.xxl.job.service.job.impl.DemoNomalJobBean;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {
	
	@Resource
	private IXxlJobInfoDao xxlJobInfoDao;
	
	// remote job bean
	public static Class <? extends Job> remoteJobBean = RemoteHttpJobBean.class;
	// loacal job bean
	public static List<Class <? extends Job>> localJobBeanList = new ArrayList<Class<? extends Job>>();
	static{
		localJobBeanList.add((Class<? extends Job>) DemoNomalJobBean.class);
		localJobBeanList.add((Class<? extends Job>) DemoConcurrentJobBean.class);
	}
	
	@RequestMapping
	public String index(Model model) {
		model.addAttribute("localJobBeanList", localJobBeanList);			// 本地任务-列表
		model.addAttribute("remoteJobBean", remoteJobBean);	// 远程任务-jobBean
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
	public ReturnT<String> add(String jobGroup, String jobName, String jobCron, String jobDesc, String jobClass,
			String handler_params, String handler_address, String handler_name, 
			String author, String alarmEmail, int alarmThreshold) {
		
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
		Class<? extends Job> jobClass_ = null;
		try {
			Class<?> clazz = Class.forName(jobClass);
			if (clazz!=null) {
				jobClass_ = (Class<? extends Job>) clazz;
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		if (jobClass_ == null) {
			return new ReturnT<String>(500, "请选择“JobBean”");
		}
		if (jobClass_.getClass().getName().equals(remoteJobBean.getName())) {
			if (StringUtils.isBlank(handler_address)) {
				return new ReturnT<String>(500, "请输入“远程-机器地址”");
			}
			if (StringUtils.isBlank(handler_name)) {
				return new ReturnT<String>(500, "请输入“远程-执行器”");
			}
		}
		if (StringUtils.isBlank(author)) {
			return new ReturnT<String>(500, "请输入“负责人”");
		}
		if (StringUtils.isBlank(alarmEmail)) {
			return new ReturnT<String>(500, "请输入“报警邮件”");
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
		jobDataMap.put(HandlerRepository.HANDLER_PARAMS, handler_params);
		jobDataMap.put(HandlerRepository.HANDLER_ADDRESS, handler_address);
		jobDataMap.put(HandlerRepository.HANDLER_NAME, handler_name);
		
		// Backup to the database
		XxlJobInfo jobInfo = new XxlJobInfo();
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setJobName(jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setJobClass(jobClass);
		jobInfo.setJobData(JacksonUtil.writeValueAsString(jobDataMap));
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setAlarmThreshold(alarmThreshold);
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
	public ReturnT<String> reschedule(String jobGroup, String jobName, String jobCron, String jobDesc, String jobClass,
			String handler_params, String handler_address, String handler_name, 
			String author, String alarmEmail, int alarmThreshold) {
		
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
		
		// parse jobDataMap
		HashMap<String, String> jobDataMap = new HashMap<String, String>();
		jobDataMap.put(HandlerRepository.HANDLER_PARAMS, handler_params);
		jobDataMap.put(HandlerRepository.HANDLER_ADDRESS, handler_address);
		jobDataMap.put(HandlerRepository.HANDLER_NAME, handler_name);
		
		XxlJobInfo jobInfo = xxlJobInfoDao.load(jobGroup, jobName);
		jobInfo.setJobCron(jobCron);
		jobInfo.setJobDesc(jobDesc);
		jobInfo.setJobData(JacksonUtil.writeValueAsString(jobDataMap));
		jobInfo.setAuthor(author);
		jobInfo.setAlarmEmail(alarmEmail);
		jobInfo.setAlarmThreshold(alarmThreshold);
		
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

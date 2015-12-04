package com.xxl.controller;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.quartz.DynamicSchedulerUtil;
import com.xxl.quartz.ReturnT;

@Controller
@RequestMapping("/job")
public class IndexController {

	
	@RequestMapping("/index")
	public String index(Model model) {
		List<Map<String, Object>> jobList = DynamicSchedulerUtil.getJobList();
		model.addAttribute("jobList", jobList);
		return "job/index";
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(String triggerKeyName, String cronExpression, String jobClassName, String jobDesc) {
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
		// jobClassName
		Class<?> clazz = null;
		try {
			clazz = Class.forName(jobClassName);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		if (clazz == null) {
			return new ReturnT<String>(500, "请输入“任务Impl”");
		}
		if (!Job.class.isAssignableFrom(clazz)) {
			return new ReturnT<String>(500, "“任务Impl”类必须继承Job接口");
		}
		@SuppressWarnings("unchecked")
		Class<? extends Job> jobClass = (Class<? extends Job>)clazz;
		// jobDesc
		if (StringUtils.isBlank(jobDesc)) {
			return new ReturnT<String>(500, "请输入“任务描述”");
		}
		try {
			Map<String, Object> jobData = new HashMap<String, Object>();
			jobData.put(DynamicSchedulerUtil.job_desc, jobDesc);
			DynamicSchedulerUtil.addJob(triggerKeyName, cronExpression, jobClass, jobData);
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
			DynamicSchedulerUtil.removeJob(triggerKeyName);
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(String triggerKeyName) {
		try {
			DynamicSchedulerUtil.pauseJob(triggerKeyName);
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
			return ReturnT.SUCCESS;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
	}
	
	@RequestMapping("/help")
	public String help(Model model) {
		return "job/help";
	}
	
	private int simpleParam = 0;
	private ThreadLocal<Integer> tlParam;
	
	@RequestMapping("/beat")
	@ResponseBody
	public String beat() {
		if (tlParam == null) {
			tlParam = new ThreadLocal<Integer>();
		}
		if (tlParam.get() == null) {
			tlParam.set(5000);
		}
		simpleParam++;
		tlParam.set(tlParam.get() + 1);
		
		long start = System.currentTimeMillis();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		return MessageFormat.format("cost:{0}, hashCode:{1}, simpleParam:{2}, tlParam:{3}", 
				(end - start), this.hashCode(), simpleParam, tlParam.get());
	}
	
	
	public static void main(String[] args) {
		Runnable runa = new Runnable() {
			private int simInt = 0;
			private ThreadLocal<Integer> tlParam = new ThreadLocal<Integer>();
			@Override
			public void run() {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (tlParam.get() == null) {
						tlParam.set(0);
					}
					simInt++;
					tlParam.set(tlParam.get()+1);
					System.out.println(Thread.currentThread().hashCode() + ":simInt:" + simInt);
					System.out.println(Thread.currentThread().hashCode() + ":tlParam:" + tlParam.get());
				}
			}
		};
		
		Thread t1 = new Thread(runa);
		Thread t2 = new Thread(runa);
		t1.start();
		t2.start();
	}
}

package com.xxl.job.admin.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.core.model.ReturnT;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.thread.JobRegistryHelper;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.service.IXxlJobService;
import com.xxl.job.core.registry.RegistHelper;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/api")
public class ApiController {

	@Resource
	private IXxlJobGroupDao xxlJobGroupDao;
	@Resource
	private IXxlJobService xxlJobService;
	
	@RequestMapping("/jobgroup/list")
	@ResponseBody
	public List<XxlJobGroup> index(Model model) {

		// 任务组
		List<XxlJobGroup> list = xxlJobGroupDao.findAll();

		if (CollectionUtils.isNotEmpty(list)) {
			for (XxlJobGroup group: list) {
				List<String> registryList = JobRegistryHelper.discover(RegistHelper.RegistType.EXECUTOR.name(), group.getAppName());
				group.setRegistryList(registryList);
			}
		}
		return list;
	}
	
	@RequestMapping("/job/list")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, String executorHandler, String filterTime) {
		
		return xxlJobService.pageList(start, length, jobGroup, executorHandler, filterTime);
	}
	
	@RequestMapping("/job/trigger")
	@ResponseBody
	public ReturnT<String> triggerJob(int jobGroup, String jobName,String executorParam) {
		
		return xxlJobService.triggerJob(jobGroup, jobName,executorParam);
	}
	
}

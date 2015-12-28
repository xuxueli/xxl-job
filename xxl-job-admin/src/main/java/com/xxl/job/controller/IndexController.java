package com.xxl.job.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xxl.job.core.util.DynamicSchedulerUtil;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

	@RequestMapping("")
	public String index(Model model) {
		List<Map<String, Object>> jobList = DynamicSchedulerUtil.getJobList();
		model.addAttribute("jobList", jobList);
		return "job/index";
	}
	
	@RequestMapping("/help")
	public String help(Model model) {
		return "job/help";
	}
	
}

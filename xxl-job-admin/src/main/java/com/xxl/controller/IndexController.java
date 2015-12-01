package com.xxl.controller;

import java.util.Set;

import org.quartz.JobKey;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xxl.quartz.DynamicSchedulerUtil;

@Controller
@RequestMapping("/job")
public class IndexController {

	
	@RequestMapping("index")
	public String index(Model model) {
		Set<JobKey> list = DynamicSchedulerUtil.getJobKeys();
		model.addAttribute("jobList", list);
		return "job/index";
	}
	
	@RequestMapping("/help")
	public String help(Model model) {
		return "job/help";
	}
	
}

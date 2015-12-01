package com.xxl.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xxl.quartz.DynamicSchedulerUtil;

@Controller
@RequestMapping("/job")
public class IndexController {

	
	@RequestMapping("index")
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

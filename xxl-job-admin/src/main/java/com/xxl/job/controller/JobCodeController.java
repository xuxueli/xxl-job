package com.xxl.job.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.core.model.ReturnT;
import com.xxl.job.core.model.XxlJobInfo;

/**
 * job code controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobcode")
public class JobCodeController {

	@RequestMapping
	public String index(Model model, HttpServletRequest request) {
		return "jobcode/index";
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(Model model, XxlJobInfo jobInfo, HttpServletRequest request) {
		
		return ReturnT.SUCCESS;
	}
	
}

package com.xxl.job.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
public class IndexController {

	@RequestMapping("/")
	public String index(Model model) {
		return "redirect:job";
	}
	
	@RequestMapping("/help")
	public String help(Model model) {
		return "help";
	}
	
}

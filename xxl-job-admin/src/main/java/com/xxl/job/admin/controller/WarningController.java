package com.xxl.job.admin.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.service.WarningService;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;

/**
 * Warning configuration maintains
 * 
 * @author Locki 2019-11-14 17:57:16
 *
 */
@Controller
@RequestMapping("/warninfo")
public class WarningController {
	@Resource
	private WarningService warningService;
	
	@RequestMapping
	public String index() {
		return "warninfo/warninfo.index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			String warningName, String warningParam, String warningDesc) {
		
		return warningService.pageList(start, length, warningName, warningParam, warningDesc);
	}
}

package com.xxl.job.admin.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.core.alarm.JobAlarmerEnum;
import com.xxl.job.admin.core.model.XxlAlarmInfo;
import com.xxl.job.admin.service.AlarmService;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * Alarm configuration maintains
 * 
 * @author Locki 2019-11-14 17:57:16
 *
 */
@Controller
@RequestMapping("/alarminfo")
public class AlarmController {
	@Resource
	private AlarmService alarmService;

	@RequestMapping
	public String index(HttpServletRequest request, Model model) {
		// 枚举-字典
		model.addAttribute("JobAlarmerEnum", JobAlarmerEnum.values()); // 报警方式-列表
		return "alarminfo/alarminfo.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length, String alarmEnum, String alarmName, String alarmDesc) {

		return alarmService.pageList(start, length, alarmEnum, alarmName, alarmDesc);
	}
	
	@RequestMapping("/addOrUpdate")
	@ResponseBody
	public ReturnT<String> add(XxlAlarmInfo alarmInfo) {
		if(alarmInfo.getId() > 0) {
			return alarmService.update(alarmInfo);
		}else {
			return alarmService.add(alarmInfo);
		}
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(long id){
		return alarmService.remove(id);
	}
}

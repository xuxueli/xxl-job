package com.xxl.job.admin.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.controller.annotation.PermessionLimit;
import com.xxl.job.admin.core.bean.CallBackBean;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.thread.JobFailMonitorHelper;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;

@Controller
@RequestMapping("/irms")
public class IRMSApiController {
	
	@Resource
	public XxlJobLogDao xxlJobLogDao;
	
	@Resource
	public XxlJobService xxlJobService;

	@RequestMapping(value = "/callback/{taskId}",method = RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> callback(@PathVariable(value = "taskId") int taskId, @RequestBody CallBackBean bean) {
		if (bean == null)
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请求失败,callback 对象为 null");

		String msg;
		try {
			XxlJobLog jobLog = xxlJobLogDao.load(taskId);
			StringBuffer handleMsg = new StringBuffer(jobLog.getHandleMsg());
			handleMsg.append("<div style=\"clear:both;\"></div>");
			handleMsg.append("========================callback 分隔符==========================");
			handleMsg.append("<div style=\"clear:both;\"></div>");
			handleMsg.append(bean.getErrorMessage());

			jobLog.setHandleMsg(handleMsg.toString());
			Integer code = bean.isSuccess() ? 200 : 500;
			jobLog.setHandleCode(code);
			jobLog.setHandleTime(new Date());
			xxlJobLogDao.updateHandleInfo(jobLog);
			
			JobFailMonitorHelper.monitor(jobLog.getId());
			
			return new ReturnT<String>(ReturnT.SUCCESS_CODE, "请求成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		return new ReturnT<String>(ReturnT.FAIL_CODE, msg);
	}
	
	@RequestMapping(value = "/monitor",method = RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> monitor(@RequestBody Map<String, String> map) {
		return xxlJobService.monitor(map.get("emails"));
	}
	
}

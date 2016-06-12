package com.xxl.job.admin.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.admin.core.constant.Constants.JobGroupEnum;
import com.xxl.job.admin.core.model.ReturnT;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.core.handler.HandlerRepository.ActionEnum;
import com.xxl.job.core.handler.HandlerRepository.HandlerParamEnum;
import com.xxl.job.core.util.HttpUtil;
import com.xxl.job.core.util.HttpUtil.RemoteCallBack;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {

	@Resource
	public IXxlJobLogDao xxlJobLogDao;
	@Resource
	public IXxlJobInfoDao xxlJobInfoDao;
	
	@RequestMapping
	public String index(Model model, String jobGroup, String jobName) {
		model.addAttribute("jobGroup", jobGroup);
		model.addAttribute("jobName", jobName);
		model.addAttribute("JobGroupList", JobGroupEnum.values());
		return "joblog/joblog.index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			String jobGroup, String jobName, String filterTime) {
		
		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (StringUtils.isNotBlank(filterTime)) {
			String[] temp = filterTime.split(" - ");
			if (temp!=null && temp.length == 2) {
				try {
					triggerTimeStart = DateUtils.parseDate(temp[0], new String[]{"yyyy-MM-dd HH:mm:ss"});
					triggerTimeEnd = DateUtils.parseDate(temp[1], new String[]{"yyyy-MM-dd HH:mm:ss"});
				} catch (ParseException e) {	}
			}
		}
		
		// page query
		List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobName, triggerTimeStart, triggerTimeEnd);
		int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobName, triggerTimeStart, triggerTimeEnd);
		
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
	    maps.put("recordsTotal", list_count);		// 总记录数
	    maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
	    maps.put("data", list);  					// 分页列表
		return maps;
	}
	
	/*@RequestMapping("/save")
	@ResponseBody
	@PermessionLimit(limit=false)
	public RemoteCallBack triggerLog(int trigger_log_id, String status, String msg) {
		RemoteCallBack callBack = new RemoteCallBack();
		callBack.setStatus(RemoteCallBack.FAIL);
		XxlJobLog log = xxlJobLogDao.load(trigger_log_id);
		if (log!=null) {
			log.setHandleTime(new Date());
			log.setHandleStatus(status);
			log.setHandleMsg(msg);
			xxlJobLogDao.updateHandleInfo(log);
			callBack.setStatus(RemoteCallBack.SUCCESS);
			return callBack;
		}
		return callBack;
	}*/
	
	@RequestMapping("/logDetail")
	@ResponseBody
	public ReturnT<String> logDetail(int id){
		// base check
		XxlJobLog log = xxlJobLogDao.load(id);
		if (log == null) {
			return new ReturnT<String>(500, "参数异常");
		}
		if (!RemoteCallBack.SUCCESS.equals(log.getTriggerStatus())) {
			return new ReturnT<String>(500, "调度失败，无法查看执行日志");
		}
		
		// trigger id, trigger time
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put(HandlerParamEnum.TIMESTAMP.name(), String.valueOf(System.currentTimeMillis()));
		reqMap.put(HandlerParamEnum.ACTION.name(), ActionEnum.LOG.name());
		reqMap.put(HandlerParamEnum.LOG_ID.name(), String.valueOf(id));
		reqMap.put(HandlerParamEnum.LOG_DATE.name(), String.valueOf(log.getTriggerTime().getTime()));
		
		RemoteCallBack callBack = HttpUtil.post(HttpUtil.addressToUrl(log.getExecutorAddress()), reqMap);
		if (HttpUtil.RemoteCallBack.SUCCESS.equals(callBack.getStatus())) {
			return new ReturnT<String>(callBack.getMsg());
		} else {
			return new ReturnT<String>(500, callBack.getMsg());
		}
	}
	
	@RequestMapping("/logDetailPage")
	public String logDetailPage(int id, Model model){
		ReturnT<String> data = logDetail(id);
		model.addAttribute("result", data);
		return "joblog/logdetail";
	}
	
	@RequestMapping("/logKill")
	@ResponseBody
	public ReturnT<String> logKill(int id){
		// base check
		XxlJobLog log = xxlJobLogDao.load(id);
		XxlJobInfo jobInfo = xxlJobInfoDao.load(log.getJobGroup(), log.getJobName());
		if (log == null || jobInfo==null) {
			return new ReturnT<String>(500, "参数异常");
		}
		if (!RemoteCallBack.SUCCESS.equals(log.getTriggerStatus())) {
			return new ReturnT<String>(500, "调度失败，无法终止日志");
		}
		
		// request
		Map<String, String> reqMap = new HashMap<String, String>();
		reqMap.put(HandlerParamEnum.TIMESTAMP.name(), String.valueOf(System.currentTimeMillis()));
		reqMap.put(HandlerParamEnum.ACTION.name(), ActionEnum.KILL.name());
		reqMap.put(HandlerParamEnum.GLUE_SWITCH.name(), String.valueOf(jobInfo.getGlueSwitch()));
		reqMap.put(HandlerParamEnum.EXECUTOR_HANDLER.name(), log.getExecutorHandler());
		reqMap.put(HandlerParamEnum.JOB_GROUP.name(), log.getJobGroup());
		reqMap.put(HandlerParamEnum.JOB_NAME.name(), log.getJobName());
		
		RemoteCallBack callBack = HttpUtil.post(HttpUtil.addressToUrl(log.getExecutorAddress()), reqMap);
		if (HttpUtil.RemoteCallBack.SUCCESS.equals(callBack.getStatus())) {
			log.setHandleStatus(HttpUtil.RemoteCallBack.FAIL);
			log.setHandleMsg("人为操作主动终止");
			log.setHandleTime(new Date());
			xxlJobLogDao.updateHandleInfo(log);
			return new ReturnT<String>(callBack.getMsg());
		} else {
			return new ReturnT<String>(500, callBack.getMsg());
		}
	}
}

package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.dao.IXxlJobGroupDao;
import com.xxl.job.admin.dao.IXxlJobInfoDao;
import com.xxl.job.admin.dao.IXxlJobLogDao;
import com.xxl.job.admin.utils.MsgUtils;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.rpc.netcom.NetComClientProxy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {

	@Resource
	private IXxlJobGroupDao xxlJobGroupDao;
	@Resource
	public IXxlJobInfoDao xxlJobInfoDao;
	@Resource
	public IXxlJobLogDao xxlJobLogDao;

	@RequestMapping
	public String index(Model model, String jobGroup, String jobName) {

		// 任务组
		List<XxlJobGroup> jobGroupList =  xxlJobGroupDao.findAll();

		model.addAttribute("jobGroup", jobGroup);
		model.addAttribute("jobName", jobName);
		model.addAttribute("JobGroupList", jobGroupList);
		return "joblog/joblog.index";
	}

	@RequestMapping("/getJobsByGroup")
	@ResponseBody
	public ReturnT<List<XxlJobLog>> listJobByGroup(String jobGroup){
		List<XxlJobLog> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
		return new ReturnT<List<XxlJobLog>>(list);
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, String jobName, String filterTime) {
		
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
	
	@RequestMapping("/logDetail")
	@ResponseBody
	public ReturnT<String> logDetail(int id){
		// base check
		XxlJobLog log = xxlJobLogDao.load(id);
		if (log == null) {
			return new ReturnT<String>(500, "查看执行日志失败: 参数异常");
		}
		if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
			return new ReturnT<String>(500, "查看执行日志失败: 任务发起调度失败，无法查看执行日志");
		}
		
		// trigger id, trigger time
		ExecutorBiz executorBiz = null;
		try {
			executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, log.getExecutorAddress()).getObject();
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnT<String>(500, e.getMessage());
		}
		ReturnT<String> logResult = executorBiz.log(log.getTriggerTime().getTime(), id);

		if (ReturnT.SUCCESS_CODE == logResult.getCode()) {
			return new ReturnT<String>(logResult.getMsg());
		} else {
			return new ReturnT<String>(500, "查看执行日志失败: " + logResult.getMsg());
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
		if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
			return new ReturnT<String>(500, "调度失败，无法终止日志");
		}

		// request of kill
		ExecutorBiz executorBiz = null;
		try {
			executorBiz = (ExecutorBiz) new NetComClientProxy(ExecutorBiz.class, log.getExecutorAddress()).getObject();
		} catch (Exception e) {
			e.printStackTrace();
			return new ReturnT<String>(500, e.getMessage());
		}
		ReturnT<String> runResult = executorBiz.kill(String.valueOf(log.getJobGroup()), log.getJobName());

		if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
			log.setHandleCode(ReturnT.FAIL_CODE);
			log.setHandleMsg("人为操作主动终止:" + (runResult.getMsg()!=null?runResult.getMsg():""));
			log.setHandleTime(new Date());
			MsgUtils.whenHandleMsgOverSize(log);
			xxlJobLogDao.updateHandleInfo(log);
			return new ReturnT<String>(runResult.getMsg());
		} else {
			return new ReturnT<String>(500, runResult.getMsg());
		}
	}
}

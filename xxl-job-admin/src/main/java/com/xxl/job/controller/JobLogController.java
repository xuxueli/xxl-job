package com.xxl.job.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxl.job.core.model.ReturnT;
import com.xxl.job.core.model.XxlJobLog;
import com.xxl.job.dao.IXxlJobLogDao;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {

	@Resource
	public IXxlJobLogDao xxlJobLogDao;
	
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> triggerLog(int triggerLogId, String status, String msg) {
		XxlJobLog log = xxlJobLogDao.load(triggerLogId);
		if (log!=null) {
			log.setHandleTime(new Date());
			log.setHandleStatus(status);
			log.setHandleMsg(msg);
			xxlJobLogDao.updateHandleInfo(log);
			return ReturnT.SUCCESS;
		}
		return ReturnT.FAIL;
	}
	
	@RequestMapping("/")
	public String index(Model model) {
		return "joblog/index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false) String jobName,  
		      @RequestParam(required = false, defaultValue = "0") int start,  
		      @RequestParam(required = false, defaultValue = "10") int length) {
		
		System.out.println(start);
		System.out.println(length);
		System.out.println(jobName);
		
		List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobName);
		int list_count = xxlJobLogDao.pageListCount(start, length, jobName);
		
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("draw", list_count);			// 请求次数
	    maps.put("recordsTotal", list_count);	// 总记录数
	    maps.put("recordsFiltered", list_count);// 过滤后的总记录数
	    maps.put("data", list);  				// 分页列表
		return maps;
	}
	
}

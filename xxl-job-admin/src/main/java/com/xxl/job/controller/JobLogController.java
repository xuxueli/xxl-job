package com.xxl.job.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
}

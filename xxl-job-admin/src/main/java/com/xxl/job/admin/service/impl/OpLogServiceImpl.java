package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.core.model.XxlJobOpLog;
import com.xxl.job.admin.core.util.CurrentUserUtil;
import com.xxl.job.admin.dao.XxlJobOpLogDao;
import com.xxl.job.admin.service.OpLogService;
import com.xxl.job.core.util.GsonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * operation log service
 */
@Service
public class OpLogServiceImpl implements OpLogService {
	private static Logger logger = LoggerFactory.getLogger(OpLogServiceImpl.class);

	@Resource
	private XxlJobOpLogDao xxlJobOpLogDao;

	@Override
	public void addLog(String logType, Object oldVal, Object newVal, String description) {
		String currentUserName = CurrentUserUtil.getCurrentUserName();
		this.addLog(logType, GsonTool.toJson(oldVal), GsonTool.toJson(newVal),description,currentUserName);
	}

	@Override
	public void addLog(String logType) {
		String currentUserName = CurrentUserUtil.getCurrentUserName();
		this.addLog(logType, currentUserName);
	}

	@Override
	public void addLog(String logType, String userName) {
		this.addLog(logType, null,null,"",userName);
	}

	private void addLog(String logType, String oldVal, String newVal, String description,String userName) {
		XxlJobOpLog operateLog = new XxlJobOpLog();
		operateLog.setLogType(logType);
		operateLog.setDescription(description);
		operateLog.setOldVal(oldVal);
		operateLog.setNewVal(newVal);
		operateLog.setCreateUser(userName);
		xxlJobOpLogDao.save(operateLog);
	}
}

package com.xxl.job.admin.controller;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.xxl.job.admin.core.complete.XxlJobCompleter;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.*;
import com.xxl.job.admin.core.scheduler.XxlJobScheduler;
import com.xxl.job.admin.core.thread.JobLogReportHelper;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.*;
import com.xxl.job.core.biz.ExecutorBiz;
import com.xxl.job.core.biz.model.*;
import com.xxl.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {

	private static final Logger logger = LoggerFactory.getLogger(JobLogController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	public XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {

		// 执行器列表
		List<XxlJobGroup> jobGroupList_all = xxlJobGroupDao.findAll();

		// filter group
		List<XxlJobGroup> jobGroupList = JobInfoController.filterJobGroupByRole(request, jobGroupList_all);
		if (jobGroupList.isEmpty()) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		model.addAttribute("JobGroupList", jobGroupList);

		// 任务
		if (jobId > 0) {
			XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
			if (jobInfo == null) {
				throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
			}

			model.addAttribute("jobInfo", jobInfo);

			// valid permission
			JobInfoController.validPermission(request, jobInfo.getJobGroup());
		}

		return "joblog/joblog.index";
	}

	@RequestMapping("/getJobsByGroup")
	@ResponseBody
	public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
		List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
		return new ReturnT<>(list);
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(HttpServletRequest request,
	                                    @RequestParam(required = false, defaultValue = "0") int start,
	                                    @RequestParam(required = false, defaultValue = "10") int length,
	                                    int jobGroup, int jobId, int logStatus, String filterTime) {

		// valid permission
		JobInfoController.validPermission(request, jobGroup);    // 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup

		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (StringUtils.hasText(filterTime)) {
			String[] temp = filterTime.split(" - ");
			if (temp.length == 2) {
				triggerTimeStart = DateUtil.parseDateTime(temp[0]);
				triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
			}
		}

		// page query
		List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

		// package result
		Map<String, Object> maps = new HashMap<>(4, 1F);
		maps.put("recordsTotal", list_count);        // 总记录数
		maps.put("recordsFiltered", list_count);    // 过滤后的总记录数
		maps.put("data", list);                    // 分页列表
		return maps;
	}

	@RequestMapping("/logDetailPage")
	public String logDetailPage(long id, Model model) {
		// base check
		XxlJobLog jobLog = xxlJobLogDao.load(id);
		if (jobLog == null) {
			throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
		}

		model.addAttribute("triggerCode", jobLog.getTriggerCode());
		model.addAttribute("handleCode", jobLog.getHandleCode());
		model.addAttribute("logId", jobLog.getId());
		return "joblog/joblog.detail";
	}

	@RequestMapping("/logDetailCat")
	@ResponseBody
	public ReturnT<LogResult> logDetailCat(long logId, int fromLineNum) {
		try {
			// valid
			XxlJobLog jobLog = xxlJobLogDao.load(logId);    // TODO, need to improve performance
			if (jobLog == null) {
				return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_logid_unvalid"));
			}

			// log cat
			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(jobLog.getExecutorAddress());
			ReturnT<LogResult> logResult = executorBiz.log(new LogParam(jobLog.getTriggerTime().getTime(), logId, fromLineNum));

			// is end
			final LogResult content = logResult.getContent();
			if (content != null && content.getFromLineNum() > content.getToLineNum()) {
				if (jobLog.getHandleCode() > 0) {
					content.setEnd(true);
				}
			}

			// fix xss
			if (content != null && StringUtils.hasText(content.getLogContent())) {
				String newLogContent = HtmlUtils.htmlEscape(content.getLogContent(), "UTF-8");
				content.setLogContent(newLogContent);
			}

			return logResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
		}
	}

	@RequestMapping("/logKill")
	@ResponseBody
	public ReturnT<String> logKill(long id) {
		// base check
		XxlJobLog log = xxlJobLogDao.load(id);
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
		if (jobInfo == null) {
			return new ReturnT<>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (ReturnT.SUCCESS_CODE != log.getTriggerCode()) {
			return new ReturnT<>(500, I18nUtil.getString("joblog_kill_log_limit"));
		}

		// request of kill
		ReturnT<String> runResult;
		try {
			ExecutorBiz executorBiz = XxlJobScheduler.getExecutorBiz(log.getExecutorAddress());
			runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			runResult = new ReturnT<>(500, e.getMessage());
		}

		if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
			log.setHandleCode(ReturnT.FAIL_CODE);
			log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
			log.setHandleTime(new Date());
			XxlJobCompleter.updateHandleInfoAndFinish(log);
			return new ReturnT<>(runResult.getMsg());
		} else {
			return new ReturnT<>(500, runResult.getMsg());
		}
	}

	@RequestMapping("/clearLog")
	@ResponseBody
	public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {

		Date clearBeforeTime = null;
		int clearBeforeNum = 0;
		if (type == 1) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -1);	// 清理一个月之前日志数据
		} else if (type == 2) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -3);	// 清理三个月之前日志数据
		} else if (type == 3) {
			clearBeforeTime = DateUtil.addMonths(new Date(), -6);	// 清理六个月之前日志数据
		} else if (type == 4) {
			clearBeforeTime = DateUtil.addYears(new Date(), -1);	// 清理一年之前日志数据
		} else if (type == 5) {
			clearBeforeNum = 1000;		// 清理一千条以前日志数据
		} else if (type == 6) {
			clearBeforeNum = 10000;		// 清理一万条以前日志数据
		} else if (type == 7) {
			clearBeforeNum = 30000;		// 清理三万条以前日志数据
		} else if (type == 8) {
			clearBeforeNum = 100000;	// 清理十万条以前日志数据
		} else if (type == 9) {
			// 清理所有日志数据
		} else {
			return new ReturnT<>(ReturnT.FAIL_CODE, I18nUtil.getString("joblog_clean_type_unvalid"));
		}

		JobLogReportHelper.clearLogs(xxlJobLogDao, jobGroup, jobId, clearBeforeTime, clearBeforeNum);

		return ReturnT.SUCCESS;
	}
}
package com.xxl.job.admin.controller.biz;

import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.mapper.XxlJobLogMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.scheduler.exception.XxlJobException;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.util.I18nUtil;
import com.xxl.job.admin.util.JobGroupPermissionUtil;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.openapi.ExecutorBiz;
import com.xxl.job.core.openapi.model.KillRequest;
import com.xxl.job.core.openapi.model.LogRequest;
import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import com.xxl.tool.response.Response;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

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
	private static final Logger logger = LoggerFactory.getLogger(JobLogController.class);

	@Resource
	private XxlJobGroupMapper xxlJobGroupMapper;
	@Resource
	public XxlJobInfoMapper xxlJobInfoMapper;
	@Resource
	public XxlJobLogMapper xxlJobLogMapper;
    @Autowired
    private XxlJobService xxlJobService;

	@RequestMapping
	public String index(HttpServletRequest request,
						Model model,
						@RequestParam(value = "jobGroup", required = false, defaultValue = "0") Integer jobGroup,
						@RequestParam(value = "jobId", required = false, defaultValue = "0") Integer jobId) {

		// find all jobGroup
		List<XxlJobGroup> jobGroupListTotal =  xxlJobGroupMapper.findAll();

		// filter JobGroupList
		List<XxlJobGroup> jobGroupList = JobGroupPermissionUtil.filterJobGroupByPermission(request, jobGroupListTotal);
		if (CollectionTool.isEmpty(jobGroupList)) {
			throw new XxlJobException(I18nUtil.getString("jobgroup_empty"));
		}

		// parse jobGroup
		if (jobId > 0) {
			// assign jobId (+ jobGroup)
			XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(jobId);
			if (jobInfo == null) {
				// jobId not exist, inteceptor
				throw new RuntimeException(I18nUtil.getString("jobinfo_field_id") + I18nUtil.getString("system_unvalid"));
			}
			jobGroup = jobInfo.getJobGroup();
		} else if (jobGroup > 0) {
			// assign jobGroup
			Integer finalJobGroup = jobGroup;
			if (CollectionTool.isEmpty(jobGroupListTotal.stream().filter(item -> item.getId() == finalJobGroup).toList())) {
				// jobGroup not exist, use first
				jobGroup = jobGroupList.get(0).getId();
			}
			jobId = 0;
		} else {
			// default first valid jobGroup
			jobGroup = jobGroupList.get(0).getId();
			jobId = 0;
		}

		/*// valid permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);*/

		// find jobList
		List<XxlJobInfo> jobInfoList = xxlJobInfoMapper.getJobsByGroup(jobGroup);

		// parse jobId
		if (CollectionTool.isEmpty(jobInfoList)) {
			jobId = 0;
		} else {
			if (!jobInfoList.stream().map(XxlJobInfo::getId).toList().contains(jobId)) {
				// jobId not exist, use first
				jobId = jobInfoList.get(0).getId();
			}
		}

		// write
		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobInfoList", jobInfoList);
		model.addAttribute("jobGroup", jobGroup);
		model.addAttribute("jobId", jobId);

		return "biz/log.list";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Response<PageModel<XxlJobLog>> pageList(HttpServletRequest request,
										@RequestParam(required = false, defaultValue = "0") int offset,
										@RequestParam(required = false, defaultValue = "10") int pagesize,
										@RequestParam int jobGroup,
										@RequestParam int jobId,
										@RequestParam int logStatus,
										@RequestParam String filterTime) {

		// valid jobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

		// valid jobId
		if (jobId < 1) {
			return Response.ofFail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_job"));
		}

		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (StringTool.isNotBlank(filterTime)) {
			String[] temp = filterTime.split(" - ");
			if (temp.length == 2) {
				triggerTimeStart = DateTool.parseDateTime(temp[0]);
				triggerTimeEnd = DateTool.parseDateTime(temp[1]);
			}
		}
		
		// page query
		List<XxlJobLog> list = xxlJobLogMapper.pageList(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
		int list_count = xxlJobLogMapper.pageListCount(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

		// package result
		PageModel<XxlJobLog> pageModel = new PageModel<>();
		pageModel.setData(list);
		pageModel.setTotal(list_count);

		return Response.ofSuccess(pageModel);
	}

	/**
	 * filter xss tag
	 */
	private String filter(String originData){

		// exclude tag
		Map<String, String> excludeTagMap = new HashMap<String, String>();
		excludeTagMap.put("<br>", "###TAG_BR###");
		excludeTagMap.put("<b>", "###TAG_BOLD###");
		excludeTagMap.put("</b>", "###TAG_BOLD_END###");

		// replace
		for (String key : excludeTagMap.keySet()) {
			String value = excludeTagMap.get(key);
			originData = originData.replaceAll(key, value);
		}

		// htmlEscape
		originData = HtmlUtils.htmlEscape(originData, "UTF-8");

		// replace back
		for (String key : excludeTagMap.keySet()) {
			String value = excludeTagMap.get(key);
			originData = originData.replaceAll(value, key);
		}

		return originData;
	}

	@RequestMapping("/logKill")
	@ResponseBody
	public Response<String> logKill(HttpServletRequest request, @RequestParam("id") long id){
		// base check
		XxlJobLog log = xxlJobLogMapper.load(id);
		XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(log.getJobId());
		if (jobInfo==null) {
			return Response.ofFail(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (XxlJobContext.HANDLE_CODE_SUCCESS != log.getTriggerCode()) {
			return Response.ofFail( I18nUtil.getString("joblog_kill_log_limit"));
		}

		// valid JobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobInfo.getJobGroup());

		// request of kill
		Response<String> runResult = null;
		try {
			ExecutorBiz executorBiz = XxlJobAdminBootstrap.getExecutorBiz(log.getExecutorAddress());
			runResult = executorBiz.kill(new KillRequest(jobInfo.getId()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			runResult = Response.ofFail( e.getMessage());
		}

		if (XxlJobContext.HANDLE_CODE_SUCCESS == runResult.getCode()) {
			log.setHandleCode(XxlJobContext.HANDLE_CODE_FAIL);
			log.setHandleMsg( I18nUtil.getString("joblog_kill_log_byman")+":" + (runResult.getMsg()!=null?runResult.getMsg():""));
			log.setHandleTime(new Date());
			XxlJobAdminBootstrap.getInstance().getJobCompleter().complete(log);
			return Response.ofSuccess(runResult.getMsg());
		} else {
			return Response.ofFail(runResult.getMsg());
		}
	}

	@RequestMapping("/clearLog")
	@ResponseBody
	public Response<String> clearLog(HttpServletRequest request,
									@RequestParam("jobGroup") int jobGroup,
									@RequestParam("jobId") int jobId,
									@RequestParam("type") int type){
		// valid JobGroup permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobGroup);

		// valid jobId
		if (jobId < 1) {
			return Response.ofFail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_job"));
		}

		// opt
		Date clearBeforeTime = null;
		int clearBeforeNum = 0;
		if (type == 1) {
			clearBeforeTime = DateTool.addMonths(new Date(), -1);	// 清理一个月之前日志数据
		} else if (type == 2) {
			clearBeforeTime = DateTool.addMonths(new Date(), -3);	// 清理三个月之前日志数据
		} else if (type == 3) {
			clearBeforeTime = DateTool.addMonths(new Date(), -6);	// 清理六个月之前日志数据
		} else if (type == 4) {
			clearBeforeTime = DateTool.addYears(new Date(), -1);	// 清理一年之前日志数据
		} else if (type == 5) {
			clearBeforeNum = 1000;		// 清理一千条以前日志数据
		} else if (type == 6) {
			clearBeforeNum = 10000;		// 清理一万条以前日志数据
		} else if (type == 7) {
			clearBeforeNum = 30000;		// 清理三万条以前日志数据
		} else if (type == 8) {
			clearBeforeNum = 100000;	// 清理十万条以前日志数据
		} else if (type == 9) {
			clearBeforeNum = 0;			// 清理所有日志数据
		} else {
			return Response.ofFail(I18nUtil.getString("joblog_clean_type_unvalid"));
		}

		List<Long> logIds = null;
		do {
			logIds = xxlJobLogMapper.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
			if (logIds!=null && !logIds.isEmpty()) {
				xxlJobLogMapper.clearLog(logIds);
			}
		} while (logIds!=null && !logIds.isEmpty());

		return Response.ofSuccess();
	}

	@RequestMapping("/logDetailPage")
	public String logDetailPage(HttpServletRequest request, @RequestParam("id") long id, Model model){

		// base check
		XxlJobLog jobLog = xxlJobLogMapper.load(id);
		if (jobLog == null) {
			throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
		}

		// valid permission
		JobGroupPermissionUtil.validJobGroupPermission(request, jobLog.getJobGroup());

		// load jobInfo
		XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(jobLog.getJobId());

		// data
		model.addAttribute("triggerCode", jobLog.getTriggerCode());
		model.addAttribute("handleCode", jobLog.getHandleCode());
		model.addAttribute("logId", jobLog.getId());
		model.addAttribute("jobInfo", jobInfo);
		return "biz/log.detail";
	}

	@RequestMapping("/logDetailCat")
	@ResponseBody
	public Response<LogResult> logDetailCat(@RequestParam("logId") long logId, @RequestParam("fromLineNum") int fromLineNum){
		try {
			// valid
			XxlJobLog jobLog = xxlJobLogMapper.load(logId);	// todo, need to improve performance
			if (jobLog == null) {
				return Response.ofFail(I18nUtil.getString("joblog_logid_unvalid"));
			}

			// log cat
			ExecutorBiz executorBiz = XxlJobAdminBootstrap.getExecutorBiz(jobLog.getExecutorAddress());
			Response<LogResult> logResult = executorBiz.log(new LogRequest(jobLog.getTriggerTime().getTime(), logId, fromLineNum));

			// is end
			if (logResult.getData()!=null && logResult.getData().getFromLineNum() > logResult.getData().getToLineNum()) {
				if (jobLog.getHandleCode() > 0) {
					logResult.getData().setEnd(true);
				}
			}

			// fix xss
			if (logResult.getData()!=null && StringTool.isNotBlank(logResult.getData().getLogContent())) {
				String newLogContent = filter(logResult.getData().getLogContent());
				logResult.getData().setLogContent(newLogContent);
			}

			return logResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.ofFail(e.getMessage());
		}
	}

}

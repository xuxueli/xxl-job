package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.mapper.XxlJobLogMapper;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.core.service.JobLogService;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.openapi.ExecutorBiz;
import com.xxl.job.core.openapi.model.KillRequest;
import com.xxl.job.core.openapi.model.LogRequest;
import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.response.Response;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.function.Function;

/**
 * Job log service implementation for xxl-job core module.
 * Refactored from JobLogController to remove web-layer dependencies.
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
@Service
public class JobLogServiceImpl implements JobLogService {
    private static final Logger logger = LoggerFactory.getLogger(JobLogServiceImpl.class);

    @Resource
    private XxlJobLogMapper xxlJobLogMapper;
    @Resource
    private XxlJobInfoMapper xxlJobInfoMapper;

    @Override
    public PageModel<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, int logStatus, String filterTime) {
        // valid jobId
		/*if (jobId < 1) {
			return Response.ofFail(I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_job"));
		}*/

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
        int listCount = xxlJobLogMapper.pageListCount(offset, pagesize, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        // package result
        PageModel<XxlJobLog> pageModel = new PageModel<>();
        pageModel.setData(list);
        pageModel.setTotal(listCount);

        return pageModel;
    }

    @Override
    public XxlJobLog load(long id) {
        return xxlJobLogMapper.load(id);
    }

    @Override
    public XxlJobLog loadAndValidate(long id) throws XxlException {
        XxlJobLog jobLog = xxlJobLogMapper.load(id);
        if (jobLog == null) {
            throw new XxlException(I18nUtil.getString("joblog_logid_invalid"));
        }
        return jobLog;
    }

    @Override
    public Map<String, Object> getLogStatGraph(int jobGroup, int jobId, String fromTime, String toTime) {
        Date from = DateTool.parseDateTime(fromTime);
        Date to = DateTool.parseDateTime(toTime);
        return xxlJobLogMapper.findLogReport(from, to);
    }

    @Override
    public Response<String> kill(long id, Function<Integer, Boolean> groupPermissionCheck) {
        // base check
		XxlJobLog log = xxlJobLogMapper.load(id);
		XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(log.getJobId());
		if (jobInfo==null) {
			return Response.ofFail(I18nUtil.getString("jobinfo_glue_jobid_invalid"));
		}
		if (XxlJobContext.HANDLE_CODE_SUCCESS != log.getTriggerCode()) {
			return Response.ofFail( I18nUtil.getString("joblog_kill_log_limit"));
		}

		// valid JobGroup permission
        groupPermissionCheck.apply(jobInfo.getJobGroup());

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

    @Override
    public int clearLog(int jobGroup, int jobId, int type) {
        if (jobId < 1) {
			throw new XxlException(I18nUtil.getString("system_please_choose") + I18nUtil.getString("jobinfo_job"));
		}
        // determine clear time/num based on type
        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateTool.addMonths(new Date(), -1);
        } else if (type == 2) {
            clearBeforeTime = DateTool.addMonths(new Date(), -3);
        } else if (type == 3) {
            clearBeforeTime = DateTool.addMonths(new Date(), -6);
        } else if (type == 4) {
            clearBeforeTime = DateTool.addYears(new Date(), -1);
        } else if (type == 5) {
            clearBeforeNum = 1000;
        } else if (type == 6) {
            clearBeforeNum = 10000;
        } else if (type == 7) {
            clearBeforeNum = 30000;
        } else if (type == 8) {
            clearBeforeNum = 100000;
        } else if (type == 9) {
            clearBeforeNum = 0;
        } else {
            throw new XxlException(I18nUtil.getString("joblog_clean_type_invalid"));
        }

        // clear logs in batches
        List<Long> logIds;
        do {
            logIds = xxlJobLogMapper.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && !logIds.isEmpty()) {
                xxlJobLogMapper.clearLog(logIds);
            }
        } while (logIds != null && !logIds.isEmpty());

        return 1;
    }

    @Override
    public Response<LogResult> getLogDetailCat(long logId, int fromLineNum) throws XxlException {
        try {
			// valid
			XxlJobLog jobLog = xxlJobLogMapper.load(logId);	// todo, need to improve performance
			if (jobLog == null) {
				return Response.ofFail(I18nUtil.getString("joblog_logid_invalid"));
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
			logger.error("logId({}) logDetailCat error: {}", logId, e.getMessage(), e);
			return Response.ofFail(e.getMessage());
		}
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
}
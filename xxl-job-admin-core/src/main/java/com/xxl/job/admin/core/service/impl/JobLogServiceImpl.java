package com.xxl.job.admin.core.service.impl;

import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.mapper.XxlJobLogMapper;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.core.service.JobLogService;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.openapi.ExecutorBiz;
import com.xxl.job.core.openapi.model.KillRequest;
import com.xxl.job.tool.response.Response;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.response.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.*;

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
    public PageModel<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, int logStatus, String startTime, String endTime) {
        // parse time filter
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (StringTool.isNotBlank(startTime) && StringTool.isNotBlank(endTime)) {
            triggerTimeStart = DateTool.parseDateTime(startTime);
            triggerTimeEnd = DateTool.parseDateTime(endTime);
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
    public Map<String, Object> getLogStatGraph(int jobGroup, int jobId, String fromTime, String toTime) {
        Date from = DateTool.parseDateTime(fromTime);
        Date to = DateTool.parseDateTime(toTime);
        return xxlJobLogMapper.findLogReport(from, to);
    }

    @Override
    public boolean kill(long id, int userId) {
        // load log and jobInfo
        XxlJobLog log = xxlJobLogMapper.load(id);
        if (log == null) {
            return false;
        }
        XxlJobInfo jobInfo = xxlJobInfoMapper.loadById(log.getJobId());
        if (jobInfo == null) {
            return false;
        }
        if (XxlJobContext.HANDLE_CODE_SUCCESS != log.getTriggerCode()) {
            return false;
        }

        // request of kill
        try {
            ExecutorBiz executorBiz = XxlJobAdminBootstrap.getExecutorBiz(log.getExecutorAddress());
            Response<String> runResult = executorBiz.kill(new KillRequest(jobInfo.getId()));

            if (XxlJobContext.HANDLE_CODE_SUCCESS == runResult.getCode()) {
                log.setHandleCode(XxlJobContext.HANDLE_CODE_FAIL);
                log.setHandleMsg("killed by operator:" + userId + ", " + (runResult.getMsg() != null ? runResult.getMsg() : ""));
                log.setHandleTime(new Date());
                XxlJobAdminBootstrap.getInstance().getJobCompleter().complete(log);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("kill error, logId={}", id, e);
            return false;
        }
    }

    @Override
    public int clearLog(int jobGroup, int jobId, int type) {
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
            return 0;
        }

        // clear logs in batches
        int clearCount = 0;
        List<Long> logIds;
        do {
            logIds = xxlJobLogMapper.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && !logIds.isEmpty()) {
                xxlJobLogMapper.clearLog(logIds);
                clearCount += logIds.size();
            }
        } while (logIds != null && !logIds.isEmpty());

        return clearCount;
    }
}
package com.xxl.job.admin.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.mapper.XxlJobLogMapper;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.core.service.JobInfoService;
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
import java.util.stream.Collectors;

/**
 * Job log service implementation for xxl-job core module.
 * Refactored from JobLogController to remove web-layer dependencies.
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<XxlJobLogMapper, XxlJobLog> implements JobLogService {
    private static final Logger logger = LoggerFactory.getLogger(JobLogServiceImpl.class);

    @Resource
    private JobInfoService jobInfoService;

    /** 原 XxlJobLogMapper
     * 	<select id="pageList" resultMap="XxlJobLog">
            SELECT <include refid="Base_Column_List" />
            FROM xxl_job_log AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="jobGroup gt 0">
                    AND t.job_group = #{jobGroup}
                </if>
                <if test="jobId gt 0">
                    AND t.job_id = #{jobId}
                </if>
                <if test="triggerTimeStart != null">
                    AND t.trigger_time <![CDATA[ >= ]]> #{triggerTimeStart}
                </if>
                <if test="triggerTimeEnd != null">
                    AND t.trigger_time <![CDATA[ <= ]]> #{triggerTimeEnd}
                </if>
                <if test="logStatus == 1" >
                    AND t.handle_code = 200
                </if>
                <if test="logStatus == 2" >
                    AND (
                        t.trigger_code NOT IN (0, 200) OR
                        t.handle_code NOT IN (0, 200)
                    )
                </if>
                <if test="logStatus == 3" >
                    AND t.trigger_code = 200
                    AND t.handle_code = 0
                </if>
            </trim>
            ORDER BY t.id DESC
            LIMIT #{offset}, #{pagesize}
        </select>
        
        <select id="pageListCount" resultType="int">
            SELECT count(1)
            FROM xxl_job_log AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="jobGroup gt 0">
                    AND t.job_group = #{jobGroup}
                </if>
                <if test="jobId gt 0">
                    AND t.job_id = #{jobId}
                </if>
                <if test="triggerTimeStart != null">
                    AND t.trigger_time <![CDATA[ >= ]]> #{triggerTimeStart}
                </if>
                <if test="triggerTimeEnd != null">
                    AND t.trigger_time <![CDATA[ <= ]]> #{triggerTimeEnd}
                </if>
                <if test="logStatus == 1" >
                    AND t.handle_code = 200
                </if>
                <if test="logStatus == 2" >
                    AND (
                        t.trigger_code NOT IN (0, 200) OR
                        t.handle_code NOT IN (0, 200)
                    )
                </if>
                <if test="logStatus == 3" >
                    AND t.trigger_code = 200
                    AND t.handle_code = 0
                </if>
            </trim>
        </select>
     */

    @Override
    public PageModel<XxlJobLog> pageList(int page, int pagesize, int jobGroup, int jobId, int logStatus, String filterTime) {
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

		Page<XxlJobLog> p = new Page<XxlJobLog>(page, pagesize);

        p.addOrder(new OrderItem().setColumn("id").setAsc(false));
        
		IPage<XxlJobLog> ipage =  this.page(p, this.getQueryWrapper(jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus));
		
        // package result
        PageModel<XxlJobLog> pageModel = new PageModel<>();
        pageModel.setData(ipage.getRecords());
        pageModel.setTotal(Math.toIntExact(ipage.getTotal()));

        return pageModel;
    }

	 private QueryWrapper<XxlJobLog> getQueryWrapper(int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        QueryWrapper<XxlJobLog> qw = new QueryWrapper<>();
        if (jobId == 0 && jobGroup > 0)
            qw = qw.eq("job_group", jobGroup);
        if (jobId > 0)
            qw = qw.eq("job_id", jobId);
        if (triggerTimeStart != null)
            qw = qw.ge("trigger_time", triggerTimeStart);
        if (triggerTimeEnd != null)
            qw = qw.le("trigger_time", triggerTimeEnd);
        if (logStatus == 1)
            qw = qw.eq("handle_code", 200);
        if (logStatus == 2)
            qw = qw.and(wq -> wq.notIn("trigger_code", Arrays.asList(0, 200)).or().notIn("handle_code", Arrays.asList(0, 200)));
        if (logStatus == 3)
            qw = qw.eq("trigger_code", 200).eq("handle_code", 0);
        if (logStatus > 3)
            qw = qw.eq("trigger_code", 200).eq("handle_code", logStatus);
        return qw;
    }

    @Override
    public XxlJobLog load(long id) {
        /** 原 XxlJobLogMapper
         * 	<select id="load" parameterType="java.lang.Long" resultMap="XxlJobLog">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_log AS t
                WHERE t.id = #{id}
            </select>
        */
        return this.getById(id);
    }

    @Override
    public XxlJobLog loadAndValidate(long id) throws XxlException {
        XxlJobLog jobLog = this.load(id);
        if (jobLog == null) {
            throw new XxlException(I18nUtil.getString("joblog_logid_invalid"));
        }
        return jobLog;
    }

    @Override
    public Map<String, Object> getLogStatGraph(int jobGroup, int jobId, String fromTime, String toTime) {
        Date from = DateTool.parseDateTime(fromTime);
        Date to = DateTool.parseDateTime(toTime);
        return this.baseMapper.findLogReport(from, to);
    }

    @Override
    public Response<String> kill(long id, Function<Integer, Boolean> groupPermissionCheck) {
        // base check
		XxlJobLog log = this.getById(id);
		XxlJobInfo jobInfo = jobInfoService.getById(log.getJobId());
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
            logIds = this.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && !logIds.isEmpty()) {
                this.clearLog(logIds);
            }
        } while (logIds != null && !logIds.isEmpty());

        return 1;
    }

    @Override
    public void clearLog(List<Long> ids) {
        /** 原 XxlJobLogMapper
         * 	<delete id="clearLog" >
                delete from xxl_job_log
                WHERE id in
                <foreach collection="logIds" item="item" open="(" close=")" separator="," >
                    #{item}
                </foreach>
            </delete>
        */
        this.removeBatchByIds(ids);
    }

	@Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        /** 原 XxlJobLogMapper
         * 	<select id="findClearLogIds" resultType="long" >
                SELECT id FROM xxl_job_log
                <trim prefix="WHERE" prefixOverrides="AND | OR" >
                    <if test="jobGroup gt 0">
                        AND job_group = #{jobGroup}
                    </if>
                    <if test="jobId gt 0">
                        AND job_id = #{jobId}
                    </if>
                    <if test="clearBeforeTime != null">
                        AND trigger_time <![CDATA[ <= ]]> #{clearBeforeTime}
                    </if>
                    <if test="clearBeforeNum gt 0">
                        AND id NOT in(
                        SELECT id FROM(
                        SELECT id FROM xxl_job_log AS t
                        <trim prefix="WHERE" prefixOverrides="AND | OR" >
                            <if test="jobGroup gt 0">
                                AND t.job_group = #{jobGroup}
                            </if>
                            <if test="jobId gt 0">
                                AND t.job_id = #{jobId}
                            </if>
                        </trim>
                        ORDER BY t.trigger_time desc
                        LIMIT 0, #{clearBeforeNum}
                        ) t1
                        )
                    </if>
                </trim>
                order by id asc
                LIMIT #{pagesize}
            </select>
        */
        QueryWrapper<XxlJobLog> qw = new QueryWrapper<>();
        if (jobGroup > 0)
            qw = qw.eq("job_group", jobGroup);
        if (jobId > 0)
            qw = qw.eq("job_id", jobId);
        if (clearBeforeTime != null)
            qw = qw.le("trigger_time", clearBeforeTime);

        if (clearBeforeNum > 0) {
            QueryWrapper<XxlJobLog> beforeQW = new QueryWrapper<>();
            if (jobGroup > 0)
                beforeQW = beforeQW.eq("job_group", jobGroup);
            if (jobId > 0)
                beforeQW = beforeQW.eq("job_id", jobId);
            beforeQW.orderByDesc("trigger_time");

            IPage<XxlJobLog> iPage = this.page(
                new Page<XxlJobLog>(1, clearBeforeNum), beforeQW);

            List<XxlJobLog> xxlJobLogDOBefore = iPage.getRecords();

            List<Long> idsBefore = xxlJobLogDOBefore.stream().map(XxlJobLog::getId).collect(Collectors.toList());

            qw = qw.notIn("id", idsBefore);
        }

        IPage<XxlJobLog> iPage = this.page(
                new Page<XxlJobLog>(1, pagesize), qw);

        List<XxlJobLog> resultListXxlJobLogDO = iPage.getRecords();        

        List<Long> resultList = resultListXxlJobLogDO.stream().map(XxlJobLog::getId).collect(Collectors.toList());

        return resultList;
    }

    @Override
    public Response<LogResult> getLogDetailCat(long logId, int fromLineNum) throws XxlException {
        try {
			// valid
			XxlJobLog jobLog = this.getById(logId);	// todo, need to improve performance
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

    @Override
    public List<Long> findFailJobLogIds(int pagesize) {
        /** 原 XxlJobLogMapper
         * 	<select id="findFailJobLogIds" resultType="long" >
                SELECT id FROM `xxl_job_log`
                WHERE !(
                    (trigger_code in (0, 200) and handle_code = 0)
                    OR
                    (handle_code = 200)
                )
                AND `alarm_status` = 0
                ORDER BY id ASC
                LIMIT #{pagesize}
            </select>
         */
        QueryWrapper<XxlJobLog> qw = new QueryWrapper<>();

        qw.not(i -> i
                .and(j -> j
                    .in("trigger_code", 0, 200)
                    .eq("handle_code", 0)
                )
                .or(j -> j
                    .eq("handle_code", 200)
                ))
            .eq("alarm_status", 0)
            .orderByAsc("id");

        IPage<XxlJobLog> iPage = this.page(
            new Page<XxlJobLog>(1, pagesize), qw);

        List<XxlJobLog> res = iPage.getRecords();

        return res.stream().map(XxlJobLog::getId).collect(Collectors.toList());
    }

    @Override
    public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
        /** 原 XxlJobLogMapper
         * <update id="updateAlarmStatus" >
                UPDATE xxl_job_log
                SET
                    `alarm_status` = #{newAlarmStatus}
                WHERE `id`= #{logId} AND `alarm_status` = #{oldAlarmStatus}
            </update>
         */
        return this.update(new UpdateWrapper<XxlJobLog>()
                .set("alarm_status", newAlarmStatus)
                .eq("id", logId)
                .eq("alarm_status", oldAlarmStatus))
        ? 1 : 0;
    }

    @Override
    public List<Long> findLostJobIds(Date losedTime) {
        return this.baseMapper.findLostJobIds(losedTime);
    }

    @Override
    public void saveLog(XxlJobLog xxlJobLog) {
        this.save(xxlJobLog);
    }

    @Override
    public int updateTriggerInfo(XxlJobLog xxlJobLog) {
        /** 原 XxlJobLogMapper
         * <update id="updateTriggerInfo" >
                UPDATE xxl_job_log
                SET
                    `trigger_time`= #{triggerTime},
                    `trigger_code`= #{triggerCode},
                    `trigger_msg`= #{triggerMsg},
                    `executor_address`= #{executorAddress},
                    `executor_handler`=#{executorHandler},
                    `executor_param`= #{executorParam},
                    `executor_sharding_param`= #{executorShardingParam},
                    `executor_fail_retry_count`= #{executorFailRetryCount}
                WHERE `id`= #{id}
            </update>
         */
        return this.updateById(xxlJobLog) ? 1 : 0;
    }

    @Override
    public int updateHandleInfo(XxlJobLog xxlJobLog) {
        /** 原 XxlJobLogMapper
         * <update id="updateHandleInfo">
                UPDATE xxl_job_log
                SET 
                    `handle_time`= #{handleTime}, 
                    `handle_code`= #{handleCode},
                    `handle_msg`= #{handleMsg}
                WHERE `id`= #{id}
            </update>
         */
        return this.updateById(xxlJobLog) ? 1 : 0;
    }

    @Override
    public int deleteByJobId(int jobId) {
        /** 原 XxlJobLogMapper
         *  <delete id="delete" >
                delete from xxl_job_log
                WHERE job_id = #{jobId}
            </delete>
         */
        return this.remove(new QueryWrapper<XxlJobLog>().eq("job_id", jobId)) ? 1 : 0;
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

    @Override
    public Map<String, Object> findLogReport(Date from, Date to) {
        return this.baseMapper.findLogReport(from, to);
    }
}
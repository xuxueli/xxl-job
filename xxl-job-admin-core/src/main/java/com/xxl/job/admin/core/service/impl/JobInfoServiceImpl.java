package com.xxl.job.admin.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.constant.TriggerStatus;
import com.xxl.job.admin.core.exception.XxlException;
import com.xxl.job.admin.core.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.scheduler.config.XxlJobAdminBootstrap;
import com.xxl.job.admin.core.scheduler.cron.CronExpression;
import com.xxl.job.admin.core.scheduler.misfire.MisfireStrategyEnum;
import com.xxl.job.admin.core.scheduler.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.scheduler.thread.JobScheduleHelper;
import com.xxl.job.admin.core.scheduler.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.scheduler.type.ScheduleTypeEnum;
import com.xxl.job.admin.core.service.JobGroupService;
import com.xxl.job.admin.core.service.JobInfoService;
import com.xxl.job.admin.core.service.JobLogGlueService;
import com.xxl.job.admin.core.service.JobLogService;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.core.constant.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.json.GsonTool;
import com.xxl.tool.response.PageModel;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * JobInfo service implementation for xxl-job core module.
 * Refactored from XxlJobServiceImpl to remove web-layer dependencies.
 *
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class JobInfoServiceImpl extends ServiceImpl<XxlJobInfoMapper, XxlJobInfo> implements JobInfoService {
    private static final Logger logger = LoggerFactory.getLogger(JobInfoServiceImpl.class);

    @Resource
    private JobGroupService jobGroupService;

    @Resource
    private JobLogService jobLogService;

    @Resource
    private JobLogGlueService jobLogGlueService;


    @Override
    public int add(XxlJobInfo jobInfo, String username, Function<Integer, Boolean> groupPermissionCheck) {
        // valid base
		XxlJobGroup group = jobGroupService.load(jobInfo.getJobGroup());
		if (group == null) {
			throw new XxlException(I18nUtil.getString("system_please_choose")+I18nUtil.getString("jobinfo_field_jobgroup"));
		}
		if (StringTool.isBlank(jobInfo.getJobDesc())) {
			throw new XxlException(I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc"));
		}
		if (StringTool.isBlank(jobInfo.getAuthor())) {
			throw new XxlException(I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author"));
		}

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        validateScheduleType(scheduleTypeEnum, jobInfo.getScheduleConf());

        // valid job
        if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
            throw new XxlException(I18nUtil.getString("jobconf_field_gluetype") + I18nUtil.getString("system_invalid"));
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType()) && StringTool.isBlank(jobInfo.getExecutorHandler())) {
            throw new XxlException(I18nUtil.getString("system_please_input")+"JobHandler");
        }
        // fix "\r" in shell
        if (GlueTypeEnum.GLUE_SHELL == GlueTypeEnum.match(jobInfo.getGlueType()) && jobInfo.getGlueSource() != null) {
            jobInfo.setGlueSource(jobInfo.getGlueSource().replaceAll("\r", ""));
        }

        // valid advanced
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_invalid"));
		}
		if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
			throw new XxlException(I18nUtil.getString("misfire_strategy")+I18nUtil.getString("system_invalid"));
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_invalid"));
		}

        // 》ChildJobId valid
		if (StringTool.isNotBlank(jobInfo.getChildJobId())) {
			String[] childJobIds = jobInfo.getChildJobId().split(",");
			for (String childJobIdItem: childJobIds) {
				if (StringTool.isNotBlank(childJobIdItem) && StringTool.isNumeric(childJobIdItem)) {
					XxlJobInfo childJobInfo = this.getJobInfoById(Integer.parseInt(childJobIdItem));
					if (childJobInfo==null) {
						throw new XxlException(
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
					// valid jobGroup permission
					if (groupPermissionCheck != null && !groupPermissionCheck.apply(childJobInfo.getJobGroup())) {
						throw new XxlException(
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_permission_limit")), childJobIdItem));
					}
				} else {
					throw new XxlException(
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_invalid")), childJobIdItem));
				}
			}

			// join , avoid "xxx,,"
			String temp = "";
			for (String item:childJobIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length()-1);

			jobInfo.setChildJobId(temp);
		}

        // add in db
        jobInfo.setAddTime(new Date());
        jobInfo.setUpdateTime(new Date());
        jobInfo.setGlueUpdatetime(new Date());
        // remove the whitespace
        jobInfo.setExecutorHandler(jobInfo.getExecutorHandler().trim());
        
        /** 原XxlJobInfoMapper
         * save
         */

        this.save(jobInfo);
        if (jobInfo.getId() < 1) {
            throw new XxlException(I18nUtil.getString("jobinfo_field_add")+I18nUtil.getString("system_fail"));
        }

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorName = {}, type = {}, content = {}",
                username, "jobinfo-add", GsonTool.toJson(jobInfo));

        return jobInfo.getId();
    }

    @Override
    public int update(XxlJobInfo jobInfo, String userName, Function<Integer, Boolean> groupPermissionCheck) {
        // valid base
		if (StringTool.isBlank(jobInfo.getJobDesc())) {
			throw new XxlException(I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_jobdesc"));
		}
		if (StringTool.isBlank(jobInfo.getAuthor())) {
			throw new XxlException(I18nUtil.getString("system_please_input")+I18nUtil.getString("jobinfo_field_author"));
		}

        // valid trigger
        ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), null);
        validateScheduleType(scheduleTypeEnum, jobInfo.getScheduleConf());

        // valid advanced
		if (ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null) == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_field_executorRouteStrategy")+I18nUtil.getString("system_invalid"));
		}
		if (MisfireStrategyEnum.match(jobInfo.getMisfireStrategy(), null) == null) {
			throw new XxlException(I18nUtil.getString("misfire_strategy")+I18nUtil.getString("system_invalid"));
		}
		if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_field_executorBlockStrategy")+I18nUtil.getString("system_invalid"));
		}


        // 》ChildJobId valid
		if (StringTool.isNotBlank(jobInfo.getChildJobId())) {
			String[] childJobIds = jobInfo.getChildJobId().split(",");
			for (String childJobIdItem: childJobIds) {
				if (StringTool.isNotBlank(childJobIdItem) && StringTool.isNumeric(childJobIdItem)) {
					// parse child
					int childJobId = Integer.parseInt(childJobIdItem);
					if (childJobId == jobInfo.getId()) {
						throw new XxlException(I18nUtil.getString("jobinfo_field_childJobId")+"("+childJobId+")"+I18nUtil.getString("system_invalid"));
					}

					// valid child
					XxlJobInfo childJobInfo = this.getJobInfoById(childJobId);
					if (childJobInfo==null) {
						throw new XxlException(
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_not_found")), childJobIdItem));
					}
					// valid jobGroup permission
					if (groupPermissionCheck != null && !groupPermissionCheck.apply(childJobInfo.getJobGroup())) {
						throw new XxlException(
								MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_permission_limit")), childJobIdItem));
					}
				} else {
					throw new XxlException(
							MessageFormat.format((I18nUtil.getString("jobinfo_field_childJobId")+"({0})"+I18nUtil.getString("system_invalid")), childJobIdItem));
				}
			}

			// join , avoid "xxx,,"
			String temp = "";
			for (String item:childJobIds) {
				temp += item + ",";
			}
			temp = temp.substring(0, temp.length()-1);

			jobInfo.setChildJobId(temp);
		}


        // group valid
		XxlJobGroup jobGroup = jobGroupService.load(jobInfo.getJobGroup());
		if (jobGroup == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_field_jobgroup")+I18nUtil.getString("system_invalid"));
		}

		// stage job info
		XxlJobInfo exists_jobInfo = this.getJobInfoById(jobInfo.getId());
		if (exists_jobInfo == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_field_id")+I18nUtil.getString("system_not_found"));
		}

        // next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = exists_jobInfo.getTriggerNextTime();
		boolean scheduleDataNotChanged = jobInfo.getScheduleType().equals(exists_jobInfo.getScheduleType())
				&& jobInfo.getScheduleConf().equals(exists_jobInfo.getScheduleConf());		// 触发配置如果不变，避免重复计算；
		if (exists_jobInfo.getTriggerStatus() == TriggerStatus.RUNNING.getValue() && !scheduleDataNotChanged) {
			try {
				// generate next trigger time
				Date nextValidTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(jobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));
				if (nextValidTime == null) {
					throw new XxlException(I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid"));
				}
				nextTriggerTime = nextValidTime.getTime();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new XxlException(I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid"));
			}
		}

        exists_jobInfo.setJobGroup(jobInfo.getJobGroup());
        exists_jobInfo.setJobDesc(jobInfo.getJobDesc());
        exists_jobInfo.setAuthor(jobInfo.getAuthor());
        exists_jobInfo.setAlarmEmail(jobInfo.getAlarmEmail());
        exists_jobInfo.setScheduleType(jobInfo.getScheduleType());
        exists_jobInfo.setScheduleConf(jobInfo.getScheduleConf());
        exists_jobInfo.setMisfireStrategy(jobInfo.getMisfireStrategy());
        exists_jobInfo.setExecutorRouteStrategy(jobInfo.getExecutorRouteStrategy());
        // remove the whitespace
        exists_jobInfo.setExecutorHandler(jobInfo.getExecutorHandler().trim());
        exists_jobInfo.setExecutorParam(jobInfo.getExecutorParam());
        exists_jobInfo.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        exists_jobInfo.setExecutorTimeout(jobInfo.getExecutorTimeout());
        exists_jobInfo.setExecutorFailRetryCount(jobInfo.getExecutorFailRetryCount());
        exists_jobInfo.setChildJobId(jobInfo.getChildJobId());
        exists_jobInfo.setTriggerNextTime(nextTriggerTime);

        exists_jobInfo.setUpdateTime(new Date());

        /** 原XxlJobInfoMapper
         * update
         */
        this.updateById(exists_jobInfo);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorName = {}, type = {}, content = {}",
                userName, "jobinfo-update", GsonTool.toJson(exists_jobInfo));

        return 1;
    }

    @Override
    public int remove(List<Integer> ids, String userName, Function<Integer, Boolean> groupPermissionCheck) {
        // valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			throw new XxlException(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
		}

        int id = ids.get(0);

        // valid job
        XxlJobInfo xxlJobInfo = this.getJobInfoById(id);
        if (xxlJobInfo == null) {
            return 1;
        }

        // valid jobGroup permission
        if (groupPermissionCheck != null && !groupPermissionCheck.apply(xxlJobInfo.getJobGroup())) {
            throw new XxlException(I18nUtil.getString("system_permission_limit"));
        }

        /** 原XxlJobInfoMapper
         *  <delete id="delete" parameterType="java.util.HashMap">
                DELETE
                FROM xxl_job_info
                WHERE id = #{id}
            </delete>
         */

        this.removeById(id);
        jobLogService.removeById(id);
        jobLogGlueService.deleteByJobId(id);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorName = {}, type = {}, content = {}",
                userName, "jobinfo-remove", id);

        return 1;
    }

    @Override
    public int start(List<Integer> ids, String userName, Function<Integer, Boolean> groupPermissionCheck) {
        // valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			throw new XxlException(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
		}

        int id = ids.get(0);
        
        // load and valid
		XxlJobInfo xxlJobInfo = this.getJobInfoById(id);
		if (xxlJobInfo == null) {
			throw new XxlException(I18nUtil.getString("jobinfo_glue_jobid_invalid"));
		}

        // valid jobGroup permission
        if (groupPermissionCheck != null && !groupPermissionCheck.apply(xxlJobInfo.getJobGroup())) {
            throw new XxlException(I18nUtil.getString("system_permission_limit"));
        }

		// valid ScheduleType: can not be none
		ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(xxlJobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
		if (ScheduleTypeEnum.NONE == scheduleTypeEnum) {
			throw new XxlException(I18nUtil.getString("schedule_type_none_limit_start"));
		}

		// next trigger time (5s后生效，避开预读周期)
		long nextTriggerTime = 0;
		try {
			// generate next trigger time
			Date nextValidTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(xxlJobInfo, new Date(System.currentTimeMillis() + JobScheduleHelper.PRE_READ_MS));

			if (nextValidTime == null) {
				throw new XxlException(I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid"));
			}
			nextTriggerTime = nextValidTime.getTime();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new XxlException(I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid"));
		}

		xxlJobInfo.setTriggerStatus(TriggerStatus.RUNNING.getValue());
		xxlJobInfo.setTriggerLastTime(0);
		xxlJobInfo.setTriggerNextTime(nextTriggerTime);

		xxlJobInfo.setUpdateTime(new Date());
		this.updateById(xxlJobInfo);

		// write operation log
		logger.info(">>>>>>>>>>> xxl-job operation log: operatorName = {}, type = {}, content = {}",
				userName, "jobinfo-start", id);

        return 1;
    }

    @Override
    public int stop(List<Integer> ids, String userName, Function<Integer, Boolean> groupPermissionCheck) {
        // valid
		if (CollectionTool.isEmpty(ids) || ids.size()!=1) {
			throw new XxlException(I18nUtil.getString("system_please_choose") + I18nUtil.getString("system_one") + I18nUtil.getString("system_data"));
		}

        int id = ids.get(0);

        // load and valid
        XxlJobInfo xxlJobInfo = this.getJobInfoById(id);
        if (xxlJobInfo == null) {
            throw new XxlException(I18nUtil.getString("jobinfo_glue_jobid_invalid"));
        }

        // valid jobGroup permission
        if (groupPermissionCheck != null && !groupPermissionCheck.apply(xxlJobInfo.getJobGroup())) {
            throw new XxlException(I18nUtil.getString("system_permission_limit"));
        }

        // stop
        xxlJobInfo.setTriggerStatus(TriggerStatus.STOPPED.getValue());
        xxlJobInfo.setTriggerLastTime(0);
        xxlJobInfo.setTriggerNextTime(0);

        xxlJobInfo.setUpdateTime(new Date());
        this.updateById(xxlJobInfo);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorName = {}, type = {}, content = {}",
                userName, "jobinfo-stop", id);

        return 1;
    }

    @Override
    public int trigger(int jobId, String userName, String executorParam, String addressList, Function<Integer, Boolean> groupPermissionCheck) {
        // valid job
        XxlJobInfo xxlJobInfo = this.getJobInfoById(jobId);
        if (xxlJobInfo == null) {
            throw new XxlException(I18nUtil.getString("jobinfo_glue_jobid_invalid"));
        }

        // valid jobGroup permission
        if (groupPermissionCheck != null && !groupPermissionCheck.apply(xxlJobInfo.getJobGroup())) {
            throw new XxlException(I18nUtil.getString("system_permission_limit"));
        }

        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        XxlJobAdminBootstrap.getInstance().getJobTriggerPoolHelper().trigger(jobId, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);

        // write operation log
        logger.info(">>>>>>>>>>> xxl-job operation log: operatorName = {}, type = {}, content = {}",
                userName, "jobinfo-trigger", jobId);

        return 1;
    }

    /** 原XxlJobInfoMapper
     * 	<select id="pageList" parameterType="java.util.HashMap" resultMap="XxlJobInfo">
            SELECT <include refid="Base_Column_List" />
            FROM xxl_job_info AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="jobGroup gt 0">
                    AND t.job_group = #{jobGroup}
                </if>
                <if test="triggerStatus gte 0">
                    AND t.trigger_status = #{triggerStatus}
                </if>
                <if test="jobDesc != null and jobDesc != ''">
                    AND t.job_desc like CONCAT(CONCAT('%', #{jobDesc}), '%')
                </if>
                <if test="executorHandler != null and executorHandler != ''">
                    AND t.executor_handler like CONCAT(CONCAT('%', #{executorHandler}), '%')
                </if>
                <if test="author != null and author != ''">
                    AND t.author like CONCAT(CONCAT('%', #{author}), '%')
                </if>
            </trim>
            ORDER BY id DESC
            LIMIT #{offset}, #{pagesize}
        </select>

        <select id="pageListCount" parameterType="java.util.HashMap" resultType="int">
            SELECT count(1)
            FROM xxl_job_info AS t
            <trim prefix="WHERE" prefixOverrides="AND | OR" >
                <if test="jobGroup gt 0">
                    AND t.job_group = #{jobGroup}
                </if>
                <if test="triggerStatus gte 0">
                    AND t.trigger_status = #{triggerStatus}
                </if>
                <if test="jobDesc != null and jobDesc != ''">
                    AND t.job_desc like CONCAT(CONCAT('%', #{jobDesc}), '%')
                </if>
                <if test="executorHandler != null and executorHandler != ''">
                    AND t.executor_handler like CONCAT(CONCAT('%', #{executorHandler}), '%')
                </if>
                <if test="author != null and author != ''">
                    AND t.author like CONCAT(CONCAT('%', #{author}), '%')
                </if>
            </trim>
        </select>
     */

    @Override
    public PageModel<XxlJobInfo> pageList(int page, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        Page<XxlJobInfo> p = new Page<>(page, pagesize);

        p.addOrder(new OrderItem().setColumn("id").setAsc(true));

        IPage<XxlJobInfo> iPage = this.page(p, this.getQueryWrapper(jobGroup, triggerStatus, jobDesc, executorHandler, author));
        
        // package result
        PageModel<XxlJobInfo> pageModel = new PageModel<>();
        pageModel.setData(iPage.getRecords());
        pageModel.setTotal(Math.toIntExact(iPage.getTotal()));

        return pageModel;
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc,
            String executorHandler, String author) {
        return (int) this.count(this.getQueryWrapper(jobGroup, triggerStatus, jobDesc, executorHandler, author));
    }

    private QueryWrapper<XxlJobInfo> getQueryWrapper(int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        QueryWrapper<XxlJobInfo> qw = new QueryWrapper<>();
        if (jobGroup > 0)
            qw = qw.eq("job_group", jobGroup);
        if (triggerStatus >= 0)
            qw = qw.eq("trigger_status", triggerStatus);
        if (StringTool.isNotEmpty(jobDesc))
            qw = qw.like("job_desc", jobDesc);
        if (StringTool.isNotEmpty(executorHandler))
            qw = qw.like("executor_handler", executorHandler);
        if (StringTool.isNotEmpty(author))
            qw = qw.like("author", author);
        return qw;
    }

    @Override
    public List<String> generateNextTriggerTime(String scheduleType, String scheduleConf) {
        List<String> result = new ArrayList<>();
        // valid
		if (StringTool.isBlank(scheduleType) || StringTool.isBlank(scheduleConf)) {
			return result;
		}
        try {
            // param
            XxlJobInfo paramXxlJobInfo = new XxlJobInfo();
            paramXxlJobInfo.setScheduleType(scheduleType);
            paramXxlJobInfo.setScheduleConf(scheduleConf);

            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                // generate next trigger time
                ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(paramXxlJobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
                lastTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(paramXxlJobInfo, lastTime);

                // collect data
                if (lastTime != null) {
                    result.add(DateTool.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> generateNextTriggerTime error. scheduleType={}, scheduleConf={}, error: {} ", scheduleType, scheduleConf, e.getMessage());
            throw new XxlException((I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid")) + e.getMessage());
        }
        return result;
    }

    @Override
    public List<String> generateNextTriggerTime(XxlJobInfo jobInfo) {
        List<String> result = new ArrayList<>();
        try {
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                // generate next trigger time
                ScheduleTypeEnum scheduleTypeEnum = ScheduleTypeEnum.match(jobInfo.getScheduleType(), ScheduleTypeEnum.NONE);
                lastTime = scheduleTypeEnum.getScheduleType().generateNextTriggerTime(jobInfo, lastTime);

                // collect data
                if (lastTime != null) {
                    result.add(DateTool.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> generateNextTriggerTime error. jobInfo = {}, error: {} ", GsonTool.toJson(jobInfo), e.getMessage());
        }
        return result;
    }

    @Override
    public XxlJobInfo getJobInfoById(int id) {
        return this.getById(id);
    }

    @Override
    public List<XxlJobInfo> getJobsByGroupId(int groupId) {
        /** 原XxlJobInfoMapper
         *  <select id="getJobsByGroup" parameterType="java.util.HashMap" resultMap="XxlJobInfo">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_info AS t
                WHERE t.job_group = #{jobGroup}
            </select>
         */
        return this.list(new QueryWrapper<XxlJobInfo>().eq("job_group", groupId));
    }

    @Override
    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
        /** 原XxlJobInfoMapper
         *  <select id="scheduleJobQuery" parameterType="java.util.HashMap" resultMap="XxlJobInfo">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_info AS t
                WHERE t.trigger_status = 1
                    and t.trigger_next_time <![CDATA[ <= ]]> #{maxNextTime}
                ORDER BY id ASC
                LIMIT #{pagesize}
            </select>
         */
        QueryWrapper<XxlJobInfo> qw = new QueryWrapper<XxlJobInfo>();

        qw = qw
            .eq("trigger_status", 1)
            .le("trigger_next_time", maxNextTime)
            .orderByAsc("id");

        IPage<XxlJobInfo> iPage = this.page(new Page<XxlJobInfo>(1, pagesize), qw);

        List<XxlJobInfo> res = iPage.getRecords();

        return res;
    }

    @Override
    public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
        return this.getBaseMapper().scheduleUpdate(xxlJobInfo);
    }

    @Override
    public int scheduleBatchUpdate(List<XxlJobInfo> jobInfoList) {
        return this.getBaseMapper().scheduleBatchUpdate(jobInfoList);
    }

    /**
     * Validate schedule type and schedule config.
     * Returns true if valid, false otherwise.
     */
    private void validateScheduleType(ScheduleTypeEnum scheduleTypeEnum, String scheduleConf) {
        if (scheduleTypeEnum == null) {
            throw new XxlException(I18nUtil.getString("schedule_type") + I18nUtil.getString("system_invalid"));
        }
        if (scheduleTypeEnum == ScheduleTypeEnum.CRON) {
            if (scheduleConf == null || !CronExpression.isValidExpression(scheduleConf)) {
				throw new XxlException("Cron" + I18nUtil.getString("system_invalid"));
			}
            return ;
        } else if (scheduleTypeEnum == ScheduleTypeEnum.FIX_RATE) {
            if (scheduleConf == null) {
                throw new XxlException(I18nUtil.getString("schedule_type") + I18nUtil.getString("system_invalid"));
            }
            try {
                int fixSecond = Integer.parseInt(scheduleConf);
                if (fixSecond < 1) {
                    throw new XxlException(I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid"));
                }
            } catch (Exception e) {
                throw new XxlException(I18nUtil.getString("schedule_type")+I18nUtil.getString("system_invalid"));
            }
        }
    }
}
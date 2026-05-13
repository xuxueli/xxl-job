package com.xxl.job.admin.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.core.mapper.XxlJobLogReportMapper;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.core.service.JobLogReportService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JobLogReportServiceImpl extends ServiceImpl<XxlJobLogReportMapper, XxlJobLogReport> implements JobLogReportService {

    @Override
    public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
        /** 原XxlJobLogReportMapper
         *  <select id="queryLogReport" resultMap="XxlJobLogReport">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_log_report AS t
                WHERE t.trigger_day between #{triggerDayFrom} and #{triggerDayTo}
                ORDER BY t.trigger_day ASC
            </select>
         */
        return this.list(
            new QueryWrapper<XxlJobLogReport>()
            .between("trigger_day", triggerDayFrom, triggerDayTo)
            .orderByAsc("trigger_day")
        );
    }

    @Override
    public XxlJobLogReport queryLogReportTotal() {
        return this.getBaseMapper().queryLogReportTotal();
    }

    @Override
    public boolean saveOrUpdate(XxlJobLogReport entity) {
        /** 原XxlJobLogReportMapper
         *  <insert id="saveOrUpdate" parameterType="com.xxl.job.admin.model.XxlJobLogReport" useGeneratedKeys="true" keyProperty="id" >
                INSERT INTO xxl_job_log_report (
                    `trigger_day`,
                    `running_count`,
                    `suc_count`,
                    `fail_count`,
                    `update_time`
                ) VALUES (
                    #{triggerDay},
                    #{runningCount},
                    #{sucCount},
                    #{failCount},
                    #{updateTime}
                )
                ON DUPLICATE KEY UPDATE
                    `running_count` = #{runningCount},
                    `suc_count` = #{sucCount},
                    `fail_count` = #{failCount},
                    `update_time` = #{updateTime}
            </insert>
         */
        XxlJobLogReport existReport = this.getOne(new QueryWrapper<XxlJobLogReport>().eq("trigger_day", entity.getTriggerDay()));

        if (existReport != null) {
            entity.setId(existReport.getId());
            return this.updateById(entity);
        } else {
            return this.save(entity);
        }
    }
}
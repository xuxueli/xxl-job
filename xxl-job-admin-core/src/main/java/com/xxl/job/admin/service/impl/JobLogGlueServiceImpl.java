package com.xxl.job.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.admin.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.model.XxlJobLogGlue;
import com.xxl.job.admin.service.JobLogGlueService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("XxlJobLogGlueService")
public class JobLogGlueServiceImpl extends ServiceImpl<XxlJobLogGlueMapper, XxlJobLogGlue> implements JobLogGlueService {

    @Override
    public List<XxlJobLogGlue> findByJobId(int jobId) {
        /** 原XxlJobLogGlueMapper
         *  <select id="findByJobId" parameterType="java.lang.Integer" resultMap="XxlJobLogGlue">
                SELECT <include refid="Base_Column_List" />
                FROM xxl_job_logglue AS t
                WHERE t.job_id = #{jobId}
                ORDER BY id DESC
            </select>
         */
        return this.list(new QueryWrapper<XxlJobLogGlue>()
                .eq("job_id", jobId)
                .orderByDesc("id"));
    }

    @Override
    public void removeOld(int id, int limit) {
        /** 原XxlJobLogGlueMapper
         *  <delete id="removeOld" >
                DELETE FROM xxl_job_logglue
                WHERE id NOT in(
                    SELECT id FROM(
                        SELECT id FROM xxl_job_logglue
                        WHERE `job_id` = #{jobId}
                        ORDER BY update_time desc
                        LIMIT 0, #{limit}
                    ) t1
                ) AND `job_id` = #{jobId}
            </delete>
         */
        QueryWrapper<XxlJobLogGlue> qw = new QueryWrapper<XxlJobLogGlue>()
            .eq("job_id", id)
            .orderByDesc("update_time");

        IPage<XxlJobLogGlue> iPage = this.page(
            new Page<XxlJobLogGlue>(1, limit), qw);

        List<XxlJobLogGlue> limitedResult = iPage.getRecords();

        List<Integer> limitedResultId = limitedResult.stream().map(XxlJobLogGlue::getId).collect(Collectors.toList());

        this.remove(
                    new QueryWrapper<XxlJobLogGlue>()
                            .notIn("id", limitedResultId)
                            .eq("job_id", id));
    }

    @Override
    public int deleteByJobId(int jobId) {
        /** 原XxlJobLogGlueMapper
         *  <delete id="deleteByJobId" parameterType="java.lang.Integer" >
                DELETE FROM xxl_job_logglue
                WHERE `job_id` = #{jobId}
            </delete>
         */
        return this.remove(new QueryWrapper<XxlJobLogGlue>().eq("job_id", jobId)) ? 1 : 0;
    }
}
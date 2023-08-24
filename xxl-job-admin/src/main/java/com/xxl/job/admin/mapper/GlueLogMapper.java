package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.common.pojo.entity.GlueLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * GLUE日志 Mapper 接口
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface GlueLogMapper extends BaseMapper<GlueLog> {

    /**
     * 查询Glue日志根据任务id
     *
     * @param jobId 任务id
     * @return {@link List}<{@link GlueLog}>
     */
    List<GlueLog> findGlueLogByJobId(@Param("jobId") Long jobId);

    /**
     * 删除旧Glue日志
     *
     * @param jobId 任务ID
     * @param limit 数量
     */
    void deleteOldGlueLog(@Param("jobId") Long jobId, @Param("limit") Integer limit);

    /**
     * 根据任务ID删除Glue日志
     *
     * @param jobId 任务ID
     */
    void deleteGlueLogByJobId(@Param("jobId") Long jobId);



}

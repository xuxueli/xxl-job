package com.xxl.job.admin.service;

import com.xxl.job.admin.common.pojo.dto.GlueLogDTO;
import com.xxl.job.admin.common.pojo.entity.GlueLog;
import com.xxl.job.admin.common.pojo.vo.GlueLogVO;
import com.xxl.job.admin.service.base.BaseService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * GLUE日志 服务类
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
public interface GlueLogService extends BaseService<GlueLog, GlueLog, GlueLogVO> {

    /**
     * 保存GLUE
     *
     * @param glueLogDTO GLUE DTO
     */
    void saveGlue(GlueLogDTO glueLogDTO);

    /**
     * 查询Glue日志根据任务id
     *
     * @param jobId 任务id
     * @return {@link List}<{@link GlueLogVO}>
     */
    List<GlueLogVO> findGlueLogByJobId(@Param("jobId") Long jobId);

    /**
     * 删除旧Glue日志
     *
     * @param jobId 任务ID
     * @param limit 数量
     */
    void deleteOldGlueLog(@Param("jobId") Long jobId, @Param("limit") int limit);

    /**
     * 根据任务ID删除Glue日志
     *
     * @param jobId 任务ID
     */
    void deleteGlueLogByJobId(@Param("jobId") Long jobId);







}

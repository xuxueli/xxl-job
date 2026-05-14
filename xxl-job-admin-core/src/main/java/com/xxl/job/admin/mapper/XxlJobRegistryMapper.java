package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.model.XxlJobRegistry;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface XxlJobRegistryMapper extends BaseMapper<XxlJobRegistry> {

    /**
     * Find dead registry ids (DATE_ADD - complex SQL, kept here)
     */
    @Select("SELECT t.id FROM xxl_job_registry AS t " +
            "WHERE t.update_time < DATE_ADD(#{nowTime}, INTERVAL -#{timeout} SECOND)")
    List<Integer> findDead(@Param("timeout") int timeout, @Param("nowTime") Date nowTime);

    /**
     * Remove dead registry records (foreach dynamic SQL - kept here)
     */
    int removeDead(@Param("ids") List<Integer> ids);

    /**
     * Find all active registries (DATE_ADD - complex SQL, kept here)
     */
    @Select("SELECT * FROM xxl_job_registry AS t " +
            "WHERE t.update_time > DATE_ADD(#{nowTime}, INTERVAL -#{timeout} SECOND)")
    List<XxlJobRegistry> findAll(@Param("timeout") int timeout, @Param("nowTime") Date nowTime);
}
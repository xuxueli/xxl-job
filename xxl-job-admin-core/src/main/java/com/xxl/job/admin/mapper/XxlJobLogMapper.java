package com.xxl.job.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.model.XxlJobLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 *
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface XxlJobLogMapper extends BaseMapper<XxlJobLog> {

    /**
     * Query log report data (with COALESCE and CASE WHEN - complex SQL, kept here)
     */
    @Select("SELECT " +
                "COALESCE(COUNT(handle_code),0) AS triggerDayCount, " +
                "COALESCE(SUM(CASE WHEN (trigger_code IN (0, 200) AND handle_code = 0) THEN 1 ELSE 0 END),0) AS triggerDayCountRunning, " +
                "COALESCE(SUM(CASE WHEN handle_code = 200 THEN 1 ELSE 0 END),0) AS triggerDayCountSuc " +
            "FROM xxl_job_log " +
            "WHERE trigger_time BETWEEN #{from} AND #{to}")
    Map<String, Object> findLogReport(@Param("from") Date from, @Param("to") Date to);

    /**
     * Find lost job ids (LEFT JOIN - complex SQL, kept here)
     */
    @Select("SELECT t.id FROM xxl_job_log t " +
                "LEFT JOIN xxl_job_registry t2 ON t.executor_address = t2.registry_value " +
            "WHERE t.trigger_code = 200 AND t.handle_code = 0 " +
            "AND t.trigger_time <= #{losedTime} AND t2.id IS NULL")
    List<Long> findLostJobIds(@Param("losedTime") Date losedTime);
}
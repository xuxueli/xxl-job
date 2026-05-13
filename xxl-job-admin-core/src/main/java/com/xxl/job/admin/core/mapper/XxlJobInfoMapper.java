package com.xxl.job.admin.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface XxlJobInfoMapper extends BaseMapper<XxlJobInfo> {
    /**
     * 更新调度状态
     * 只能更新 trigger_status = 1 的任务，避免停用任务被启用
     */
    @Update("<script>" +
            "UPDATE xxl_job_info SET " +
                "trigger_last_time = #{triggerLastTime}, " +
                "trigger_next_time = #{triggerNextTime}" +
                " <if test='triggerStatus != null and triggerStatus gte 0'>" +
                        ", trigger_status = #{triggerStatus}" +
                " </if>" +
            " WHERE id = #{id} AND trigger_status = 1" +
            "</script>")
    int scheduleUpdate(XxlJobInfo xxlJobInfo);

    /**
     * 批量更新调度状态
     */
    @Update("<script>" +
                "UPDATE xxl_job_info " +
                "SET " +
                        "trigger_last_time = CASE id " +
                        "<foreach collection='list' item='item' separator=' '>" +
                                "WHEN #{item.id} THEN #{item.triggerLastTime} " +
                        "</foreach>" +
                        "ELSE trigger_last_time END, " +
                        "trigger_next_time = CASE id " +
                        "<foreach collection='list' item='item' separator=' '>" +
                                "WHEN #{item.id} THEN #{item.triggerNextTime} " +
                        "</foreach>" +
                        "ELSE trigger_next_time END, " +
                        "trigger_status = CASE id " +
                        "<foreach collection='list' item='item' separator=' '>" +
                                "WHEN #{item.id} THEN " +
                                "<choose>" +
                                        "<when test='item.triggerStatus != null and item.triggerStatus gte 0'>" + 
                                                " #{item.triggerStatus}" +
                                        "</when>" +
                                        "<otherwise>trigger_status</otherwise>" +
                                "</choose> " +
                        "</foreach>" +
                        "ELSE trigger_status END " +
                "WHERE id IN " +
                        "<foreach collection='list' item='item' open='(' separator=',' close=')'>#{item.id}</foreach>" +
                        "AND trigger_status = 1" +
            "</script>")
    int scheduleBatchUpdate(@Param("list") List<XxlJobInfo> jobInfoList);
}
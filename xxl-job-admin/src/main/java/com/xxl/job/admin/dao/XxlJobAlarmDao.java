package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobAlarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created on 2022/2/23.
 *
 * @author lan
 */
@Mapper
public interface XxlJobAlarmDao {

    List<XxlJobAlarm> findByJobId(@Param("jobId") int jobId);

    void deleteByJobId(@Param("jobId") int jobId);

    void batchSave(@Param("dataList") List<XxlJobAlarm> dataList);
}

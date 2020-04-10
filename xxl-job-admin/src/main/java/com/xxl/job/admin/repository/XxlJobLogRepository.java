package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author YunSongLiu
 */
public interface XxlJobLogRepository   extends JpaRepository<XxlJobLog, Long>, JpaSpecificationExecutor<XxlJobLog> {

    void deleteXxlJobLogsByJobId(int jobId);

    XxlJobLog queryXxlJobLogByIdEqualsAndAlarmStatusEquals(long id,int alarmStatus);

}

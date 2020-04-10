package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author YunSongLiu
 */
public interface XxlJobInfoRepository extends JpaRepository<XxlJobInfo, Integer>, JpaSpecificationExecutor<XxlJobInfo> {

    List<XxlJobInfo> queryXxlJobInfosByJobGroupEquals(int jobGroup);

    @Modifying
    @Query("update XxlJobInfo set triggerLastTime = ?2 , triggerNextTime = ?3,triggerStatus = ?4 where id = ?1")
    void updateSchedule(int id, long triggerLastTime, long triggerNextTime, int triggerStatus);
}

package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author YunSongLiu
 */
public interface XxlJobInfoRepository extends JpaRepository<XxlJobInfo, Integer>, JpaSpecificationExecutor<XxlJobInfo> {

    List<XxlJobInfo> queryXxlJobInfosByJobGroupEquals(int jobGroup);

}

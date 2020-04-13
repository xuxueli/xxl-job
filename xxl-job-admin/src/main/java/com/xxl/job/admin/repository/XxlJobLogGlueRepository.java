package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author YunSongLiu
 */
public interface XxlJobLogGlueRepository extends JpaRepository<XxlJobLogGlue, Integer>, JpaSpecificationExecutor<XxlJobLogGlue> {

    List<XxlJobLogGlue> findByJobIdEqualsOrderByIdDesc(int jobId);

    void deleteAllByJobIdEquals(int jobId);

}

package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * @author dudiao
 * @date 2020/3/23 下午 04:56
 */
@Repository
public interface XxlJobLockDao extends JpaRepository<XxlJobLock, String> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select jl from XxlJobLock jl")
    List<XxlJobLock> getJobLockForUpdate();

}

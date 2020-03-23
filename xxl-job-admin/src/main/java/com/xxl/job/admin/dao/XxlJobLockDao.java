package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dudiao
 * @date 2020/3/23 下午 04:56
 */
@Repository
public interface XxlJobLockDao extends JpaRepository<XxlJobLock, String> {

}

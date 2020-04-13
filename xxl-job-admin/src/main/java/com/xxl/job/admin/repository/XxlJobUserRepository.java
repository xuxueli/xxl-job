package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author YunSongLiu
 */
public interface XxlJobUserRepository extends JpaRepository<XxlJobUser, Integer>, JpaSpecificationExecutor<XxlJobUser> {

    XxlJobUser findByUsernameEquals(String username);

}

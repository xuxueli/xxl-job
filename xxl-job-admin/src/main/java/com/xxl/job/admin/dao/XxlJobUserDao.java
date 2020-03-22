package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
@Repository
public interface XxlJobUserDao extends JpaRepository<XxlJobUser, Long>, JpaSpecificationExecutor<XxlJobUser> {

	@Query("select t from XxlJobUser t where t.username = :username")
	public XxlJobUser loadByUserName(@Param("username") String username);

	@Transactional
	@Modifying
	@Query("update XxlJobUser t set t.password = " +
			"case when :#{#xxlJobUser.password} is null then t.password else :#{#xxlJobUser.password} end, " +
			"t.role = :#{#xxlJobUser.role}, t.permission = :#{#xxlJobUser.permission} where t.id = :id")
	public int update(XxlJobUser xxlJobUser);

	@Transactional
	@Modifying
	@Query("delete from XxlJobUser t where t.id = :id")
	public int delete(@Param("id") long id);

}

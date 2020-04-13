package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
public interface XxlJobUserDao {

	public Page<XxlJobUser> pageList(@Param("offset") int offset,
									 @Param("pagesize") int pagesize,
									 @Param("username") String username,
									 @Param("role") int role);
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("username") String username,
							 @Param("role") int role);

	public XxlJobUser loadByUserName(@Param("username") String username);

	public int save(XxlJobUser xxlJobUser);

	public int update(XxlJobUser xxlJobUser);
	
	public int delete(@Param("id") int id);

}

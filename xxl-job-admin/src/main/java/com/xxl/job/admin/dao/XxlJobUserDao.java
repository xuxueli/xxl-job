package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
@Mapper
public interface XxlJobUserDao {

	public List<XxlJobUser> pageList(@Param("username") String username, @Param("role") int role);

	public int pageListCount(@Param("username") String username, @Param("role") int role);

	public XxlJobUser loadByUserName(@Param("username") String username);

	public void save(XxlJobUser xxlJobUser);

	public int update(XxlJobUser xxlJobUser);
	
	public int delete(@Param("id") long id);

}

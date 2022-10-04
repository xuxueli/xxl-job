package com.xxl.job.admin.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.xxl.job.admin.core.model.QXxlJobUser;
import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobUserDao;

@Component
@Transactional
public class XxlJobUserDaoImpl implements XxlJobUserDao {

	@Autowired
	EntityManager em;

	@Override
	public List<XxlJobUser> pageList(int offset, int pagesize, String username, int role) {
		JPAQuery<XxlJobUser> jpaQuery = new JPAQueryFactory(em).select(QXxlJobUser.xxlJobUser)
				.from(QXxlJobUser.xxlJobUser);
		if (username != null && username.length() > 0) {
			jpaQuery.where(QXxlJobUser.xxlJobUser.username.indexOf(username).gt(-1));
		}
		if (role > -1) {
			jpaQuery.where(QXxlJobUser.xxlJobUser.role.eq(role));
		}
		jpaQuery.orderBy(QXxlJobUser.xxlJobUser.username.asc());
		jpaQuery.offset(offset).limit(pagesize);
		return jpaQuery.fetch();
	}

	@Override
	public int pageListCount(int offset, int pagesize, String username, int role) {
		JPAQuery<Long> jpaQuery = new JPAQueryFactory(em).select(QXxlJobUser.xxlJobUser.count())
				.from(QXxlJobUser.xxlJobUser);
		if (username != null && username.length() > 0) {
			jpaQuery.where(QXxlJobUser.xxlJobUser.username.indexOf(username).gt(-1));
		}
		if (role > -1) {
			jpaQuery.where(QXxlJobUser.xxlJobUser.role.eq(role));
		}
		return jpaQuery.fetchFirst().intValue();
	}

	@Override
	public XxlJobUser loadByUserName(String username) {
		return new JPAQueryFactory(em).select(QXxlJobUser.xxlJobUser).from(QXxlJobUser.xxlJobUser)
				.where(QXxlJobUser.xxlJobUser.username.eq(username)).fetchFirst();
	}

	@Override
	public int save(XxlJobUser xxlJobUser) {
		XxlJobUser pi = em.merge(xxlJobUser);
		BeanUtils.copyProperties(pi, xxlJobUser);
		return xxlJobUser.getId();
	}

	@Override
	public int update(XxlJobUser xxlJobUser) {
		JPAUpdateClause update = new JPAQueryFactory(em).update(QXxlJobUser.xxlJobUser);
		if (xxlJobUser.getPassword() != null && xxlJobUser.getPassword().length() > 0) {
			update.set(QXxlJobUser.xxlJobUser.password, xxlJobUser.getPassword());
		}
		update.set(QXxlJobUser.xxlJobUser.role, xxlJobUser.getRole());
		update.set(QXxlJobUser.xxlJobUser.permission, xxlJobUser.getPermission());
		update.where(QXxlJobUser.xxlJobUser.id.eq(xxlJobUser.getId()));
		return (int) update.execute();
	}

	@Override
	public int delete(int id) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobUser.xxlJobUser).where(QXxlJobUser.xxlJobUser.id.eq(id))
				.execute();
	}

}

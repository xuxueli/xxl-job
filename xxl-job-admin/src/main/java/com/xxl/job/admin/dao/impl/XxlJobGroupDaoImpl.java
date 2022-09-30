package com.xxl.job.admin.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xxl.job.admin.core.model.QXxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupDao;

@Component
@Transactional
public class XxlJobGroupDaoImpl implements XxlJobGroupDao {

	@Autowired
	EntityManager em;

	@Override
	public List<XxlJobGroup> findAll() {
		return new JPAQueryFactory(em).select(QXxlJobGroup.xxlJobGroup).from(QXxlJobGroup.xxlJobGroup)
				.orderBy(QXxlJobGroup.xxlJobGroup.appname.asc(), QXxlJobGroup.xxlJobGroup.title.asc(),
						QXxlJobGroup.xxlJobGroup.id.asc())
				.fetch();
	}

	@Override
	public List<XxlJobGroup> findByAddressType(int addressType) {
		return new JPAQueryFactory(em).select(QXxlJobGroup.xxlJobGroup).from(QXxlJobGroup.xxlJobGroup)
				.where(QXxlJobGroup.xxlJobGroup.addressType.eq(addressType))
				.orderBy(QXxlJobGroup.xxlJobGroup.appname.asc(), QXxlJobGroup.xxlJobGroup.title.asc(),
						QXxlJobGroup.xxlJobGroup.id.asc())
				.fetch();
	}

	@Override
	public int save(XxlJobGroup xxlJobGroup) {
		XxlJobGroup pi = em.merge(xxlJobGroup);
		BeanUtils.copyProperties(pi, xxlJobGroup);
		return 1;
	}

	@Override
	public int update(XxlJobGroup xxlJobGroup) {
		return (int) new JPAQueryFactory(em).update(QXxlJobGroup.xxlJobGroup)
				.set(QXxlJobGroup.xxlJobGroup.appname, xxlJobGroup.getAppname())
				.set(QXxlJobGroup.xxlJobGroup.title, xxlJobGroup.getTitle())
				.set(QXxlJobGroup.xxlJobGroup.addressType, xxlJobGroup.getAddressType())
				.set(QXxlJobGroup.xxlJobGroup.addressList, xxlJobGroup.getAddressList())
				.set(QXxlJobGroup.xxlJobGroup.updateTime, xxlJobGroup.getUpdateTime())
				.where(QXxlJobGroup.xxlJobGroup.id.eq(xxlJobGroup.getId())).execute();
	}

	@Override
	public int remove(int id) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobGroup.xxlJobGroup).where(QXxlJobGroup.xxlJobGroup.id.eq(id))
				.execute();
	}

	@Override
	public XxlJobGroup load(int id) {
		return em.find(XxlJobGroup.class, id);
	}

	@Override
	public List<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title) {
		JPAQuery<XxlJobGroup> jpaQuery = new JPAQueryFactory(em).select(QXxlJobGroup.xxlJobGroup)
				.from(QXxlJobGroup.xxlJobGroup);
		if (appname != null && appname.length() > 0) {
			jpaQuery.where(QXxlJobGroup.xxlJobGroup.appname.indexOf(appname).gt(-1));
		}
		if (title != null && title.length() > 0) {
			jpaQuery.where(QXxlJobGroup.xxlJobGroup.title.indexOf(title).gt(-1));
		}
		jpaQuery.orderBy(QXxlJobGroup.xxlJobGroup.appname.asc(), QXxlJobGroup.xxlJobGroup.title.asc(),
				QXxlJobGroup.xxlJobGroup.id.asc());
		jpaQuery.offset(offset).limit(pagesize);
		return jpaQuery.fetch();
	}

	@Override
	public int pageListCount(int offset, int pagesize, String appname, String title) {
		JPAQuery<Long> jpaQuery = new JPAQueryFactory(em).select(QXxlJobGroup.xxlJobGroup.count())
				.from(QXxlJobGroup.xxlJobGroup);
		if (appname != null && appname.length() > 0) {
			jpaQuery.where(QXxlJobGroup.xxlJobGroup.appname.indexOf(appname).gt(-1));
		}
		if (title != null && title.length() > 0) {
			jpaQuery.where(QXxlJobGroup.xxlJobGroup.title.indexOf(title).gt(-1));
		}
		return jpaQuery.fetchFirst().intValue();
	}

}

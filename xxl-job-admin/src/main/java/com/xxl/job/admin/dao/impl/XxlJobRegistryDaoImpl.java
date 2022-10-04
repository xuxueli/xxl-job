package com.xxl.job.admin.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xxl.job.admin.core.model.QXxlJobRegistry;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.dao.XxlJobRegistryDao;

@Component
@Transactional
public class XxlJobRegistryDaoImpl implements XxlJobRegistryDao {

	@Autowired
	EntityManager em;

	@Override
	public List<Integer> findDead(int timeout, Date nowTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowTime);
		calendar.add(Calendar.SECOND, 0 - timeout);
		Date limitTime = calendar.getTime();
		return new JPAQueryFactory(em).select(QXxlJobRegistry.xxlJobRegistry.id).from(QXxlJobRegistry.xxlJobRegistry)
				.where(QXxlJobRegistry.xxlJobRegistry.updateTime.lt(limitTime)).fetch();
	}

	@Override
	public int removeDead(List<Integer> ids) {
		if (ids != null && ids.size() > 0) {
			return (int) new JPAQueryFactory(em).delete(QXxlJobRegistry.xxlJobRegistry)
					.where(QXxlJobRegistry.xxlJobRegistry.id.in(ids)).execute();
		}
		return 1;
	}

	@Override
	public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowTime);
		calendar.add(Calendar.SECOND, 0 - timeout);
		Date limitTime = calendar.getTime();
		return new JPAQueryFactory(em).select(QXxlJobRegistry.xxlJobRegistry).from(QXxlJobRegistry.xxlJobRegistry)
				.where(QXxlJobRegistry.xxlJobRegistry.updateTime.gt(limitTime)).fetch();
	}

	@Override
	public int registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
		return (int) new JPAQueryFactory(em).update(QXxlJobRegistry.xxlJobRegistry)
				.set(QXxlJobRegistry.xxlJobRegistry.updateTime, updateTime)
				.where(QXxlJobRegistry.xxlJobRegistry.registryGroup.eq(registryGroup),
						QXxlJobRegistry.xxlJobRegistry.registryKey.eq(registryKey),
						QXxlJobRegistry.xxlJobRegistry.registryValue.eq(registryValue))
				.execute();
	}

	@Override
	public int registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime) {
		XxlJobRegistry registry = new XxlJobRegistry();
		registry.setRegistryGroup(registryGroup);
		registry.setRegistryKey(registryKey);
		registry.setRegistryValue(registryValue);
		registry.setUpdateTime(updateTime);
		registry = em.merge(registry);
		return 1;
	}

	@Override
	public int registryDelete(String registryGroup, String registryKey, String registryValue) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobRegistry.xxlJobRegistry)
				.where(QXxlJobRegistry.xxlJobRegistry.registryGroup.eq(registryGroup),
						QXxlJobRegistry.xxlJobRegistry.registryKey.eq(registryKey),
						QXxlJobRegistry.xxlJobRegistry.registryValue.eq(registryValue))
				.execute();
	}

}

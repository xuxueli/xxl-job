package com.xxl.job.admin.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xxl.job.admin.core.model.QXxlJobLogGlue;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;

@Component
@Transactional
public class XxlJobLogGlueDaoImpl implements XxlJobLogGlueDao {

	@Autowired
	EntityManager em;

	@Override
	public int save(XxlJobLogGlue xxlJobLogGlue) {
		XxlJobLogGlue pi = em.merge(xxlJobLogGlue);
		BeanUtils.copyProperties(pi, xxlJobLogGlue);
		return 1;
	}

	@Override
	public List<XxlJobLogGlue> findByJobId(int jobId) {
		return new JPAQueryFactory(em).select(QXxlJobLogGlue.xxlJobLogGlue).from(QXxlJobLogGlue.xxlJobLogGlue)
				.where(QXxlJobLogGlue.xxlJobLogGlue.jobId.eq(jobId)).orderBy(QXxlJobLogGlue.xxlJobLogGlue.id.desc())
				.fetch();
	}

	@Override
	public int removeOld(int jobId, int limit) {
		JPAQuery<Integer> sub = new JPAQuery<Long>().select(QXxlJobLogGlue.xxlJobLogGlue.id)
				.from(QXxlJobLogGlue.xxlJobLogGlue).where(QXxlJobLogGlue.xxlJobLogGlue.jobId.eq(jobId))
				.orderBy(QXxlJobLogGlue.xxlJobLogGlue.updateTime.desc()).offset(limit);
		return (int) new JPAQueryFactory(em).delete(QXxlJobLogGlue.xxlJobLogGlue)
				.where(QXxlJobLogGlue.xxlJobLogGlue.jobId.eq(jobId), QXxlJobLogGlue.xxlJobLogGlue.id.notIn(sub))
				.execute();
	}

	@Override
	public int deleteByJobId(int jobId) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobLogGlue.xxlJobLogGlue)
				.where(QXxlJobLogGlue.xxlJobLogGlue.jobId.eq(jobId)).execute();
	}

}

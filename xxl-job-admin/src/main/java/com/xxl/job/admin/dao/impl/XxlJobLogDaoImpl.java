package com.xxl.job.admin.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xxl.job.admin.core.model.QXxlJobLog;
import com.xxl.job.admin.core.model.QXxlJobRegistry;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.dao.XxlJobLogDao;

@Component
@Transactional
public class XxlJobLogDaoImpl implements XxlJobLogDao {

	@Autowired
	EntityManager em;

	@Override
	public List<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart,
			Date triggerTimeEnd, int logStatus) {
		JPAQuery<XxlJobLog> jpaQuery = new JPAQueryFactory(em).select(QXxlJobLog.xxlJobLog).from(QXxlJobLog.xxlJobLog);
		if (jobId == 0 && jobGroup > 0) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.jobGroup.eq(jobGroup));
		}
		if (jobId > 0) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.jobId.eq(jobId));
		}
		if (triggerTimeStart != null) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerTime.goe(triggerTimeStart));
		}
		if (triggerTimeEnd != null) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerTime.loe(triggerTimeEnd));
		}
		if (logStatus == 1) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.handleCode.eq(200));
		} else if (logStatus == 2) {
			jpaQuery.where(
					QXxlJobLog.xxlJobLog.triggerCode.notIn(0, 200).or(QXxlJobLog.xxlJobLog.handleCode.notIn(0, 200)));
		} else if (logStatus == 3) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerCode.eq(200));
			jpaQuery.where(QXxlJobLog.xxlJobLog.handleCode.eq(0));
		}
		jpaQuery.orderBy(QXxlJobLog.xxlJobLog.triggerTime.desc());
		jpaQuery.offset(offset).limit(pagesize);
		return jpaQuery.fetch();
	}

	@Override
	public int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart,
			Date triggerTimeEnd, int logStatus) {
		JPAQuery<Long> jpaQuery = new JPAQueryFactory(em).select(QXxlJobLog.xxlJobLog.count())
				.from(QXxlJobLog.xxlJobLog);
		if (jobId == 0 && jobGroup > 0) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.jobGroup.eq(jobGroup));
		}
		if (jobId > 0) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.jobId.eq(jobId));
		}
		if (triggerTimeStart != null) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerTime.goe(triggerTimeStart));
		}
		if (triggerTimeEnd != null) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerTime.loe(triggerTimeEnd));
		}
		if (logStatus == 1) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.handleCode.eq(200));
		} else if (logStatus == 2) {
			jpaQuery.where(
					QXxlJobLog.xxlJobLog.triggerCode.notIn(0, 200).or(QXxlJobLog.xxlJobLog.handleCode.notIn(0, 200)));
		} else if (logStatus == 3) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerCode.eq(200));
			jpaQuery.where(QXxlJobLog.xxlJobLog.handleCode.eq(0));
		}
		return jpaQuery.fetchFirst().intValue();
	}

	@Override
	public XxlJobLog load(long id) {
		return em.find(XxlJobLog.class, id);
	}

	@Override
	public long save(XxlJobLog xxlJobLog) {
		XxlJobLog pi = em.merge(xxlJobLog);
		BeanUtils.copyProperties(pi, xxlJobLog);
		return 1;
	}

	@Override
	public int updateTriggerInfo(XxlJobLog xxlJobLog) {
		return (int) new JPAQueryFactory(em).update(QXxlJobLog.xxlJobLog)
				.set(QXxlJobLog.xxlJobLog.triggerTime, xxlJobLog.getTriggerTime())
				.set(QXxlJobLog.xxlJobLog.triggerCode, xxlJobLog.getTriggerCode())
				.set(QXxlJobLog.xxlJobLog.triggerMsg, xxlJobLog.getTriggerMsg())
				.set(QXxlJobLog.xxlJobLog.executorAddress, xxlJobLog.getExecutorAddress())
				.set(QXxlJobLog.xxlJobLog.executorHandler, xxlJobLog.getExecutorHandler())
				.set(QXxlJobLog.xxlJobLog.executorParam, xxlJobLog.getExecutorParam())
				.set(QXxlJobLog.xxlJobLog.executorShardingParam, xxlJobLog.getExecutorShardingParam())
				.set(QXxlJobLog.xxlJobLog.executorFailRetryCount, xxlJobLog.getExecutorFailRetryCount())
				.where(QXxlJobLog.xxlJobLog.id.eq(xxlJobLog.getId())).execute();
	}

	@Override
	public int updateHandleInfo(XxlJobLog xxlJobLog) {
		return (int) new JPAQueryFactory(em).update(QXxlJobLog.xxlJobLog)
				.set(QXxlJobLog.xxlJobLog.handleTime, xxlJobLog.getHandleTime())
				.set(QXxlJobLog.xxlJobLog.handleCode, xxlJobLog.getHandleCode())
				.set(QXxlJobLog.xxlJobLog.handleMsg, xxlJobLog.getHandleMsg())
				.where(QXxlJobLog.xxlJobLog.id.eq(xxlJobLog.getId())).execute();
	}

	@Override
	public int delete(int jobId) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobLog.xxlJobLog).where(QXxlJobLog.xxlJobLog.jobId.eq(jobId))
				.execute();
	}

	@Override
	public Map<String, Object> findLogReport(Date from, Date to) {
		Map<String, Object> map = new HashMap<String, Object>();

		NumberExpression<Integer> triggerDayCountRunning = new CaseBuilder()
				.when(QXxlJobLog.xxlJobLog.triggerCode.in(0, 200).and(QXxlJobLog.xxlJobLog.handleCode.eq(0))).then(1)
				.otherwise(0);
		Tuple tuple = new JPAQueryFactory(em)
				.select(QXxlJobLog.xxlJobLog.handleCode.count(), triggerDayCountRunning.sum(),
						QXxlJobLog.xxlJobLog.handleCode.when(200).then(1).otherwise(0).sum())
				.from(QXxlJobLog.xxlJobLog).where(QXxlJobLog.xxlJobLog.triggerTime.between(from, to)).fetchFirst();
		map.put("triggerDayCount", tuple.get(0, Integer.class));
		map.put("triggerDayCountRunning", tuple.get(1, Integer.class) == null ? 0 : tuple.get(1, Integer.class));
		map.put("triggerDayCountSuc", tuple.get(2, Integer.class) == null ? 0 : tuple.get(2, Integer.class));
		return map;
	}

	@Override
	public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
		JPAQuery<Long> jpaQuery = new JPAQueryFactory(em).select(QXxlJobLog.xxlJobLog.id).from(QXxlJobLog.xxlJobLog);
		if (jobGroup > 0) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.jobGroup.eq(jobGroup));
		}
		if (jobId > 0) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.jobId.eq(jobId));
		}
		if (clearBeforeTime != null) {
			jpaQuery.where(QXxlJobLog.xxlJobLog.triggerTime.loe(clearBeforeTime));
		}
		if (clearBeforeNum > 0) {
			JPAQuery<Long> sub = new JPAQuery<Long>().select(QXxlJobLog.xxlJobLog.id);
			if (jobGroup > 0) {
				sub.where(QXxlJobLog.xxlJobLog.jobGroup.eq(jobGroup));
			}
			if (jobId > 0) {
				sub.where(QXxlJobLog.xxlJobLog.jobId.eq(jobId));
			}
			sub.orderBy(QXxlJobLog.xxlJobLog.triggerTime.desc());
			sub.offset(0);
			sub.limit(clearBeforeNum);
			jpaQuery.where(QXxlJobLog.xxlJobLog.id.notIn(sub));
		}
		jpaQuery.orderBy(QXxlJobLog.xxlJobLog.id.asc());
		jpaQuery.limit(pagesize);
		return jpaQuery.fetch();
	}

	@Override
	public int clearLog(List<Long> logIds) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobLog.xxlJobLog).where(QXxlJobLog.xxlJobLog.id.in(logIds))
				.execute();
	}

	@Override
	public List<Long> findFailJobLogIds(int pagesize) {
		return new JPAQueryFactory(em).select(QXxlJobLog.xxlJobLog.id).from(QXxlJobLog.xxlJobLog)
				.where(QXxlJobLog.xxlJobLog.alarmStatus.eq(0),
						QXxlJobLog.xxlJobLog.triggerCode.in(0, 200).and(QXxlJobLog.xxlJobLog.handleCode.eq(0))
								.or(QXxlJobLog.xxlJobLog.handleCode.eq(200)).not())
				.orderBy(QXxlJobLog.xxlJobLog.id.asc()).fetch();
	}

	@Override
	public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
		return (int) new JPAQueryFactory(em).update(QXxlJobLog.xxlJobLog)
				.set(QXxlJobLog.xxlJobLog.alarmStatus, newAlarmStatus)
				.where(QXxlJobLog.xxlJobLog.id.eq(logId), QXxlJobLog.xxlJobLog.alarmStatus.eq(oldAlarmStatus))
				.execute();
	}

	@Override
	public List<Long> findLostJobIds(Date losedTime) {
		return new JPAQueryFactory(em).select(QXxlJobLog.xxlJobLog.id).from(QXxlJobLog.xxlJobLog)
				.leftJoin(QXxlJobRegistry.xxlJobRegistry)
				.on(QXxlJobLog.xxlJobLog.executorAddress.eq(QXxlJobRegistry.xxlJobRegistry.registryValue))
				.where(QXxlJobLog.xxlJobLog.triggerCode.eq(200), QXxlJobLog.xxlJobLog.handleCode.eq(0),
						QXxlJobLog.xxlJobLog.triggerTime.loe(losedTime), QXxlJobRegistry.xxlJobRegistry.id.isNull())
				.fetch();
	}

}

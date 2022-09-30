package com.xxl.job.admin.dao.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xxl.job.admin.core.model.QXxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobInfoDao;

@Component
@Transactional
public class XxlJobInfoDaoImpl implements XxlJobInfoDao {

	@Autowired
	EntityManager em;

	@Override
	public List<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc,
			String executorHandler, String author) {
		JPAQuery<XxlJobInfo> jpaQuery = new JPAQueryFactory(em).select(QXxlJobInfo.xxlJobInfo)
				.from(QXxlJobInfo.xxlJobInfo);
		if (jobGroup > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.jobGroup.eq(jobGroup));
		}
		if (triggerStatus >= 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.triggerStatus.eq(triggerStatus));
		}
		if (jobDesc != null && jobDesc.length() > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.jobDesc.indexOf(jobDesc).gt(-1));
		}
		if (executorHandler != null && executorHandler.length() > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.executorHandler.indexOf(executorHandler).gt(-1));
		}
		if (author != null && author.length() > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.author.indexOf(author).gt(-1));
		}
		jpaQuery.orderBy(QXxlJobInfo.xxlJobInfo.id.desc());
		jpaQuery.offset(offset).limit(pagesize);
		return jpaQuery.fetch();
	}

	@Override
	public int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc,
			String executorHandler, String author) {
		JPAQuery<Long> jpaQuery = new JPAQueryFactory(em).select(QXxlJobInfo.xxlJobInfo.count())
				.from(QXxlJobInfo.xxlJobInfo);
		if (jobGroup > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.jobGroup.eq(jobGroup));
		}
		if (triggerStatus >= 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.triggerStatus.eq(triggerStatus));
		}
		if (jobDesc != null && jobDesc.length() > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.jobDesc.indexOf(jobDesc).gt(-1));
		}
		if (executorHandler != null && executorHandler.length() > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.executorHandler.indexOf(executorHandler).gt(-1));
		}
		if (author != null && author.length() > 0) {
			jpaQuery.where(QXxlJobInfo.xxlJobInfo.author.indexOf(author).gt(-1));
		}
		return jpaQuery.fetchFirst().intValue();
	}

	@Override
	public int save(XxlJobInfo info) {
		if (info.getGlueSource() != null && info.getGlueSource().length() == 0) {
			info.setGlueSource(null);
		}
		XxlJobInfo pi = em.merge(info);
		BeanUtils.copyProperties(pi, info);
		return 1;
	}

	@Override
	public XxlJobInfo loadById(int id) {
		return em.find(XxlJobInfo.class, id);
	}

	@Override
	public int update(XxlJobInfo xxlJobInfo) {
		if (xxlJobInfo.getGlueSource() != null && xxlJobInfo.getGlueSource().length() == 0) {
			xxlJobInfo.setGlueSource(null);
		}
		return (int) new JPAQueryFactory(em).update(QXxlJobInfo.xxlJobInfo)
				.set(QXxlJobInfo.xxlJobInfo.jobGroup, xxlJobInfo.getJobGroup())
				.set(QXxlJobInfo.xxlJobInfo.jobDesc, xxlJobInfo.getJobDesc())
				
				.set(QXxlJobInfo.xxlJobInfo.updateTime, xxlJobInfo.getUpdateTime())
				.set(QXxlJobInfo.xxlJobInfo.author, xxlJobInfo.getAuthor())
				.set(QXxlJobInfo.xxlJobInfo.alarmEmail, xxlJobInfo.getAlarmEmail())
				
				.set(QXxlJobInfo.xxlJobInfo.scheduleType, xxlJobInfo.getScheduleType())
				.set(QXxlJobInfo.xxlJobInfo.scheduleConf, xxlJobInfo.getScheduleConf())
				.set(QXxlJobInfo.xxlJobInfo.misfireStrategy, xxlJobInfo.getMisfireStrategy())
				
				.set(QXxlJobInfo.xxlJobInfo.executorRouteStrategy, xxlJobInfo.getExecutorRouteStrategy())
				.set(QXxlJobInfo.xxlJobInfo.executorHandler, xxlJobInfo.getExecutorHandler())
				.set(QXxlJobInfo.xxlJobInfo.executorParam, xxlJobInfo.getExecutorParam())
				.set(QXxlJobInfo.xxlJobInfo.executorBlockStrategy, xxlJobInfo.getExecutorBlockStrategy())
				.set(QXxlJobInfo.xxlJobInfo.executorTimeout, xxlJobInfo.getExecutorTimeout())
				.set(QXxlJobInfo.xxlJobInfo.executorFailRetryCount, xxlJobInfo.getExecutorFailRetryCount())
				
				.set(QXxlJobInfo.xxlJobInfo.glueType, xxlJobInfo.getGlueType())
				.set(QXxlJobInfo.xxlJobInfo.glueSource, xxlJobInfo.getGlueSource())
				.set(QXxlJobInfo.xxlJobInfo.glueRemark, xxlJobInfo.getGlueRemark())
				.set(QXxlJobInfo.xxlJobInfo.glueUpdatetime, xxlJobInfo.getGlueUpdatetime())
				
				.set(QXxlJobInfo.xxlJobInfo.childJobId, xxlJobInfo.getChildJobId())
				.set(QXxlJobInfo.xxlJobInfo.triggerStatus, xxlJobInfo.getTriggerStatus())
				.set(QXxlJobInfo.xxlJobInfo.triggerLastTime, xxlJobInfo.getTriggerLastTime())
				.set(QXxlJobInfo.xxlJobInfo.triggerNextTime, xxlJobInfo.getTriggerNextTime())
				.where(QXxlJobInfo.xxlJobInfo.id.eq(xxlJobInfo.getId())).execute();
	}

	@Override
	public int delete(long id) {
		return (int) new JPAQueryFactory(em).delete(QXxlJobInfo.xxlJobInfo)
				.where(QXxlJobInfo.xxlJobInfo.id.eq((int) id)).execute();
	}

	@Override
	public List<XxlJobInfo> getJobsByGroup(int jobGroup) {
		return new JPAQueryFactory(em).select(QXxlJobInfo.xxlJobInfo).from(QXxlJobInfo.xxlJobInfo)
				.where(QXxlJobInfo.xxlJobInfo.jobGroup.eq(jobGroup)).fetch();
	}

	@Override
	public int findAllCount() {
		return new JPAQueryFactory(em).select(QXxlJobInfo.xxlJobInfo.count()).from(QXxlJobInfo.xxlJobInfo).fetchFirst()
				.intValue();
	}

	@Override
	public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
		return new JPAQueryFactory(em).select(QXxlJobInfo.xxlJobInfo).from(QXxlJobInfo.xxlJobInfo)
				.where(QXxlJobInfo.xxlJobInfo.triggerStatus.eq(1),
						QXxlJobInfo.xxlJobInfo.triggerNextTime.loe(maxNextTime))
				.orderBy(QXxlJobInfo.xxlJobInfo.id.asc()).limit(pagesize).fetch();
	}

	@Override
	public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
		return (int) new JPAQueryFactory(em).update(QXxlJobInfo.xxlJobInfo)
				.set(QXxlJobInfo.xxlJobInfo.triggerLastTime, xxlJobInfo.getTriggerLastTime())
				.set(QXxlJobInfo.xxlJobInfo.triggerNextTime, xxlJobInfo.getTriggerNextTime())
				.set(QXxlJobInfo.xxlJobInfo.triggerStatus, xxlJobInfo.getTriggerStatus())
				.where(QXxlJobInfo.xxlJobInfo.id.eq(xxlJobInfo.getId())).execute();
	}

}

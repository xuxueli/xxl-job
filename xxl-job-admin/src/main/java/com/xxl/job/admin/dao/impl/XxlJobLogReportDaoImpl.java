package com.xxl.job.admin.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xxl.job.admin.core.model.QXxlJobLogReport;
import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.dao.XxlJobLogReportDao;

@Component
@Transactional
public class XxlJobLogReportDaoImpl implements XxlJobLogReportDao {

	@Autowired
	EntityManager em;

	@Override
	public int save(XxlJobLogReport xxlJobLogReport) {
		XxlJobLogReport pi = em.merge(xxlJobLogReport);
		BeanUtils.copyProperties(pi, xxlJobLogReport);
		return 1;
	}

	@Override
	public int update(XxlJobLogReport xxlJobLogReport) {
		return (int) new JPAQueryFactory(em).update(QXxlJobLogReport.xxlJobLogReport)
				.set(QXxlJobLogReport.xxlJobLogReport.runningCount, xxlJobLogReport.getRunningCount())
				.set(QXxlJobLogReport.xxlJobLogReport.sucCount, xxlJobLogReport.getSucCount())
				.set(QXxlJobLogReport.xxlJobLogReport.failCount, xxlJobLogReport.getFailCount())
				.where(QXxlJobLogReport.xxlJobLogReport.triggerDay.eq(xxlJobLogReport.getTriggerDay())).execute();
	}

	@Override
	public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
		return new JPAQueryFactory(em).select(QXxlJobLogReport.xxlJobLogReport).from(QXxlJobLogReport.xxlJobLogReport)
				.where(QXxlJobLogReport.xxlJobLogReport.triggerDay.between(triggerDayFrom, triggerDayTo))
				.orderBy(QXxlJobLogReport.xxlJobLogReport.triggerDay.asc()).fetch();
	}

	@Override
	public XxlJobLogReport queryLogReportTotal() {

		Tuple tuple = new JPAQueryFactory(em).select(QXxlJobLogReport.xxlJobLogReport.runningCount.sum(),
				QXxlJobLogReport.xxlJobLogReport.sucCount.sum(), QXxlJobLogReport.xxlJobLogReport.failCount.sum())
				.from(QXxlJobLogReport.xxlJobLogReport).fetchFirst();
		XxlJobLogReport report = null;
		if (tuple != null) {
			report = new XxlJobLogReport();
			Object[] arr = tuple.toArray();
			if (arr[0] != null) {
				report.setRunningCount(Integer.parseInt(arr[0].toString()));
			}
			if (arr[1] != null) {
				report.setSucCount(Integer.parseInt(arr[1].toString()));
			}
			if (arr[2] != null) {
				report.setFailCount(Integer.parseInt(arr[2].toString()));
			}
		}
		return report;
	}

}

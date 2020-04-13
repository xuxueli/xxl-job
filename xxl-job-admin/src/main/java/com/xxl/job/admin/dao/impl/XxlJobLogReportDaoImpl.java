package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import com.xxl.job.admin.dao.XxlJobLogReportDao;
import com.xxl.job.admin.repository.XxlJobLogReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobLogReportDaoImpl implements XxlJobLogReportDao {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(XxlJobLogReport xxlJobLogReport) {
        entityManager.persist(xxlJobLogReport);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(XxlJobLogReport xxlJobLogReport) {
        XxlJobLogReport data = xxlJobLogReportRepository.findByTriggerDayEquals(xxlJobLogReport.getTriggerDay());
        if (data == null) {
            return 0;
        }

        data.setRunningCount(xxlJobLogReport.getRunningCount());
        data.setSucCount(xxlJobLogReport.getSucCount());
        data.setFailCount(xxlJobLogReport.getFailCount());

        entityManager.merge(data);
        return 1;
    }

    @Override
    public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
        return xxlJobLogReportRepository.queryXxlJobLogReportsByTriggerDayBetweenOrderByTriggerDayAsc(triggerDayFrom,triggerDayTo);
    }

    @Override
    public XxlJobLogReport queryLogReportTotal() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<XxlJobLogReport> root = criteriaQuery.from(XxlJobLogReport.class);
        criteriaQuery.multiselect(
                criteriaBuilder.sum(root.get("runningCount")).alias("runningCount"),
                criteriaBuilder.sum(root.get("sucCount")).alias("sucCount"),
                criteriaBuilder.sum(root.get("failCount")).alias("failCount")
        );

        Tuple result = entityManager.createQuery(criteriaQuery).getSingleResult();

        XxlJobLogReport xxlJobLogReport = new XxlJobLogReport();
        xxlJobLogReport.setFailCount(((Long)result.get(2)).intValue());
        xxlJobLogReport.setSucCount(((Long)result.get(1)).intValue());
        xxlJobLogReport.setRunningCount(((Long)result.get(0)).intValue());

        return xxlJobLogReport;
    }

    @Autowired
    private XxlJobLogReportRepository xxlJobLogReportRepository;

    @Autowired
    private EntityManager entityManager;
}

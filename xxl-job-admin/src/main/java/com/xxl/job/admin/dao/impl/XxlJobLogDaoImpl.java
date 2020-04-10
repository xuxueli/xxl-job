package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.repository.XxlJobLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobLogDaoImpl implements XxlJobLogDao {

    @Override
    public Page<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        Pageable pageable = PageRequest.of(offset / pagesize, pagesize, Sort.Direction.DESC, "triggerTime");
        Specification<XxlJobLog> specification = (Specification<XxlJobLog>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            if (jobId == 0 && jobGroup > 0) {
                expressions.add(criteriaBuilder.equal(root.get("jobGroup"), jobGroup));
            }
            if (jobId > 0) {
                expressions.add(criteriaBuilder.equal(root.get("jobId"), jobId));
            }
            if (triggerTimeStart != null) {
                expressions.add(criteriaBuilder.greaterThanOrEqualTo(root.get("triggerTimeStart"), triggerTimeStart));
            }
            if (triggerTimeEnd != null) {
                expressions.add(criteriaBuilder.lessThanOrEqualTo(root.get("triggerTimeEnd"), triggerTimeEnd));
            }
            if (logStatus == 1) {
                expressions.add(criteriaBuilder.equal(root.get("handleCode"), 200));
            } else if (logStatus == 2) {
                CriteriaBuilder.In<Object> in1 = criteriaBuilder.in(root.get("triggerCode"));
                in1.value(200);
                in1.value(0);
                CriteriaBuilder.In<Object> in2 = criteriaBuilder.in(root.get("handleCode"));
                in2.value(200);
                in2.value(0);
                expressions.add(criteriaBuilder.or(criteriaBuilder.not(in1), criteriaBuilder.not(in2)));
            } else if (logStatus == 3) {
                expressions.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("triggerCode"), 200), criteriaBuilder.equal(root.get("handleCode"), 0)));
            }
            return predicate;
        };

        return xxlJobLogRepository.findAll(specification, pageable);
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        return 0;
    }

    @Override
    public XxlJobLog load(long id) {
        return xxlJobLogRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public long save(XxlJobLog xxlJobLog) {
        entityManager.persist(xxlJobLog);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional
    public int updateTriggerInfo(XxlJobLog xxlJobLog) {
        XxlJobLog data = load(xxlJobLog.getId());
        if (data == null) {
            return 1;
        }
        data.setTriggerTime(xxlJobLog.getTriggerTime());
        data.setTriggerCode(xxlJobLog.getTriggerCode());
        data.setTriggerMsg(xxlJobLog.getTriggerMsg());
        data.setExecutorAddress(xxlJobLog.getExecutorAddress());
        data.setExecutorHandler(xxlJobLog.getExecutorHandler());
        data.setExecutorParam(xxlJobLog.getExecutorParam());
        data.setExecutorShardingParam(xxlJobLog.getExecutorShardingParam());
        data.setExecutorFailRetryCount(xxlJobLog.getExecutorFailRetryCount());
        entityManager.merge(data);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional
    public int updateHandleInfo(XxlJobLog xxlJobLog) {
        XxlJobLog data = load(xxlJobLog.getId());
        if (data == null) {
            return 1;
        }
        data.setHandleTime(xxlJobLog.getHandleTime());
        data.setHandleCode(xxlJobLog.getHandleCode());
        data.setHandleMsg(xxlJobLog.getHandleMsg());
        entityManager.merge(data);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional
    public int delete(int jobId) {
        xxlJobLogRepository.deleteXxlJobLogsByJobId(jobId);
        return 0;
    }

    @Override
    public Map<String, Object> findLogReport(Date from, Date to) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<XxlJobLog> root = criteriaQuery.from(XxlJobLog.class);
        Path<Integer> handleCode = root.get("handleCode");
        Path<Integer> triggerCode = root.get("triggerCode");
        CriteriaBuilder.In<Object> in1 = criteriaBuilder.in(triggerCode);
        in1.value(200);
        in1.value(0);
        criteriaQuery.multiselect(
                criteriaBuilder.count(handleCode).alias("triggerDayCount"),
                criteriaBuilder
                        .sum(criteriaBuilder.selectCase()
                                .when(criteriaBuilder.and(in1, criteriaBuilder.equal(handleCode, 0)), 1)
                                .otherwise(0).as(Long.class)).alias("triggerDayCountRunning"),
                criteriaBuilder.sum(criteriaBuilder.selectCase().when(criteriaBuilder.equal(handleCode, 200), 1).otherwise(0).as(Long.class)).alias("triggerDayCountSuc")
        ).where(
                criteriaBuilder.between(root.get("triggerTime"), from, to)
        );
        Tuple result = entityManager.createQuery(criteriaQuery).getSingleResult();
        Map<String,Object> data = new HashMap<>();
        data.put("triggerDayCount",result.get(0));
        data.put("triggerDayCountRunning",result.get(1));
        data.put("triggerTime",result.get(2));
        return data;
    }

    @Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {



        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<XxlJobLog> root = criteriaQuery.from(XxlJobLog.class);
        Predicate predicate = criteriaBuilder.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        if (jobGroup > 0) {
            expressions.add(criteriaBuilder.equal(root.get("jobGroup"), jobGroup));
        }
        if (jobId > 0) {
            expressions.add(criteriaBuilder.equal(root.get("jobId"), jobId));
        }
        if (clearBeforeTime != null) {
            expressions.add(criteriaBuilder.lessThanOrEqualTo(root.get("triggerTime"), clearBeforeTime));
        }
        if (clearBeforeNum > 0) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> subQuery = cb.createQuery(Long.class);
            Root<XxlJobLog> subRoot = subQuery.from(XxlJobLog.class);
            Predicate subPredicate = cb.conjunction();
            if (jobGroup > 0) {
                subPredicate.getExpressions().add(cb.equal(subRoot.get("jobGroup"), jobGroup));
            }
            if (jobId > 0) {
                subPredicate.getExpressions().add(cb.equal(subRoot.get("jobId"), jobId));
            }
            subQuery.select(subRoot.get("id").as(Long.class)).where(subPredicate).orderBy(cb.desc(subRoot.get("triggerTime")));
            List<Long> idList = entityManager.createQuery(subQuery).setMaxResults(clearBeforeNum).getResultList();

            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
            in.value(idList);

            expressions.add(criteriaBuilder.not(in));
        }

        criteriaQuery.select(root.get("id").as(Long.class)).where(predicate).orderBy(criteriaBuilder.asc(root.get("id")));
        return entityManager.createQuery(criteriaQuery).setMaxResults(pagesize).getResultList();
    }

    @Override
    @Transactional
    public int clearLog(List<Long> logIds) {
        for (Long id : logIds) {
            xxlJobLogRepository.deleteById(id);
        }
        return 0;
    }

    @Override
    public List<Long> findFailJobLogIds(int pagesize) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<XxlJobLog> root = criteriaQuery.from(XxlJobLog.class);
        Predicate predicate = criteriaBuilder.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();
        Path<Integer> handleCode = root.get("handleCode");
        Path<Integer> triggerCode = root.get("triggerCode");
        CriteriaBuilder.In<Object> in = criteriaBuilder.in(triggerCode);
        in.value(Arrays.asList(0,200));

        expressions.add(
                criteriaBuilder.not(
                        criteriaBuilder.or(
                                criteriaBuilder.and(in,criteriaBuilder.equal(handleCode,0)),
                        criteriaBuilder.equal(handleCode,200))));

        expressions.add(criteriaBuilder.equal(root.get("alarmStatus"),0));

        criteriaQuery.select(root.get("id").as(Long.class)).where(predicate).orderBy(criteriaBuilder.asc(root.get("id")));

        return entityManager.createQuery(criteriaQuery).setMaxResults(pagesize).getResultList();
    }

    @Override
    @Transactional
    public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
        XxlJobLog xxlJobLog = xxlJobLogRepository.queryXxlJobLogByIdEqualsAndAlarmStatusEquals(logId,oldAlarmStatus);
        xxlJobLog.setAlarmStatus(newAlarmStatus);
        entityManager.merge(xxlJobLog);
        entityManager.flush();
        return 1;
    }

    @Autowired
    private XxlJobLogRepository xxlJobLogRepository;
    @Autowired
    private EntityManager entityManager;
}

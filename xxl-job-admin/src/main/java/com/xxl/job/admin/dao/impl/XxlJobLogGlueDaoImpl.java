package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.admin.repository.XxlJobLogGlueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobLogGlueDaoImpl implements XxlJobLogGlueDao {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(XxlJobLogGlue xxlJobLogGlue) {
        entityManager.persist(xxlJobLogGlue);
        entityManager.flush();
        return 1;
    }

    @Override
    public List<XxlJobLogGlue> findByJobId(int jobId) {
        return xxlJobLogGlueRepository.findByJobIdEqualsOrderByIdDesc(jobId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeOld(int jobId, int limit) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<XxlJobLogGlue> criteriaDelete = criteriaBuilder.createCriteriaDelete(XxlJobLogGlue.class);
        Root<XxlJobLogGlue> root = criteriaDelete.from(XxlJobLogGlue.class);
        Predicate predicate = criteriaBuilder.conjunction();
        List<Expression<Boolean>> expressions = predicate.getExpressions();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> subQuery = cb.createQuery(Integer.class);
        Root<XxlJobLogGlue> subRoot = subQuery.from(XxlJobLogGlue.class);
        Predicate subPredicate = cb.equal(subRoot.get("jobId"),jobId);
        Path<Object> updateDate = subRoot.get("updateTime");
        subQuery.select(subRoot.get("id").as(Integer.class)).where(subPredicate).orderBy(cb.desc(updateDate));
        List<Integer> idList = entityManager.createQuery(subQuery).setMaxResults(limit).getResultList();

        CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
        in.value(idList);
        expressions.add(criteriaBuilder.not(in));
        criteriaDelete.where(predicate);

        entityManager.createQuery(criteriaDelete).executeUpdate();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByJobId(int jobId) {
        xxlJobLogGlueRepository.deleteAllByJobIdEquals(jobId);
        return 1;
    }

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private XxlJobLogGlueRepository xxlJobLogGlueRepository;
}

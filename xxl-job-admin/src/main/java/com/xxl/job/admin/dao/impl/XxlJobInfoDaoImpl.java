package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.repository.XxlJobInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobInfoDaoImpl implements XxlJobInfoDao {

    @Override
    public Page<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        Pageable pageable = PageRequest.of(offset / pagesize, pagesize, Sort.Direction.DESC, "id");
        Specification<XxlJobInfo> specification = (Specification<XxlJobInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            if (jobGroup > 0) {
                expressions.add(criteriaBuilder.equal(root.get("jobGroup"), jobGroup));
            }
            if (triggerStatus >= 0) {
                expressions.add(criteriaBuilder.equal(root.get("triggerStatus"),triggerStatus));
            }
            if (!StringUtils.isEmpty(jobDesc)) {
                expressions.add(criteriaBuilder.like(root.get("jobDesc"),"%" +jobDesc + "%"));
            }
            if (!StringUtils.isEmpty(executorHandler)) {
                expressions.add(criteriaBuilder.like(root.get("executorHandler"),"%" + executorHandler + "%"));
            }
            if (!StringUtils.isEmpty(author)) {
                expressions.add(criteriaBuilder.like(root.get("author"),"%" + author + "%"));
            }
            return predicate;
        };

        return xxlJobInfoRepository.findAll(specification,pageable);
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Scope("prototype")
    public int save(XxlJobInfo info) {
        entityManager.persist(info);
        entityManager.flush();
        return 1;
    }

    @Override
    public XxlJobInfo loadById(int id) {
        return xxlJobInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(XxlJobInfo xxlJobInfo) {
        if (loadById(xxlJobInfo.getId()) == null){
            return 0;
        }
        entityManager.merge(xxlJobInfo);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(long id) {
        xxlJobInfoRepository.deleteById((int) id);
        return 1;
    }

    @Override
    public List<XxlJobInfo> getJobsByGroup(int jobGroup) {
        return xxlJobInfoRepository.queryXxlJobInfosByJobGroupEquals(jobGroup);
    }

    @Override
    public int findAllCount() {
        return (int) xxlJobInfoRepository.count();
    }

    @Override
    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
        Pageable pageable = PageRequest.of(0, pagesize, Sort.Direction.ASC, "id");
        Specification<XxlJobInfo> specification = (Specification<XxlJobInfo>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            expressions.add(criteriaBuilder.equal(root.get("triggerStatus"), 1));
            expressions.add(criteriaBuilder.lessThanOrEqualTo(root.get("triggerNextTime"),maxNextTime));
            return predicate;
        };
        return xxlJobInfoRepository.findAll(specification,pageable).getContent();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
        XxlJobInfo data = loadById(xxlJobInfo.getId());
        if (data == null){
            return 0;
        }
        data.setTriggerLastTime(xxlJobInfo.getTriggerLastTime());
        data.setTriggerNextTime(xxlJobInfo.getTriggerNextTime());
        data.setTriggerStatus(xxlJobInfo.getTriggerStatus());
        entityManager.merge(data);
        entityManager.flush();
        return 1;
    }

    @Autowired
    private XxlJobInfoRepository xxlJobInfoRepository;
    @Autowired
    private EntityManager entityManager;
}

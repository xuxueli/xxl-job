package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.admin.repository.XxlJobRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobRegistryDaoImpl implements XxlJobRegistryDao {

    @Override
    public List<Integer> findDead(int timeout, Date nowTime) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
        Root<XxlJobRegistry> root = criteriaQuery.from(XxlJobRegistry.class);
        Predicate predicate = criteriaBuilder.lessThan(root.get("updateTime"),minusDate(nowTime,timeout));
        criteriaQuery.select(root.get("id")).where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeDead(List<Integer> ids) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<XxlJobRegistry> criteriaDelete = criteriaBuilder.createCriteriaDelete(XxlJobRegistry.class);
        Root<XxlJobRegistry> root = criteriaDelete.from(XxlJobRegistry.class);

        CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
        in.value(ids);

        criteriaDelete.where(in);

        entityManager.createQuery(criteriaDelete).executeUpdate();

        return 1;
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<XxlJobRegistry> criteriaQuery = criteriaBuilder.createQuery(XxlJobRegistry.class);
        Root<XxlJobRegistry> root = criteriaQuery.from(XxlJobRegistry.class);
        Predicate predicate = criteriaBuilder.greaterThan(root.get("updateTime"),minusDate(nowTime,timeout));
        criteriaQuery.where(predicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registryUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        XxlJobRegistry xxlJobRegistry = xxlJobRegistryRepository.findByRegistryGroupEqualsAndRegistryValueEqualsAndRegistryKeyEquals(registryGroup,registryValue,registryKey);
        if (xxlJobRegistry == null) {
            return 0;
        }
        xxlJobRegistry.setUpdateTime(updateTime);
        entityManager.persist(xxlJobRegistry);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registrySave(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        XxlJobRegistry xxlJobRegistry = new XxlJobRegistry();
        xxlJobRegistry.setRegistryGroup(registryGroup);
        xxlJobRegistry.setRegistryKey(registryKey);
        xxlJobRegistry.setRegistryValue(registryValue);
        xxlJobRegistry.setUpdateTime(updateTime);
        entityManager.persist(xxlJobRegistry);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<XxlJobRegistry> criteriaDelete = criteriaBuilder.createCriteriaDelete(XxlJobRegistry.class);
        Root<XxlJobRegistry> root = criteriaDelete.from(XxlJobRegistry.class);

        Predicate predicate = criteriaBuilder.conjunction();

        predicate.getExpressions().add(criteriaBuilder.equal(root.get("registryGroup"),registryGroup));
        predicate.getExpressions().add(criteriaBuilder.equal(root.get("registryKey"),registryKey));
        predicate.getExpressions().add(criteriaBuilder.equal(root.get("registryValue"),registryValue));

        criteriaDelete.where(predicate);

        entityManager.createQuery(criteriaDelete).executeUpdate();
        return 0;
    }

    private Date minusDate(Date date, int seconds) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(instant.atZone(zoneId).toLocalDateTime().minusSeconds(seconds).atZone(zoneId).toInstant());
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private XxlJobRegistryRepository xxlJobRegistryRepository;

}

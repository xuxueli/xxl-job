package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.repository.XxlJobGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobGroupDaoImpl implements XxlJobGroupDao {

    @Override
    public List<XxlJobGroup> findAll() {
        return xxlJobGroupRepository.findAll(getGroupSort());
    }

    @Override
    public Page<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title) {

        Pageable pageable = PageRequest.of(offset / pagesize, pagesize, getGroupSort());
        Specification<XxlJobGroup> specification = (Specification<XxlJobGroup>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            if (!StringUtils.isEmpty(appname)) {
                expressions.add(criteriaBuilder.equal(root.get("appname"), appname));
            }
            if (!StringUtils.isEmpty(title)) {
                expressions.add(criteriaBuilder.equal(root.get("title"),title));
            }
            return predicate;
        };
        return xxlJobGroupRepository.findAll(specification,pageable);
    }

    @Override
    public int pageListCount(int offset, int pagesize, String appname, String title) {
        return 0;
    }

    @Override
    public List<XxlJobGroup> findByAddressType(int addressType) {
        return xxlJobGroupRepository.queryXxlJobGroupsByAddressTypeEquals(addressType,getGroupSort());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(XxlJobGroup xxlJobGroup) {
        entityManager.persist(xxlJobGroup);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(XxlJobGroup xxlJobGroup) {
        XxlJobGroup result =  load(xxlJobGroup.getId());
        if (result == null) {
            return 0;
        }
        entityManager.merge(xxlJobGroup);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int remove(int id) {
        XxlJobGroup result =  xxlJobGroupRepository.findById(id).orElse(null);
        if (result == null) {
            return 0;
        }
        entityManager.remove(result);
        entityManager.flush();
        return 1;
    }

    private Sort getGroupSort(){
        Sort.Order order1 = new Sort.Order(Sort.Direction.ASC,"appname");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC,"title");
        Sort.Order order3 = new Sort.Order(Sort.Direction.ASC,"id");
        return Sort.by(Arrays.asList(order1,order2,order3));
    }

    @Override
    public XxlJobGroup load(int id) {
        return xxlJobGroupRepository.findById(id).orElse(null);
    }

    @Autowired
    private XxlJobGroupRepository xxlJobGroupRepository;
    @Autowired
    private EntityManager entityManager;
}

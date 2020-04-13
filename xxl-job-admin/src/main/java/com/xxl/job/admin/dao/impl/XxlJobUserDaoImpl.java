package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobUser;
import com.xxl.job.admin.dao.XxlJobUserDao;
import com.xxl.job.admin.repository.XxlJobUserRepository;
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
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobUserDaoImpl implements XxlJobUserDao {

    @Override
    public Page<XxlJobUser> pageList(int offset, int pagesize, String username, int role) {
        Pageable pageable = PageRequest.of(offset / pagesize, pagesize, Sort.Direction.DESC, "id");
        Specification<XxlJobUser> specification = (Specification<XxlJobUser>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            List<Expression<Boolean>> expressions = predicate.getExpressions();
            if (!StringUtils.isEmpty(username)) {
                expressions.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }
            if (role > -1) {
                expressions.add(criteriaBuilder.equal(root.get("role"), role));
            }
            return predicate;
        };

        return xxlJobUserRepository.findAll(specification, pageable);
    }

    @Override
    public int pageListCount(int offset, int pagesize, String username, int role) {
        return 0;
    }

    @Override
    public XxlJobUser loadByUserName(String username) {
        return xxlJobUserRepository.findByUsernameEquals(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(XxlJobUser xxlJobUser) {
        entityManager.persist(xxlJobUser);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(XxlJobUser xxlJobUser) {
        XxlJobUser data = xxlJobUserRepository.findById(xxlJobUser.getId()).orElse(null);
        if (data == null) {
            return 0;
        }

        if (!StringUtils.isEmpty(xxlJobUser.getPassword())){
            data.setPassword(xxlJobUser.getPassword());
        }
        data.setRole(xxlJobUser.getRole());
        data.setPermission(xxlJobUser.getPermission());
        entityManager.merge(data);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(int id) {
        xxlJobUserRepository.deleteById(id);
        return 1;
    }

    @Autowired
    private XxlJobUserRepository xxlJobUserRepository;
    @Autowired
    private EntityManager entityManager;
}

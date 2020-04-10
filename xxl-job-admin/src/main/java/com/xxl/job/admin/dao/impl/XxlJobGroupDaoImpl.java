package com.xxl.job.admin.dao.impl;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.repository.XxlJobGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author YunSongLiu
 */
@Service
public class XxlJobGroupDaoImpl implements XxlJobGroupDao {

    @Override
    public List<XxlJobGroup> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        return xxlJobGroupRepository.findAll(sort);
    }

    @Override
    public List<XxlJobGroup> findByAddressType(int addressType) {
        return xxlJobGroupRepository.queryXxlJobGroupsByAddressTypeEqualsOrderByIdAsc(addressType);
    }

    @Override
    @Transactional
    public int save(XxlJobGroup xxlJobGroup) {
        entityManager.persist(xxlJobGroup);
        entityManager.flush();
        return 1;
    }

    @Override
    @Transactional
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
    @Transactional
    public int remove(int id) {
        XxlJobGroup result =  xxlJobGroupRepository.getOne(id);
        if (result == null) {
            return 0;
        }
        entityManager.remove(result);
        entityManager.flush();
        return 0;
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

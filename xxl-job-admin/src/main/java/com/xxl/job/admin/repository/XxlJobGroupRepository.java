package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author YunSongLiu
 */
public interface XxlJobGroupRepository extends JpaRepository<XxlJobGroup, Integer>, JpaSpecificationExecutor<XxlJobGroup> {

    List<XxlJobGroup> queryXxlJobGroupsByAddressTypeEquals(int addressType, Sort sort);

    XxlJobGroup queryXxlJobGroupByIdEquals(int id);

}

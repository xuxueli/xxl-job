package com.xxl.job.admin.repository;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

/**
 * @author YunSongLiu
 */

public interface XxlJobRegistryRepository extends JpaRepository<XxlJobRegistry, Integer>, JpaSpecificationExecutor<XxlJobRegistry> {

    List<XxlJobRegistry> findAllByUpdateTimeGreaterThan(Date dateTime);

    XxlJobRegistry findByRegistryGroupEqualsAndRegistryValueEqualsAndRegistryKeyEquals(String registryGroup,String registryValue, String registryKey);

}

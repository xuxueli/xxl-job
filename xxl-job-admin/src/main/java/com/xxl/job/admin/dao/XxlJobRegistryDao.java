package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public interface XxlJobRegistryDao extends JpaRepository<XxlJobRegistry, Long> {

    @Query("select t.id from XxlJobRegistry t where t.updateTime < :timeoutTime")
    public List<Long> findDead(@Param("timeoutTime") Date timeoutTime);

    @Transactional
    @Modifying
    @Query("delete from XxlJobRegistry t where t.id in :ids")
    public int removeDead(@Param("ids") List<Long> ids);

    @Query("select t from XxlJobRegistry t where t.updateTime > :timeoutTime")
    public List<XxlJobRegistry> findAll(@Param("timeoutTime") Date timeoutTime);

    @Transactional
    @Modifying
    @Query("update XxlJobRegistry t set t.updateTime = :updateTime " +
            "where t.registryGroup = :registryGroup and t.registryKey = :registryKey and t.registryValue = :registryValue")
    public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue,
                              @Param("updateTime") Date updateTime);

    @Transactional
    @Modifying
    @Query("delete from XxlJobRegistry t where t.registryGroup = :registryGroup " +
            "and t.registryKey = :registryKey and t.registryValue = :registryValue")
    public int registryDelete(@Param("registryGroup") String registryGroup,
                          @Param("registryKey") String registryKey,
                          @Param("registryValue") String registryValue);

}

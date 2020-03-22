package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository
public interface XxlJobGroupDao extends JpaRepository<XxlJobGroup, Long> {

    @Override
    @Query("select g from XxlJobGroup g ORDER BY g.order ASC")
    public List<XxlJobGroup> findAll();

    @Query("select g from XxlJobGroup g where g.addressType = :addressType ORDER BY g.order ASC")
    public List<XxlJobGroup> findByAddressType(@Param("addressType") int addressType);

    @Transactional
    @Modifying
    @Query("update XxlJobGroup g set g.appName = :#{#xxlJobGroup.appName}, g.title = :#{#xxlJobGroup.title}, " +
            "g.order = :#{#xxlJobGroup.order}, g.addressType = :#{#xxlJobGroup.addressType}, g.addressList = :#{#xxlJobGroup.addressList} " +
            "where g.id = :#{#xxlJobGroup.id}")
    public int update(@Param("xxlJobGroup") XxlJobGroup xxlJobGroup);

    @Transactional
    @Modifying
    @Query("delete from XxlJobGroup g where g.id = :id")
    public int remove(@Param("id") long id);

    @Query("select g from XxlJobGroup g where g.id = :id")
    public XxlJobGroup load(@Param("id") long id);
}

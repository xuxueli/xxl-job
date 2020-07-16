package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobMachine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface XxlJobMachineDao {

    void save(@Param("xxlJobMachine")XxlJobMachine xxlJobMachine);

    void update(@Param("machineIp") String machineIp, @Param("heartLastTime") Date heartLastTime);

    XxlJobMachine selectByHostIp(String machineIp);

    Integer selectMaxMachineId();

}

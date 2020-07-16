package com.xxl.job.admin.core.id.service;

import com.xxl.job.admin.core.model.XxlJobMachine;

import java.util.Date;

public interface MachineService {

    void save(XxlJobMachine xxlJobMachine);

    void update(String machineIp, Date heartLastTime);

    XxlJobMachine selectByMachineIp(String machineIp);

    Integer selectMaxMachineId();

    Integer getInitMachineId();
}

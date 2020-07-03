package com.xxl.job.admin.core.id;

import com.xxl.job.admin.core.id.service.MachineService;
import com.xxl.job.admin.core.model.XxlJobMachine;
import com.xxl.job.admin.core.util.MachineUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@EnableScheduling
public class HeartBeat {

    @Autowired
    private MachineService machineService;

    @Scheduled(fixedDelay = 10000)
    public void checkMachineSurvive(){
        String machineIp = MachineUtils.get();
        XxlJobMachine xxlJobMachine = machineService.selectByMachineIp(machineIp);
        if(xxlJobMachine != null){
            machineService.update(machineIp,new Date());
        }
    }
}
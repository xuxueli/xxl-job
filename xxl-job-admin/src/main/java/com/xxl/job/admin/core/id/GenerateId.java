package com.xxl.job.admin.core.id;

import com.xxl.job.admin.core.id.service.MachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GenerateId {

    private final Logger logger = LoggerFactory.getLogger(GenerateId.class);

    private SnowflakeIdWorker idWorker = null;

    @Autowired
    private MachineService machineService;

    private Integer machineId = -1;

    public Long getId() {
        return idWorker.nextId();
    }

    @PostConstruct
    private void getIdBefore() {
        //只需要第一次调用  对idworker进行初始化
        machineId = machineService.getInitMachineId();
        idWorker = new SnowflakeIdWorker(machineId);
    }

    public Integer getMachineId(){
        return this.machineId;
    }

    public void setMachineId(Integer machineId){
        this.machineId = machineId;
    }

    public void setIdWorker(SnowflakeIdWorker snowflakeIdWorker){
        this.idWorker = snowflakeIdWorker;
    }
}
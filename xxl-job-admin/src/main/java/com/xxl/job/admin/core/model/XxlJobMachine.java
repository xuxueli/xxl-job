package com.xxl.job.admin.core.model;

import java.util.Date;

public class XxlJobMachine {

    /**
     * 主机IP
     */
    private String machineIp;

    /**
     * 主机IP对应的机器码
     */
    private Integer machineId;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 最后一次心跳时间
     */
    private Date heartLastTime;

    public Date getHeartLastTime() {
        return heartLastTime;
    }

    public void setHeartLastTime(Date heartLastTime) {
        this.heartLastTime = heartLastTime;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }
}

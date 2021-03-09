package com.xxl.job.admin.service;

public interface OpLogService {
    void addLog(String logType, Object oldVal, Object newVal, String description);
    void addLog(String logType);
    void addLog(String logType,String userName);
}

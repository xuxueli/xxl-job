package com.xxl.job.core.annotationtask.enums;

public enum  ExecutorFailStrategy {

    FAIL_ALARM("jobconf_fail_alarm"),//失败告警

    FAIL_RETRY("jobconf_fail_retry");//失败重试

    private final String title;//title

    ExecutorFailStrategy(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


}

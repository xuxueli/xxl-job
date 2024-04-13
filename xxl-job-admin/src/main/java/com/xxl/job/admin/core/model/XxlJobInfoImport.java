package com.xxl.job.admin.core.model;

import java.util.List;

/**
 * @author acton
 */
public class XxlJobInfoImport {
    private List<XxlJobGroup> jobGroups;
    private List<XxlJobInfo> jobInfos;

    public List<XxlJobGroup> getJobGroups() {
        return jobGroups;
    }

    public void setJobGroups(List<XxlJobGroup> jobGroups) {
        this.jobGroups = jobGroups;
    }

    public List<XxlJobInfo> getJobInfos() {
        return jobInfos;
    }

    public void setJobInfos(List<XxlJobInfo> jobInfos) {
        this.jobInfos = jobInfos;
    }
}

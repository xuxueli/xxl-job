package com.xxl.job.admin.platform.batch.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Ice2Faith
 * @date 2025/10/12 13:58
 * @desc
 */
@Data
@NoArgsConstructor
public class LogBatchOperateDto {
    protected String operateType;

    protected String scheduleType;
    protected String scheduleConf;

    protected Integer groupRange;
    protected String jobGroup;
    protected Integer triggerStatus;
    protected String jobDesc;
    protected String executorHandler;
    protected String author;

    protected Set<Integer> operGroupIds;
}

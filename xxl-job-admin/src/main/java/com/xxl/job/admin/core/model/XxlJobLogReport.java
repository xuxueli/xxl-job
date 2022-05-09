package com.xxl.job.admin.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("xxl_job_log_report")
public class XxlJobLogReport {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Date triggerDay;

    private int runningCount;
    private int sucCount;
    private int failCount;
}

package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 任务日志报表
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Data
@TableName("XXL_JOB_LOG_REPORT")
public class LogReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 调度-时间
     */
    private Long triggerDay;

    /**
     * 运行中-日志数量
     */
    private Long runningCount;

    /**
     * 执行成功-日志数量
     */
    private Long sucCount;

    /**
     * 执行失败-日志数量
     */
    private Long failCount;


}

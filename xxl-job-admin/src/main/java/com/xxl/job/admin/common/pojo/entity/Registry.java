package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 任务注册信息
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Data
@TableName("XXL_JOB_REGISTRY")
public class Registry implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 注册分组
     */
    private String registryGroup;

    /**
     * 注册KEY
     */
    private String registryKey;

    /**
     * 注册value
     */
    private String registryValue;

    /**
     * 修改时间
     */
    private Date updatedTime;


}

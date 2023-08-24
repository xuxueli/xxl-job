package com.xxl.job.admin.common.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * GLUE日志
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-05-13
 */
@Data
public class GlueLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务，主键ID
     */
    private Long jobId;

    /**
     * GLUE类型
     */
    private String glueType;

    /**
     * GLUE源代码
     */
    private String glueSource;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 更新人
     */
    private String updatedUser;

    /**
     * 更新时间
     */
    private Long updatedTime;

    /**
     * 描述
     */
    private String description;


}

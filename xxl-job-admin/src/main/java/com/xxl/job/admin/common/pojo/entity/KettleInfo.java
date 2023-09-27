package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * kettle信息
 * </p>
 *
 * @author Rong.Jia
 * @since 2023-09-10
 */
@Data
@TableName("xxl_job_kettle_info")
public class KettleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  主键
     */
    private Long id;

    /**
     * 系列
     */
    private String series;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 模型类型(ktr,kjb)
     */
    private String type;

    /**
     * kettle文件
     */
    private byte[] kettleFile;

    /**
     * kjb引导文件，模型类型为kjb有效
     */
    private String guideKjb;

    /**
     * 日志级别(NOTHING:没有日志,ERROR:错误日志,MINIMAL:最小日志,BASIC:基本日志,
     * DETAILED:详细日志,DEBUG:调试,ROWLEVEL:行级日志(非常详细))
     */
    private String logLevel;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新人
     */
    private String updatedUser;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 描述
     */
    private String description;


}

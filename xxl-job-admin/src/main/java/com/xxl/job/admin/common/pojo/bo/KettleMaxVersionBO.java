package com.xxl.job.admin.common.pojo.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * kettle 最大版本BO
 * @author Rong.Jia
 * @date 2023/09/17
 */
@Data
public class KettleMaxVersionBO implements Serializable {

    private static final long serialVersionUID = 8001636666063676895L;

    /**
     * 系列
     */
    private String series;

    /**
     * 名称
     */
    private String name;

    /**
     * 最大版本
     */
    private String maxVersion;



}

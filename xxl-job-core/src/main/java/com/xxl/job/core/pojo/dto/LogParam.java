package com.xxl.job.core.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 日志参数
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
@AllArgsConstructor
public class LogParam implements Serializable {

    private static final long serialVersionUID = 42L;

    private long logDateTime;
    private long logId;
    private int fromLineNum;

}
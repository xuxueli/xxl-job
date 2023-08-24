package com.xxl.job.core.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 记录结果
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
@AllArgsConstructor
public class LogResult implements Serializable {
    private static final long serialVersionUID = 42L;

    private int fromLineNum;
    private int toLineNum;
    private String logContent;
    private boolean isEnd;

}

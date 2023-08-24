package com.xxl.job.core.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 杀死参数
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
@AllArgsConstructor
public class KillParam implements Serializable {

    private static final long serialVersionUID = 42L;

    private Long jobId;

}
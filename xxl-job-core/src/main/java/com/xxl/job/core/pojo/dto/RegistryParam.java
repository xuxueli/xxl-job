package com.xxl.job.core.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 注册表参数
 *
 * @author Rong.Jia
 * @date 2023/05/12
 */
@Data
public class RegistryParam implements Serializable {

    private static final long serialVersionUID = 42L;

    private String registryGroup;
    private String registryKey;
    private String registryValue;


}

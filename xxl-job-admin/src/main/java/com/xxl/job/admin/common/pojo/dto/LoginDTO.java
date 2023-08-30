package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录DTO
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Data
@ApiModel("登录参数")
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = -2571579029095288606L;

    /**
     * 账号
     */
    @NotBlank(message = "账号 不能为空")
    @ApiModelProperty(value = "账号", required = true)
    private String account;

    /**
     * 密码
     */
    @NotBlank(message = "密码 不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;


}

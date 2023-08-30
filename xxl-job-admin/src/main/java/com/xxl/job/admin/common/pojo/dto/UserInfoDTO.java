package com.xxl.job.admin.common.pojo.dto;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户信息DTO
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@ApiModel("用户信息参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoDTO extends Base implements Serializable {

    private static final long serialVersionUID = -281379933802293123L;

    /**
     * 账号
     */
    @NotBlank(message = "账号 不能为空")
    @ApiModelProperty(value = "账号", required = true)
    private String account;

    /**
     * 密码
     */
    @Pattern(regexp = "^(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*()_.]+)$)^[\\w~!@#$%^&*()_.]{6,16}$", message = "密码应为字母，数字，特殊符号(~!@#$%^&*()_.)，两种及以上组合，6-16位")
    @NotBlank(message = "密码 不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名 不能为空")
    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String mail;

    /**
     * 性别  男：0，女：1
     */
    @ApiModelProperty("性别  男：0，女：1")
    private Integer sex;

    /**
     * 手机号码
     */
    @ApiModelProperty("手机号码")
    private String telephone;








}
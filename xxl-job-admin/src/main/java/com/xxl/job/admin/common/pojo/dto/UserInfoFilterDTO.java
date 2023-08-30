package com.xxl.job.admin.common.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户信息过滤DTO
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@ApiModel("用户信息过滤参数")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoFilterDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = -281379933802293123L;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String account;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
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
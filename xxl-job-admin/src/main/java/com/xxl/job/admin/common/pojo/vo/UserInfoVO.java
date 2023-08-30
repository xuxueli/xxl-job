package com.xxl.job.admin.common.pojo.vo;

import com.xxl.job.admin.common.pojo.bo.Base;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户信息VO
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@ApiModel("用户信息")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoVO extends Base implements Serializable {

    private static final long serialVersionUID = -281379933802293123L;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String account;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
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

    /**
     * 账号状态, (0->已过期，1->启用，-1->禁用 )
     */
    @ApiModelProperty("账号状态, (0->已过期，1->启用，-1->禁用 )")
    private Integer status;





}
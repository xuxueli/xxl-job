package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author Rong.Jia
 * @date 2023/07/23
 */
@Data
@TableName(value = "xxl_job_user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -281379933802293123L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 性别  男：0，女：1
     */
    private Integer sex;

    /**
     * 手机号码
     */
    private String telephone;

    /**
     * 账号状态, (0->已过期，1->启用，-1->禁用 )
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 创建时间
     */
    private Long createdTime;

    /**
     * 更新人
     */
    private String updatedUser;

    /**
     * 更新时间
     */
    private Long updatedTime;

    /**
     * 描述
     */
    private String description;




}
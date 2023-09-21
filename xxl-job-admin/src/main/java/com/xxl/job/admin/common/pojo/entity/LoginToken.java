package com.xxl.job.admin.common.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录令牌
 * @author Rong.Jia
 * @date 2023/09/09
 */
@Data
@TableName("xxl_job_login_token")
public class LoginToken implements Serializable {

    private static final long serialVersionUID = -542472878287179971L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 账号
     */
    private String account;

    /**
     * 有效时长(单位：秒)
     */
    private Integer effectiveDuration;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 更新时间
     */
    private Date updatedTime;








}

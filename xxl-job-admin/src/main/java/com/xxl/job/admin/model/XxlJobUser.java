package com.xxl.job.admin.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

/**
 * xxl-job user
 *
 * @author xuxueli  2019-05-04 16:43:12
 */
@TableName("xxl_job_user")
public class XxlJobUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID - 自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 登录token
     */
    private String token;

    /**
     * 角色：0-普通用户、1-管理员
     */
    private int role;

    /**
     * 权限：执行器ID列表，多个逗号分割
     */
    private String permission;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}
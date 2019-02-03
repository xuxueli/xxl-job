package com.xxl.job.admin.core.model;

import java.util.Date;

public class XxlJobUser {

    private String username;

    private String password;

    private int permission;             // 权限：0-普通用户、1-管理员
    private String permissionData;      // 权限配置数据, 格式 "appname#env,appname#env02"

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getPermissionData() {
        return permissionData;
    }

    public void setPermissionData(String permissionData) {
        this.permissionData = permissionData;
    }
}


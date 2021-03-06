package com.xxl.job.admin.controller.rest;

/**
 * @Author: 63198
 * @Date: 2021/2/25 下午4:28
 * @Version 1.0
 */
public class LoginInfo {
    private String username;
    private String password;
    private boolean ifRemember;

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

    public boolean isIfRemember() {
        return ifRemember;
    }

    public void setIfRemember(boolean ifRemember) {
        this.ifRemember = ifRemember;
    }
}

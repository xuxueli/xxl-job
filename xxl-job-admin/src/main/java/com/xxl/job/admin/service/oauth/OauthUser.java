package com.xxl.job.admin.service.oauth;


import java.io.Serializable;

/**
 * common return
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
public class OauthUser<T> implements Serializable {

    public String username;
    public String name;
    public int is_admin;

}
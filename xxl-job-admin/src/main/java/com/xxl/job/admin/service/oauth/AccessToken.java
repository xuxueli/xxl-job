package com.xxl.job.admin.service.oauth;


import java.io.Serializable;

/**
 * common return
 * @author xuxueli 2015-12-4 16:32:31
 * @param <T>
 */
public class AccessToken<T> implements Serializable {
    public String access_token;
    public String scope;
    public String refresh_token;
    public int expires_in;
  

    
}
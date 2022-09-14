package com.xxl.job.admin.service;


public interface LdapService {

    /**
     * 用户搜索
     * @param username
     * @return
     */
    boolean find(String username) ;

    /**
     * 用户验证
     * @param username
     * @param password
     * @return
     */
    boolean authenticate(String username, String password) ;

    /**
     * 是否是管理
     * @param username
     * @return
     */
    boolean isAdmin(String username) ;
}

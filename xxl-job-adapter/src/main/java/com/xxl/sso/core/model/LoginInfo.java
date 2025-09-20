package com.xxl.sso.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Ice2Faith
 * @date 2025/9/20 9:29
 */
@Data
@NoArgsConstructor
public class LoginInfo implements Serializable {
    private static final long serialVersionUID = 42L;
    private String userId;
    private String userName;
    private String realName;
    private Map<String, String> extraInfo;
    private List<String> roleList;
    private List<String> permissionList;
    private long expireTime;
    private String signature;


    public LoginInfo(String userId, String signature) {
        this.userId = userId;
        this.signature = signature;
    }

    public LoginInfo(String userId, String userName, String realName, Map<String, String> extraInfo, List<String> roleList, List<String> permissionList, long expireTime, String signature) {
        this.userId = userId;
        this.userName = userName;
        this.realName = realName;
        this.extraInfo = extraInfo;
        this.roleList = roleList;
        this.permissionList = permissionList;
        this.expireTime = expireTime;
        this.signature = signature;
    }

}
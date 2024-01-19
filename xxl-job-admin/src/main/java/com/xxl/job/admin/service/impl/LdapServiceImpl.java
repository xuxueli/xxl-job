package com.xxl.job.admin.service.impl;

import com.xxl.job.admin.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LdapServiceImpl implements LdapService {

    /**
     * 用户过滤器
     */
    @Value("${spring.ldap.filter:(&(objectclass=person)(cn={username}))}")
    private String filterStr;

    /**
     * 管理员过滤器
     */
    @Value("${spring.ldap.admin:}")
    private String adminStr;

    /**
     * 是否启用
     */
    @Value("${spring.data.ldap.repositories.enabled:true}")
    private Boolean enable;

    @Autowired(required = false)
    private LdapTemplate ldapTemplate;

    @Override
    public boolean find(String username) {
        if (!this.enable || null == ldapTemplate) {
            return false;
        }
        List<?> uid = ldapTemplate.search("", this.filterStr.replace("{username}", username), (AttributesMapper) v -> v.get("uid"));
        return null != uid && !uid.isEmpty();
    }

    @Override
    public boolean authenticate(String username, String password) {
        return ldapTemplate.authenticate("", this.filterStr.replace("{username}", username), password);
    }

    @Override
    public boolean isAdmin(String username) {
        if (null == adminStr || adminStr.isEmpty()) {
            return false;
        }
        List<?> uid = ldapTemplate.search("", this.adminStr.replace("{username}", username), (AttributesMapper) v -> v.get("uid"));
        return null != uid && !uid.isEmpty();
    }
}

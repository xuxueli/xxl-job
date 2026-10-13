package com.xxl.job.admin.framework.oidc;

import com.xxl.job.admin.framework.mapper.XxlJobUserMapper;
import com.xxl.job.admin.framework.model.XxlJobUser;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Sha256Tool;
import com.xxl.tool.id.UUIDTool;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * oidc login service: id_token claims -> xxl-job user -> login info
 *
 * @author siyixiong 2026-10-10
 */
@Service
public class OidcLoginService {
    private static final Logger logger = LoggerFactory.getLogger(OidcLoginService.class);

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._@-]{0,49}$");

    @Resource
    private OidcProperties oidcProperties;
    @Resource
    private XxlJobUserMapper xxlJobUserMapper;

    /**
     * find or create user by claims, sync role if both roles-claim and admin-role configured
     */
    public LoginInfo login(Map<String, Object> claims) {

        // parse username
        Object usernameValue = claims.get(oidcProperties.getUsernameClaim());
        String username = usernameValue != null ? usernameValue.toString() : null;
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalStateException("oidc username claim missing or invalid, claim=" + oidcProperties.getUsernameClaim());
        }

        // parse role, null means not sync
        Integer role = null;
        if (StringTool.isNotBlank(oidcProperties.getRolesClaim()) && StringTool.isNotBlank(oidcProperties.getAdminRole())) {
            role = parseRoles(claims, oidcProperties.getRolesClaim()).contains(oidcProperties.getAdminRole()) ? 1 : 0;
        }

        // find or create user
        XxlJobUser user = xxlJobUserMapper.loadByUserName(username);
        boolean created = user == null;
        boolean roleChanged = false;
        if (user != null && !username.equals(user.getUsername())) {
            // db collation may be case-insensitive
            throw new IllegalStateException("oidc username not match exactly, username=" + username);
        }
        if (user == null) {
            if (!oidcProperties.isAutoCreateUser()) {
                throw new IllegalStateException("oidc user not exists and auto-create disabled, username=" + username);
            }
            user = new XxlJobUser();
            user.setUsername(username);
            user.setPassword(Sha256Tool.sha256(UUIDTool.getSimpleUUID()));    // unusable password, login by oidc only
            user.setRole(role != null ? role : 0);
            xxlJobUserMapper.save(user);
        } else if (role != null && role != user.getRole()) {
            user.setPassword(null);     // keep password
            user.setRole(role);
            xxlJobUserMapper.update(user);
            roleChanged = true;
        }
        logger.info(">>>>>>>>>>> xxl-job oidc login success, username={}, created={}, roleChanged={}", username, created, roleChanged);

        return new LoginInfo(String.valueOf(user.getId()), UUIDTool.getSimpleUUID());
    }

    /**
     * parse roles by full key (like "https://example.com/roles"), else by dot path (like "realm_access.roles");
     * value supports string array or single string
     */
    static List<String> parseRoles(Map<String, Object> claims, String rolesClaim) {
        Object value = claims.get(rolesClaim);
        if (value == null) {
            value = claims;
            for (String key : rolesClaim.split("\\.")) {
                if (!(value instanceof Map<?, ?> map)) {
                    return List.of();
                }
                value = map.get(key);
            }
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(String::valueOf).toList();
        }
        return value != null ? List.of(value.toString()) : List.of();
    }

}

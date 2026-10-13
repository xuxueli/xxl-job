package com.xxl.job.admin.framework.oidc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * oidc login properties, prefix "xxl.job.oidc"
 *
 * @author siyixiong 2026-10-10
 */
@Component
@ConfigurationProperties("xxl.job.oidc")
public class OidcProperties {

    private boolean enabled = false;            // oidc login switch
    private String issuerUri;                   // issuer, discovery by "{issuer}/.well-known/openid-configuration"
    private String clientId;
    private String clientSecret;                // blank means public client (pkce only)
    private String redirectUri;
    private String scope = "openid profile email";
    private String usernameClaim = "preferred_username";
    private String rolesClaim;                  // dot path, like "realm_access.roles"; blank means not sync role
    private String adminRole;                   // idp role mapping to xxl-job admin
    private boolean autoCreateUser = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIssuerUri() {
        return issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsernameClaim() {
        return usernameClaim;
    }

    public void setUsernameClaim(String usernameClaim) {
        this.usernameClaim = usernameClaim;
    }

    public String getRolesClaim() {
        return rolesClaim;
    }

    public void setRolesClaim(String rolesClaim) {
        this.rolesClaim = rolesClaim;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public boolean isAutoCreateUser() {
        return autoCreateUser;
    }

    public void setAutoCreateUser(boolean autoCreateUser) {
        this.autoCreateUser = autoCreateUser;
    }

}

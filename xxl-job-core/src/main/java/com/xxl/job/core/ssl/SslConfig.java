package com.xxl.job.core.ssl;

/**
 * SSL的配置属性.
 *
 * @author cracker.lu
 * @date 2025/2/28
 */
public class SslConfig {
    /**
     * 是否启用SSL执行器.
     */
    private boolean enabled = false;
    /**
     * 外部路径配置证书路径
     */
    private String keyStore;
    /**
     * 文本输入Base64.
     */
    private String keyStoreRaw;
    /**
     * 证书打包文件加密密码
     */
    private String keyStorePassword;
    /**
     * 需要加载的证书条目别名,仅用于作为客户端
     */
    private String keyAlias;
    /**
     * 特定证书别名的密码，或者所有的密码.
     */
    private String keyPassword;
    /**
     * key 存储类型.
     */
    private String keyStoreType;

    /**
     * 自定义加密库提供者.
     */
    private String keyStoreProvider;
    /**
     * 信任库路径
     */
    private String trustStore;
    /**
     * 信任库库内容:文本输入Base64.
     */
    private String trustStoreRaw;
    /**
     * 信任库加密密码
     */
    private String trustStorePassword;
    /**
     * key 存储类型.
     */
    private String trustStoreType;
    /**
     * 自定义加密库提供者.
     */
    private String trustStoreProvider;
    /**
     * 客户端策略:是否验证主机名.
     */
    private boolean hostnameVerificationDisabled =true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStoreRaw() {
        return keyStoreRaw;
    }

    public void setKeyStoreRaw(String keyStoreRaw) {
        this.keyStoreRaw = keyStoreRaw;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStoreProvider() {
        return keyStoreProvider;
    }

    public void setKeyStoreProvider(String keyStoreProvider) {
        this.keyStoreProvider = keyStoreProvider;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStoreRaw() {
        return trustStoreRaw;
    }

    public void setTrustStoreRaw(String trustStoreRaw) {
        this.trustStoreRaw = trustStoreRaw;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getTrustStoreProvider() {
        return trustStoreProvider;
    }

    public void setTrustStoreProvider(String trustStoreProvider) {
        this.trustStoreProvider = trustStoreProvider;
    }

    public ClientAuth getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(ClientAuth clientAuth) {
        this.clientAuth = clientAuth;
    }

    public boolean isHostnameVerificationDisabled() {
        return hostnameVerificationDisabled;
    }

    public void setHostnameVerificationDisabled(boolean hostnameVerificationDisabled) {
        this.hostnameVerificationDisabled = hostnameVerificationDisabled;
    }

    /**
     * 客户端证书验证模式：不需要, 可选，必填.
     */
    private ClientAuth clientAuth;


    public enum ClientAuth {

        /**
         * Client authentication is not wanted.
         */
        NONE,

        /**
         * Client authentication is wanted but not mandatory.
         */
        WANT,

        /**
         * Client authentication is needed and mandatory.
         */
        NEED

    }
}

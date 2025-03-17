package com.xxl.job.core.ssl;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * 实现基于ssl配置和java环境进行上下文的初始化.
 *
 * @author cracker.lu
 * @date 2025/2/28
 */
public class SslContextUtils {

    /**
     * 提供用于Netty的Ssl上下文.
     *
     * @param sslConfig 自定义配置参数
     * @param forServer 是否适用于服务端
     * @return netty 的Ssl上下文.
     * @throws SSLException 构建异常
     */
    @Nullable
    public static SslContext forNetty(SslConfig sslConfig, boolean forServer) throws SSLException {
        if (Objects.isNull(sslConfig)) {
            return null;
        }
        if (!sslConfig.isEnabled()) {
            return null;
        }

        if (forServer) {
            return forServer(sslConfig).build();
        } else {
            return forClient(sslConfig).build();
        }
    }

    /**
     * 提供用于Netty的Ssl上下文.
     *
     * @param sslConfig 自定义配置参数
     * @param forServer 是否适用于服务端
     * @return netty 的Ssl上下文.
     * @throws SSLException 构建异常
     */
    @Nullable
    public static SSLContext ssl(SslConfig sslConfig, boolean forServer) throws SSLException {
        if (Objects.isNull(sslConfig)) {
            return null;
        }
        if (!sslConfig.isEnabled()) {
            return null;
        }
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(sslConfig, forServer);
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(sslConfig);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            if (forServer) {
                SSLParameters parameters = sslContext.getDefaultSSLParameters();
                if (sslConfig.getClientAuth() == SslConfig.ClientAuth.NEED) {
                    parameters.setNeedClientAuth(true);
                } else if (sslConfig.getClientAuth() == SslConfig.ClientAuth.WANT) {
                    parameters.setWantClientAuth(true);
                }
            }
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static SslContextBuilder forServer(SslConfig sslConfig) {
        SslContextBuilder builder = SslContextBuilder.forServer(getKeyManagerFactory(sslConfig, true))
                .trustManager(getTrustManagerFactory(sslConfig));
        if (sslConfig.getClientAuth() == SslConfig.ClientAuth.NEED) {
            builder.clientAuth(ClientAuth.REQUIRE);
        } else if (sslConfig.getClientAuth() == SslConfig.ClientAuth.WANT) {
            builder.clientAuth(ClientAuth.OPTIONAL);
        }
        return builder;
    }

    private static SslContextBuilder forClient(SslConfig sslConfig) {
        return SslContextBuilder.forClient()
                .keyManager(getKeyManagerFactory(sslConfig, false))
                .trustManager(getTrustManagerFactory(sslConfig));
    }


    protected static KeyManagerFactory getKeyManagerFactory(SslConfig ssl, boolean required) {
        try {
            KeyStore keyStore = getKeyStore(ssl, required);
            validateKeyAlias(keyStore, ssl.getKeyAlias());
            KeyManagerFactory keyManagerFactory = (ssl.getKeyAlias() == null && !required)
                    ? KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                    : new ConfigurableAliasKeyManagerFactory(ssl.getKeyAlias(),
                    KeyManagerFactory.getDefaultAlgorithm());
            char[] keyPassword = (ssl.getKeyPassword() != null) ? ssl.getKeyPassword().toCharArray() : null;
            if (keyPassword == null && ssl.getKeyStorePassword() != null) {
                keyPassword = ssl.getKeyStorePassword().toCharArray();
            }
            keyManagerFactory.init(keyStore, keyPassword);
            return keyManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static KeyStore getKeyStore(SslConfig ssl, boolean required) throws Exception {
        String globalResource = System.getProperty("javax.net.ssl.keyStore");

        String keyStoreType = ssl.getKeyStoreType();
        if (ObjectUtils.isEmpty(keyStoreType)) {
            keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
        }

        String keyStorePassword = ssl.getKeyStorePassword();
        if (ObjectUtils.isEmpty(keyStorePassword)) {
            keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        }

        return loadStore(keyStoreType, ssl.getKeyStoreProvider(), ssl.getKeyStore(), ssl.getKeyStoreRaw(), globalResource, keyStorePassword, required);
    }

    private static TrustManagerFactory getTrustManagerFactory(SslConfig ssl) {
        try {
            KeyStore store = getTrustStore(ssl);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(store);
            return trustManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }


    private static KeyStore getTrustStore(SslConfig ssl) throws Exception {
        String globalResource = System.getProperty("javax.net.ssl.trustStore");

        String trustStoreType = ssl.getTrustStoreType();
        if (ObjectUtils.isEmpty(trustStoreType)) {
            trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
        }

        String trustStorePassword = ssl.getKeyStorePassword();
        if (ObjectUtils.isEmpty(trustStorePassword)) {
            trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        }

        return loadStore(trustStoreType, ssl.getTrustStoreProvider(), ssl.getTrustStore(), ssl.getTrustStoreRaw(), globalResource, trustStorePassword, false);
    }

    private static KeyStore loadStore(String type,
                                      String provider,
                                      String resource,
                                      String base64Raw,
                                      String globalResource,
                                      String password,
                                      boolean required) throws KeyStoreException, NoSuchProviderException {
        type = (type != null) ? type : "JKS";
        KeyStore store = (provider != null) ? KeyStore.getInstance(type, provider) : KeyStore.getInstance(type);

        if (!ObjectUtils.isEmpty(resource)) {
            // 读取配置的
            try (InputStream stream = ResourceUtils.getURL(resource).openStream()) {
                store.load(stream, (password != null) ? password.toCharArray() : null);
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Load keyStore %s failed", resource), e);
            }
            return store;
        } else if (!ObjectUtils.isEmpty(base64Raw)) {
            // 读取配置的文本
            try (InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(base64Raw))) {
                store.load(stream, (password != null) ? password.toCharArray() : null);
            } catch (Exception e) {
                throw new IllegalStateException("Load keyStore base64 raw failed", e);
            }
            return store;

        } else if (!ObjectUtils.isEmpty(globalResource)) {
            // 读取系统属性.
            try {
                try (InputStream stream = ResourceUtils.getURL(globalResource).openStream()) {
                    store.load(stream, (password != null) ? password.toCharArray() : null);
                }
                return store;
            } catch (Exception e) {
                throw new IllegalStateException(String.format("Load keyStore %s failed", globalResource), e);
            }
        } else if (required) {
            throw new RuntimeException("Missing key store config");
        }

        return null;
    }

    public static void validateKeyAlias(KeyStore keyStore, String keyAlias) {
        if (!ObjectUtils.isEmpty(keyAlias)) {
            try {
                Assert.state(keyStore.containsAlias(keyAlias),
                        () -> String.format("Keystore does not contain specified alias '%s'", keyAlias));
            } catch (KeyStoreException ex) {
                throw new IllegalStateException(
                        String.format("Could not determine if keystore contains alias '%s'", keyAlias), ex);
            }
        }
    }


    private static final class ConfigurableAliasKeyManagerFactory extends KeyManagerFactory {

        private ConfigurableAliasKeyManagerFactory(String alias, String algorithm) throws NoSuchAlgorithmException {
            this(KeyManagerFactory.getInstance(algorithm), alias, algorithm);
        }

        private ConfigurableAliasKeyManagerFactory(KeyManagerFactory delegate, String alias, String algorithm) {
            super(new ConfigurableAliasKeyManagerFactorySpi(delegate, alias), delegate.getProvider(), algorithm);
        }

    }

    private static final class ConfigurableAliasKeyManagerFactorySpi extends KeyManagerFactorySpi {

        private final KeyManagerFactory delegate;

        private final String alias;

        private ConfigurableAliasKeyManagerFactorySpi(KeyManagerFactory delegate, String alias) {
            this.delegate = delegate;
            this.alias = alias;
        }

        @Override
        protected void engineInit(KeyStore keyStore, char[] chars)
                throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            this.delegate.init(keyStore, chars);
        }

        @Override
        protected void engineInit(ManagerFactoryParameters managerFactoryParameters)
                throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("Unsupported ManagerFactoryParameters");
        }

        @Override
        protected KeyManager[] engineGetKeyManagers() {
            return Arrays.stream(this.delegate.getKeyManagers()).filter(X509ExtendedKeyManager.class::isInstance)
                    .map(X509ExtendedKeyManager.class::cast).map(this::wrap).toArray(KeyManager[]::new);
        }

        private ConfigurableAliasKeyManager wrap(X509ExtendedKeyManager keyManager) {
            return new ConfigurableAliasKeyManager(keyManager, this.alias);
        }

    }

    private static final class ConfigurableAliasKeyManager extends X509ExtendedKeyManager {

        private final X509ExtendedKeyManager delegate;

        private final String alias;

        private ConfigurableAliasKeyManager(X509ExtendedKeyManager keyManager, String alias) {
            this.delegate = keyManager;
            this.alias = alias;
        }

        @Override
        public String chooseEngineClientAlias(String[] strings, Principal[] principals, SSLEngine sslEngine) {
            return this.delegate.chooseEngineClientAlias(strings, principals, sslEngine);
        }

        @Override
        public String chooseEngineServerAlias(String s, Principal[] principals, SSLEngine sslEngine) {
            return (this.alias != null) ? this.alias : this.delegate.chooseEngineServerAlias(s, principals, sslEngine);
        }

        @Override
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseClientAlias(keyType, issuers, socket);
        }

        @Override
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseServerAlias(keyType, issuers, socket);
        }

        @Override
        public X509Certificate[] getCertificateChain(String alias) {
            return this.delegate.getCertificateChain(alias);
        }

        @Override
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.delegate.getClientAliases(keyType, issuers);
        }

        @Override
        public PrivateKey getPrivateKey(String alias) {
            return this.delegate.getPrivateKey(alias);
        }

        @Override
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.delegate.getServerAliases(keyType, issuers);
        }

    }
}

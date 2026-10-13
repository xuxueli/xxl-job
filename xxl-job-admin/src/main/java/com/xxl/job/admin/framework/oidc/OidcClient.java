package com.xxl.job.admin.framework.oidc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.crypto.Sha256Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;

/**
 * oidc client (authorization code + pkce), based on nimbus oauth2-oidc-sdk
 *
 * 1、provider metadata is resolved lazily and cached, so that admin can start while idp is unavailable;
 * 2、id_token is validated by signature、iss、aud、exp and nonce;
 *
 * @author siyixiong 2026-10-10
 */
@Component
public class OidcClient {

    private static final int HTTP_TIMEOUT = 5000;
    private static final long RESOLVE_FAIL_BACKOFF = 30 * 1000;

    private final OidcProperties properties;
    private volatile Provider provider;
    private long resolveFailUntil;      // guarded by this

    @Autowired
    public OidcClient(OidcProperties properties) {
        this.properties = properties;
    }

    /**
     * with pre-resolved provider, for test
     */
    OidcClient(OidcProperties properties, OIDCProviderMetadata metadata, JWKSource<SecurityContext> jwkSource) {
        this.properties = properties;
        this.provider = new Provider(metadata, jwkSource);
    }


    // ---------------------- login state ----------------------

    /**
     * random secret, stored in cookie, also used as pkce code_verifier
     */
    public static String newSecret() {
        return new CodeVerifier().getValue();
    }

    public static String deriveState(String secret) {
        return Sha256Tool.sha256(secret + "state");
    }

    public static String deriveNonce(String secret) {
        return Sha256Tool.sha256(secret + "nonce");
    }


    // ---------------------- protocol ----------------------

    /**
     * build idp authorization url, with pkce code_challenge (S256) computed from code_verifier
     */
    public String buildAuthorizationUrl(String state, String nonce, String codeVerifier) throws GeneralException, IOException {
        return new AuthenticationRequest.Builder(
                        new ResponseType(ResponseType.Value.CODE),
                        Scope.parse(properties.getScope()),
                        clientId(),
                        redirectUri())
                .endpointURI(provider().metadata().getAuthorizationEndpointURI())
                .state(new State(state))
                .nonce(new Nonce(nonce))
                .codeChallenge(new CodeVerifier(codeVerifier), CodeChallengeMethod.S256)
                .build()
                .toURI()
                .toString();
    }

    /**
     * exchange code for tokens, then validate id_token and return its claims
     */
    public Map<String, Object> exchangeAndValidate(String code, String codeVerifier, String nonce)
            throws GeneralException, IOException, BadJOSEException, JOSEException {
        Provider provider = provider();
        ClientID clientId = clientId();

        // token request: client_secret_basic, or public client
        URI tokenEndpoint = provider.metadata().getTokenEndpointURI();
        AuthorizationGrant grant = new AuthorizationCodeGrant(
                new AuthorizationCode(code),
                redirectUri(),
                new CodeVerifier(codeVerifier));
        TokenRequest tokenRequest = StringTool.isNotBlank(properties.getClientSecret())
                ? new TokenRequest.Builder(tokenEndpoint, new ClientSecretBasic(clientId, new Secret(properties.getClientSecret())), grant).build()
                : new TokenRequest.Builder(tokenEndpoint, clientId, grant).build();
        HTTPRequest httpRequest = tokenRequest.toHTTPRequest();
        httpRequest.setConnectTimeout(HTTP_TIMEOUT);
        httpRequest.setReadTimeout(HTTP_TIMEOUT);

        // token response
        TokenResponse tokenResponse = OIDCTokenResponseParser.parse(httpRequest.send());
        if (!tokenResponse.indicatesSuccess()) {
            ErrorObject error = tokenResponse.toErrorResponse().getErrorObject();
            throw new GeneralException("token request fail, error=" + error.getCode() + ", description=" + error.getDescription());
        }
        if (!(tokenResponse.toSuccessResponse() instanceof OIDCTokenResponse oidcTokenResponse)) {
            throw new GeneralException("token response missing id_token");
        }

        // validate id_token
        IDTokenValidator validator = new IDTokenValidator(
                provider.metadata().getIssuer(),
                clientId,
                new JWSVerificationKeySelector<>(new HashSet<>(provider.metadata().getIDTokenJWSAlgs()), provider.jwkSource()),
                null);
        return validator.validate(oidcTokenResponse.getOIDCTokens().getIDToken(), new Nonce(nonce)).toJSONObject();
    }


    // ---------------------- provider ----------------------

    private record Provider(OIDCProviderMetadata metadata, JWKSource<SecurityContext> jwkSource) {
    }

    /**
     * resolve provider lazily; when fail, fail fast within backoff, then retry
     */
    private Provider provider() throws GeneralException, IOException {
        Provider current = provider;
        if (current == null) {
            synchronized (this) {
                current = provider;
                if (current == null) {
                    Issuer issuer = new Issuer(required(properties.getIssuerUri(), "issuer-uri"));
                    if (System.currentTimeMillis() < resolveFailUntil) {
                        throw new GeneralException("oidc provider metadata resolve fail recently, retry later.");
                    }
                    try {
                        OIDCProviderMetadata metadata = OIDCProviderMetadata.resolve(issuer, HTTP_TIMEOUT, HTTP_TIMEOUT);
                        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder
                                .create(metadata.getJWKSetURI().toURL(), new DefaultResourceRetriever(HTTP_TIMEOUT, HTTP_TIMEOUT))
                                .build();
                        current = provider = new Provider(metadata, jwkSource);
                    } catch (GeneralException | IOException | RuntimeException e) {
                        resolveFailUntil = System.currentTimeMillis() + RESOLVE_FAIL_BACKOFF;
                        throw e;
                    }
                }
            }
        }
        return current;
    }

    private ClientID clientId() {
        return new ClientID(required(properties.getClientId(), "client-id"));
    }

    private URI redirectUri() {
        return URI.create(required(properties.getRedirectUri(), "redirect-uri"));
    }

    private static String required(String value, String name) {
        if (StringTool.isBlank(value)) {
            throw new IllegalStateException("xxl-job oidc config invalid, xxl.job.oidc." + name + " is required.");
        }
        return value.trim();
    }

}

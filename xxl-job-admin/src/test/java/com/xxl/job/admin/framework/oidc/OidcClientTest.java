package com.xxl.job.admin.framework.oidc;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OidcClientTest {

    private static final String ISSUER = "https://idp.example.com/realms/xxl-job";
    private static final String CLIENT_ID = "xxl-job-admin";
    private static final String REDIRECT_URI = "http://localhost:8080/auth/oidc/callback";

    private static RSAKey rsaKey;
    private HttpServer tokenServer;
    private OidcProperties properties;
    private OidcClient oidcClient;

    // token endpoint stub: request captured, response id_token configurable
    private volatile String idToken;
    private volatile String tokenRequestBody;
    private volatile String tokenRequestAuthorization;

    @BeforeAll
    public static void generateKey() throws Exception {
        rsaKey = new RSAKeyGenerator(2048).keyID("k1").generate();
    }

    @BeforeEach
    public void setup() throws Exception {
        tokenServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        tokenServer.createContext("/token", exchange -> {
            tokenRequestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            tokenRequestAuthorization = exchange.getRequestHeaders().getFirst("Authorization");
            byte[] body = ("{\"access_token\":\"at\",\"token_type\":\"Bearer\",\"expires_in\":300,\"id_token\":\"" + idToken + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        tokenServer.start();

        OIDCProviderMetadata metadata = new OIDCProviderMetadata(new Issuer(ISSUER), List.of(SubjectType.PUBLIC), URI.create(ISSUER + "/certs"));
        metadata.setAuthorizationEndpointURI(URI.create(ISSUER + "/auth"));
        metadata.setTokenEndpointURI(URI.create("http://127.0.0.1:" + tokenServer.getAddress().getPort() + "/token"));
        metadata.setIDTokenJWSAlgs(List.of(JWSAlgorithm.RS256));

        properties = new OidcProperties();
        properties.setIssuerUri(ISSUER);
        properties.setClientId(CLIENT_ID);
        properties.setClientSecret("secret");
        properties.setRedirectUri(REDIRECT_URI);

        oidcClient = new OidcClient(properties, metadata, new ImmutableJWKSet<>(new JWKSet(rsaKey.toPublicJWK())));
    }

    @AfterEach
    public void teardown() {
        tokenServer.stop(0);
    }

    private String signIdToken(String audience, String nonce) throws Exception {
        Date now = new Date();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .audience(audience)
                .subject("u-1")
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 300_000))
                .claim("nonce", nonce)
                .claim("preferred_username", "alice")
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(rsaKey));
        return jwt.serialize();
    }

    @Test
    public void deriveLoginState() throws Exception {
        String secret = OidcClient.newSecret();
        assertEquals(43, secret.length());
        assertNotEquals(secret, OidcClient.newSecret());

        // deterministic, and independent of each other
        assertEquals(OidcClient.deriveState(secret), OidcClient.deriveState(secret));
        assertEquals(OidcClient.deriveNonce(secret), OidcClient.deriveNonce(secret));
        assertNotEquals(OidcClient.deriveState(secret), OidcClient.deriveNonce(secret));
        assertNotEquals(secret, OidcClient.deriveState(secret));

        // code_challenge = BASE64URL(SHA256(code_verifier))
        Map<String, List<String>> params = URLUtils.parseParameters(URI.create(
                oidcClient.buildAuthorizationUrl(OidcClient.deriveState(secret), OidcClient.deriveNonce(secret), secret)).getRawQuery());
        String expectedChallenge = Base64URL.encode(MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.US_ASCII))).toString();
        assertEquals(expectedChallenge, params.get("code_challenge").get(0));
    }

    @Test
    public void buildAuthorizationUrl() throws Exception {
        String url = oidcClient.buildAuthorizationUrl("s1", "n1", OidcClient.newSecret());

        assertTrue(url.startsWith(ISSUER + "/auth?"));
        Map<String, List<String>> params = URLUtils.parseParameters(URI.create(url).getRawQuery());
        assertEquals("code", params.get("response_type").get(0));
        assertEquals(CLIENT_ID, params.get("client_id").get(0));
        assertEquals(REDIRECT_URI, params.get("redirect_uri").get(0));
        assertEquals("openid profile email", params.get("scope").get(0));
        assertEquals("s1", params.get("state").get(0));
        assertEquals("n1", params.get("nonce").get(0));
        assertEquals("S256", params.get("code_challenge_method").get(0));
        assertNotNull(params.get("code_challenge"));
    }

    @Test
    public void exchangeAndValidate() throws Exception {
        String secret = OidcClient.newSecret();
        idToken = signIdToken(CLIENT_ID, "n1");

        Map<String, Object> claims = oidcClient.exchangeAndValidate("code-1", secret, "n1");

        assertEquals("alice", claims.get("preferred_username"));
        assertTrue(tokenRequestAuthorization.startsWith("Basic "));
        Map<String, List<String>> form = URLUtils.parseParameters(tokenRequestBody);
        assertEquals("authorization_code", form.get("grant_type").get(0));
        assertEquals("code-1", form.get("code").get(0));
        assertEquals(secret, form.get("code_verifier").get(0));
        assertEquals(REDIRECT_URI, form.get("redirect_uri").get(0));
    }

    @Test
    public void exchangeAsPublicClient() throws Exception {
        properties.setClientSecret("");
        idToken = signIdToken(CLIENT_ID, "n1");

        oidcClient.exchangeAndValidate("code-1", OidcClient.newSecret(), "n1");

        assertNull(tokenRequestAuthorization);
        assertEquals(CLIENT_ID, URLUtils.parseParameters(tokenRequestBody).get("client_id").get(0));
    }

    @Test
    public void rejectWrongNonce() throws Exception {
        idToken = signIdToken(CLIENT_ID, "n1");
        assertThrows(BadJOSEException.class, () -> oidcClient.exchangeAndValidate("code-1", OidcClient.newSecret(), "n2"));
    }

    @Test
    public void rejectWrongAudience() throws Exception {
        idToken = signIdToken("other-client", "n1");
        assertThrows(BadJOSEException.class, () -> oidcClient.exchangeAndValidate("code-1", OidcClient.newSecret(), "n1"));
    }

    @Test
    public void rejectWrongSignature() throws Exception {
        RSAKey otherKey = new RSAKeyGenerator(2048).keyID(rsaKey.getKeyID()).generate();
        SignedJWT jwt = SignedJWT.parse(signIdToken(CLIENT_ID, "n1"));
        SignedJWT forged = new SignedJWT(jwt.getHeader(), jwt.getJWTClaimsSet());
        forged.sign(new RSASSASigner(otherKey));
        idToken = forged.serialize();

        assertThrows(BadJOSEException.class, () -> oidcClient.exchangeAndValidate("code-1", OidcClient.newSecret(), "n1"));
    }

    @Test
    public void configValidatedLazily() {
        OidcClient client = new OidcClient(new OidcProperties());
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> client.buildAuthorizationUrl("s", "n", OidcClient.newSecret()));
        assertTrue(e.getMessage().contains("xxl.job.oidc."));
    }

    @Test
    public void resolveFailFastWithinBackoff() throws Exception {
        // closed port: first call fails on network, second call fails fast without request
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }
        OidcProperties unreachable = new OidcProperties();
        unreachable.setIssuerUri("http://127.0.0.1:" + closedPort);
        unreachable.setClientId(CLIENT_ID);
        unreachable.setRedirectUri(REDIRECT_URI);
        OidcClient client = new OidcClient(unreachable);

        assertThrows(IOException.class, () -> client.buildAuthorizationUrl("s", "n", OidcClient.newSecret()));
        GeneralException e = assertThrows(GeneralException.class, () -> client.buildAuthorizationUrl("s", "n", OidcClient.newSecret()));
        assertTrue(e.getMessage().contains("retry later"));
    }

}

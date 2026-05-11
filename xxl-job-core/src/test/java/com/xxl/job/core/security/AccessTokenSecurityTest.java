package com.xxl.job.core.security;

import com.xxl.job.core.server.EmbedServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Security tests for the fail-closed access token validation in EmbedServer.
 *
 * Verifies that:
 * 1. Null/empty server token rejects all requests (fail-closed)
 * 2. Correct token is accepted
 * 3. Wrong/missing request token is rejected
 */
@DisplayName("Access Token Security Tests")
class AccessTokenSecurityTest {

    // ==================== Fail-Closed: Server Token Not Configured ====================

    @Test
    @DisplayName("Reject when server token is null (fail-closed)")
    void testServerTokenNull_shouldReject() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken(null, "any_token");
        assertNotNull(result, "Should return error when server token is null");
        assertTrue(result.contains("not configured"), "Error should mention token not configured");
    }

    @Test
    @DisplayName("Reject when server token is empty string (fail-closed)")
    void testServerTokenEmpty_shouldReject() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("", "any_token");
        assertNotNull(result, "Should return error when server token is empty");
        assertTrue(result.contains("not configured"), "Error should mention token not configured");
    }

    @Test
    @DisplayName("Reject when server token is whitespace only (fail-closed)")
    void testServerTokenWhitespace_shouldReject() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("   ", "any_token");
        assertNotNull(result, "Should return error when server token is whitespace");
        assertTrue(result.contains("not configured"), "Error should mention token not configured");
    }

    // ==================== Request Token Validation ====================

    @Test
    @DisplayName("Reject when request token is null")
    void testRequestTokenNull_shouldReject() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("my_secret_token", null);
        assertNotNull(result, "Should return error when request token is null");
        assertTrue(result.contains("wrong"), "Error should mention token is wrong");
    }

    @Test
    @DisplayName("Reject when request token does not match server token")
    void testRequestTokenWrong_shouldReject() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("my_secret_token", "wrong_token");
        assertNotNull(result, "Should return error when tokens don't match");
        assertTrue(result.contains("wrong"), "Error should mention token is wrong");
    }

    @Test
    @DisplayName("Reject when request token is empty but server token is set")
    void testRequestTokenEmpty_shouldReject() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("my_secret_token", "");
        assertNotNull(result, "Should return error when request token is empty");
    }

    // ==================== Positive Cases: Normal Business Function ====================

    @Test
    @DisplayName("Accept when server token matches request token")
    void testTokenMatch_shouldAccept() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("my_secret_token", "my_secret_token");
        assertNull(result, "Should return null (no error) when tokens match");
    }

    @Test
    @DisplayName("Accept with strong token value")
    void testStrongToken_shouldAccept() {
        String strongToken = "a7f3b2c1-d4e5-6789-abcd-ef0123456789";
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken(strongToken, strongToken);
        assertNull(result, "Should accept with strong token");
    }

    // ==================== Regression: Old Bypass No Longer Works ====================

    @Test
    @DisplayName("Regression: null server token no longer allows any request through")
    void testRegressionNullTokenBypass() {
        // Old code: if (token != null && !token.isEmpty() && !token.equals(req)) — null token = all pass
        // New code: null token = reject all
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken(null, null);
        assertNotNull(result, "Null server token should no longer allow requests");
    }

    @Test
    @DisplayName("Regression: empty server token no longer allows any request through")
    void testRegressionEmptyTokenBypass() {
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("", "default_token");
        assertNotNull(result, "Empty server token should no longer allow requests");
    }

    @Test
    @DisplayName("Regression: default_token should not be treated specially")
    void testDefaultTokenStillWorks() {
        // If someone explicitly configures default_token (not recommended), it should still work
        String result = EmbedServer.EmbedHttpServerHandler.validateAccessToken("default_token", "default_token");
        assertNull(result, "default_token should still work if explicitly configured and matched");
    }
}

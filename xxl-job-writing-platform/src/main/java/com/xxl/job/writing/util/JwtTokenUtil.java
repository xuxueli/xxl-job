package com.xxl.job.writing.util;

import com.xxl.job.writing.auth.AuthenticatedUser;
import com.xxl.job.writing.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT token utility class
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${writing.platform.jwt.secret:default-jwt-secret-key-for-writing-platform-2024}")
    private String jwtSecret;

    @Value("${writing.platform.jwt.expiration-hours:24}")
    private Long expirationHours;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(AuthenticatedUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("userType", user.getUserType());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationHours * 60 * 60 * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUserId().toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate JWT token and extract authenticated user
     */
    public AuthenticatedUser validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get("userId", Long.class);
            Integer userType = claims.get("userType", Integer.class);

            if (userId == null || userType == null) {
                throw BusinessException.unauthorized("Invalid token claims");
            }

            return new AuthenticatedUser(userId, userType);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw BusinessException.unauthorized("Invalid or expired token");
        }
    }

    /**
     * Extract user ID from token (without full validation)
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
package com.xxl.job.writing.util;

import com.xxl.job.writing.auth.AuthenticatedUser;
import com.xxl.job.writing.auth.DefaultUserValidationService;
import com.xxl.job.writing.auth.UserValidationService;
import com.xxl.job.writing.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.util.Arrays;

import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;
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

    @Value("${writing.platform.jwt.secret:}")
    private String jwtSecret;

    @Value("${writing.platform.jwt.expiration-hours:24}")
    private Long expirationHours;

    @Value("${writing.platform.jwt.strict-validation:true}")
    private boolean strictValidation;

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void validateConfiguration() {
        Environment env = applicationContext.getEnvironment();
        boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

        // JWT密钥验证
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret must be configured");
        }

        // 生产环境强制严格验证
        if (isProd && !strictValidation) {
            throw new IllegalStateException(
                "Strict validation must be enabled in production environment"
            );
        }

        // 密钥长度警告（开发环境可宽松）
        if (jwtSecret.length() < 32) {
            if (strictValidation || isProd) {
                throw new IllegalStateException(
                    String.format("JWT secret is too short (%d chars). " +
                                "Minimum required length is 32 characters.",
                                jwtSecret.length())
                );
            } else if (isDev) {
                log.warn("JWT secret is too short ({}) chars. " +
                        "Minimum recommended length is 32 characters.",
                        jwtSecret.length());
            }
        }

        // 验证过期时间
        if (expirationHours > 24) {
            String message = String.format("JWT expiration time is too long (%d hours). Maximum recommended is 24 hours.", expirationHours);
            if (strictValidation) {
                log.warn(message + " Consider reducing token lifetime for better security.");
            } else {
                log.warn(message);
            }
        }

        // 验证用户状态检查配置
        if (strictValidation && userValidationService instanceof DefaultUserValidationService) {
            log.warn("DefaultUserValidationService is being used. In production, a proper UserValidationService implementation is required.");
        }
    }

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
                throw BusinessException.unauthorized("Invalid or expired token");
            }

            // 检查用户状态（如果配置了UserValidationService）
            if (userValidationService != null) {
                if (!userValidationService.isValidUser(userId, userType)) {
                    log.warn("User validation failed for userId: {}, userType: {}", userId, userType);
                    throw BusinessException.unauthorized("Invalid or expired token");
                }
                if (userValidationService.isUserLocked(userId)) {
                    log.warn("User account is locked for userId: {}", userId);
                    throw BusinessException.unauthorized("Invalid or expired token");
                }
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
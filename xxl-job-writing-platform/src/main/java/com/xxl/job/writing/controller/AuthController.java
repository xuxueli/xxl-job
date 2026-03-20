package com.xxl.job.writing.controller;

import com.xxl.job.writing.auth.AuthenticatedUser;
import com.xxl.job.writing.dto.LoginDTO;
import com.xxl.job.writing.dto.TokenResponseDTO;
import com.xxl.job.writing.exception.BusinessException;
import com.xxl.job.writing.util.JwtTokenUtil;
import com.xxl.job.writing.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、令牌刷新等认证相关接口")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${writing.platform.jwt.expiration-hours:24}")
    private Long expirationHours;

    @Value("${writing.platform.auth.mock-enabled:false}")
    private boolean mockEnabled;

    @Value("${writing.platform.auth.mock-credentials.admin.password:}")
    private String mockAdminPassword;

    @Value("${writing.platform.auth.mock-credentials.admin.user-id:1}")
    private Long mockAdminUserId;

    @Value("${writing.platform.auth.mock-credentials.admin.user-type:0}")
    private Integer mockAdminUserType;

    @Value("${writing.platform.auth.mock-credentials.expert.password:}")
    private String mockExpertPassword;

    @Value("${writing.platform.auth.mock-credentials.expert.user-id:2}")
    private Long mockExpertUserId;

    @Value("${writing.platform.auth.mock-credentials.expert.user-type:1}")
    private Integer mockExpertUserType;

    public AuthController(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT令牌")
    public Result<TokenResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录: {}", loginDTO.getUsername());

        // 模拟用户验证 - 实际项目中应该查询数据库验证用户凭据
        AuthenticatedUser user = authenticateUser(loginDTO.getUsername(), loginDTO.getPassword());
        if (user == null) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        String token = jwtTokenUtil.generateToken(user);
        TokenResponseDTO tokenResponse = new TokenResponseDTO(token, expirationHours);
        return Result.success(tokenResponse);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用有效令牌刷新新令牌")
    public Result<TokenResponseDTO> refresh() {
        // 在实际项目中，需要从安全上下文中获取当前用户
        // 这里模拟返回新的令牌
        throw BusinessException.notImplemented("Token refresh not implemented yet");
    }

    /**
     * 模拟用户认证
     * 仅用于开发和测试环境，生产环境应连接数据库验证
     */
    private AuthenticatedUser authenticateUser(String username, String password) {
        if (!mockEnabled) {
            log.warn("Mock authentication is disabled. This endpoint should not be used in production.");
            return null;
        }

        // 检查admin用户
        if ("admin".equals(username) && mockAdminPassword != null && !mockAdminPassword.isEmpty()) {
            if (mockAdminPassword.equals(password)) {
                log.info("Mock authentication successful for admin user");
                return new AuthenticatedUser(mockAdminUserId, mockAdminUserType);
            }
        }

        // 检查expert用户
        if ("expert".equals(username) && mockExpertPassword != null && !mockExpertPassword.isEmpty()) {
            if (mockExpertPassword.equals(password)) {
                log.info("Mock authentication successful for expert user");
                return new AuthenticatedUser(mockExpertUserId, mockExpertUserType);
            }
        }

        log.warn("Mock authentication failed for user: {}", username);
        return null;
    }

    @PostConstruct
    public void validateMockConfiguration() {
        Environment env = applicationContext.getEnvironment();
        boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");

        if (mockEnabled && isProd) {
            throw new IllegalStateException(
                "Mock authentication cannot be enabled in production environment. " +
                "Check application-prod.yml configuration."
            );
        }

        if (mockEnabled) {
            log.info("Mock authentication enabled for profiles: {}",
                     String.join(",", env.getActiveProfiles()));
        }
    }
}
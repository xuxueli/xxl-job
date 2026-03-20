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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、令牌刷新等认证相关接口")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${writing.platform.jwt.expiration-hours:24}")
    private Long expirationHours;

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
     * 实际项目中应该从数据库验证用户凭据并返回用户信息
     */
    private AuthenticatedUser authenticateUser(String username, String password) {
        // 模拟用户数据 - 实际项目中应该查询数据库
        // 这里假设用户ID为1，用户类型为0（普通用户）
        // 实际项目中应该根据用户名查询用户信息并验证密码
        if ("admin".equals(username) && "password".equals(password)) {
            return new AuthenticatedUser(1L, 0); // 普通用户
        }
        if ("expert".equals(username) && "password".equals(password)) {
            return new AuthenticatedUser(2L, 1); // 专家用户
        }
        return null;
    }
}
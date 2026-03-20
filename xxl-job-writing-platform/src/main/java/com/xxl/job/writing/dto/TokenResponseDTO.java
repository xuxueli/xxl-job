package com.xxl.job.writing.dto;

import lombok.Data;

/**
 * 令牌响应DTO
 */
@Data
public class TokenResponseDTO {
    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（小时）
     */
    private Long expiresInHours;

    public TokenResponseDTO(String accessToken, Long expiresInHours) {
        this.accessToken = accessToken;
        this.expiresInHours = expiresInHours;
    }
}
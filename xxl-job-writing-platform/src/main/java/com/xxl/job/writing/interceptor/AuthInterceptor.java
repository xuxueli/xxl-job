package com.xxl.job.writing.interceptor;

import com.xxl.job.writing.auth.AuthContextHolder;
import com.xxl.job.writing.auth.AuthenticatedUser;
import com.xxl.job.writing.exception.BusinessException;
import com.xxl.job.writing.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Basic header-based authentication interceptor.
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REQUEST_USER_ATTRIBUTE = "authenticatedUser";

    private final JwtTokenUtil jwtTokenUtil;

    public AuthInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw BusinessException.unauthorized("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw BusinessException.unauthorized("Empty token");
        }

        AuthenticatedUser authenticatedUser = jwtTokenUtil.validateToken(token);
        AuthContextHolder.setCurrentUser(authenticatedUser);
        request.setAttribute(REQUEST_USER_ATTRIBUTE, authenticatedUser);
        log.debug("Authenticated request userId: {}, userType: {}", authenticatedUser.getUserId(), authenticatedUser.getUserType());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();
    }

}

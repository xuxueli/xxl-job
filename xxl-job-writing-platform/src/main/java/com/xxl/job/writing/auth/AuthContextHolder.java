package com.xxl.job.writing.auth;

import com.xxl.job.writing.exception.BusinessException;

/**
 * Stores authenticated user information for the current request thread.
 */
public final class AuthContextHolder {
    private static final ThreadLocal<AuthenticatedUser> CONTEXT = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void setCurrentUser(AuthenticatedUser user) {
        CONTEXT.set(user);
    }

    public static AuthenticatedUser getCurrentUser() {
        return CONTEXT.get();
    }

    public static AuthenticatedUser requireCurrentUser() {
        AuthenticatedUser user = CONTEXT.get();
        if (user == null) {
            throw BusinessException.unauthorized();
        }
        return user;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

package com.xxl.job.writing.auth;

import lombok.Getter;

/**
 * Authenticated request user snapshot.
 */
@Getter
public class AuthenticatedUser {
    private final Long userId;
    private final Integer userType;

    public AuthenticatedUser(Long userId, Integer userType) {
        this.userId = userId;
        this.userType = userType;
    }

    public boolean isExpert() {
        return Integer.valueOf(1).equals(userType);
    }
}

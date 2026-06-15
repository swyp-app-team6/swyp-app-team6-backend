package org.swyp.com.backend.user.dto;

import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.user.domain.User;

public record UserMeResponse(Long id, String email, UserRole role, OAuthProvider provider) {

    public static UserMeResponse from(User user) {
        return new UserMeResponse(user.getId(), user.getEmail(), user.getRole(), user.getProvider());
    }
}

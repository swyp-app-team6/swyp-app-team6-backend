package org.swyp.com.backend.auth.oauth.dto;

import org.swyp.com.backend.global.enumeration.UserRole;

public record GoogleAuthResult(Long userId, UserRole role) {

}

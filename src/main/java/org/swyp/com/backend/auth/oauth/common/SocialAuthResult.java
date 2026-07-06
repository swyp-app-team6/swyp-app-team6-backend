package org.swyp.com.backend.auth.oauth.common;

import org.swyp.com.backend.global.enumeration.UserRole;

public record SocialAuthResult(Long userId, UserRole role, boolean requiresTermsAgreement) {}

package org.swyp.com.backend.auth.oauth.apple.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleTokenResponse(
        String accessToken,
        String tokenType,
        Integer expiresIn,
        String refreshToken,
        String idToken
) {}

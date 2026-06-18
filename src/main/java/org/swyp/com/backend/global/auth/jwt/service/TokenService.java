package org.swyp.com.backend.global.auth.jwt.service;

import org.swyp.com.backend.global.auth.dto.TokenResponse;
import org.swyp.com.backend.global.auth.jwt.CustomClaims;
import org.swyp.com.backend.global.enumeration.UserRole;

public interface TokenService {

    TokenResponse issueTokenPair(Long userId, UserRole role);

    CustomClaims validateToken(String token);

    void verifyRefreshTokenJti(Long userId, String jti);

}

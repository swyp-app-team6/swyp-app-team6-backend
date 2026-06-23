package org.swyp.com.backend.auth.jwt.service;

import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.global.enumeration.UserRole;

public interface TokenService {

    TokenResponse issueTokenPair(Long userId, UserRole role);

    TokenResponse reissueTokenPair(String refreshToken);
}

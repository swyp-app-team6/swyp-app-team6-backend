package org.swyp.com.backend.global.auth.login.service;

import org.swyp.com.backend.global.auth.dto.TokenResponse;

public interface LoginService {

    TokenResponse refreshTokens(String token);
}

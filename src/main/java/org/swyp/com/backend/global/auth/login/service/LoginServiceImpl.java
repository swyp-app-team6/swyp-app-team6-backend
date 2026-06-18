package org.swyp.com.backend.global.auth.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.auth.dto.TokenResponse;
import org.swyp.com.backend.global.auth.jwt.CustomClaims;
import org.swyp.com.backend.global.auth.jwt.service.TokenService;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginServiceImpl implements LoginService {

    private final TokenService tokenService;

    @Override
    public TokenResponse refreshTokens(String token) {
        CustomClaims claims = tokenService.validateToken(token);
        tokenService.verifyRefreshTokenJti(claims.getUserId(), claims.getJti());
        return tokenService.issueTokenPair(claims.getUserId(), claims.getRole());
    }
}

package org.swyp.com.backend.global.auth.jwt.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.auth.jwt.CustomClaims;
import org.swyp.com.backend.global.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.auth.jwt.domain.RefreshToken;
import org.swyp.com.backend.global.auth.jwt.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.global.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.global.enumeration.TokenType;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.global.exception.BusinessException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public TokenResponse issueTokenPair(Long userId, UserRole role) {
        CustomClaims accessToken = tokenProvider.generateToken(TokenType.ACCESS, userId, role);
        CustomClaims refreshToken = tokenProvider.generateToken(TokenType.REFRESH, userId, role);
        persistRefreshToken(refreshToken);
        return new TokenResponse(accessToken.getToken(), refreshToken.getToken());
    }

    @Override
    @Transactional
    public TokenResponse reissueTokenPair(String refreshToken) {
        CustomClaims claims = tokenProvider.validateToken(refreshToken);
        verifyRefreshTokenJti(claims.getUserId(), claims.getJti());
        return issueTokenPair(claims.getUserId(), claims.getRole());
    }

    private void persistRefreshToken(CustomClaims refreshToken) {
        Long userId = refreshToken.getUserId();
        Optional<RefreshToken> existing = refreshTokenRepository.findByUserId(userId);
        if (existing.isPresent()) {
            existing.get().setJti(refreshToken.getJti());
            existing.get().setExpiresAt(refreshToken.getExpiresAt());
        } else {
            refreshTokenRepository.save(new RefreshToken(userId, refreshToken.getJti(), refreshToken.getExpiresAt()));
        }
    }

    private void verifyRefreshTokenJti(Long userId, String jti) {
        refreshTokenRepository.findByUserId(userId).ifPresent(stored -> {
            if (!stored.getJti().equals(jti)) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED, "refresh Token 값 불일치");
            }
        });
    }
}

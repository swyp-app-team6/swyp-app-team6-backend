package org.swyp.com.backend.auth.oauth.apple.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.auth.oauth.apple.domain.AppleRefreshToken;
import org.swyp.com.backend.auth.oauth.apple.domain.repository.AppleRefreshTokenRepository;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleTokenResponse;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleAuthService {

    private final AppleTokenVerifier tokenVerifier;
    private final AppleTokenClient tokenClient;
    private final UserRepository userRepository;
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;

    @Transactional
    public SocialAuthResult loginWithIdentityToken(String identityToken, String authorizationCode) {
        Claims claims = tokenVerifier.verify(identityToken);
        User user = processAppleUser(claims);
        trySaveRefreshTokenFromCode(user.getId(), authorizationCode);
        return new SocialAuthResult(user.getId(), user.getRole());
    }

    @Transactional
    public SocialAuthResult loginWithCode(String code) {
        AppleTokenResponse appleToken = tokenClient.exchangeCode(code);
        Claims claims = tokenVerifier.verify(appleToken.idToken());
        User user = processAppleUser(claims);
        saveRefreshToken(user.getId(), appleToken.refreshToken());
        return new SocialAuthResult(user.getId(), user.getRole());
    }

    private User processAppleUser(Claims claims) {
        String sub = claims.getSubject();
        String email = claims.get("email", String.class);

        // 1. Apple sub로 기존 가입 유저 조회
        return userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, sub)
                .orElseGet(() -> {
                    String resolvedEmail = (email != null && !email.isBlank())
                            ? email
                            : sub + "@apple.placeholder";

                    // 2. 동일 이메일로 다른 provider(구글 등) 가입 이력 있으면 해당 유저 반환
                    return userRepository.findByEmail(resolvedEmail)
                            .orElseGet(() -> userRepository.save(
                                    User.createOAuthUser(resolvedEmail, OAuthProvider.APPLE, sub, UserRole.USER)
                            ));
                });
    }

    private void trySaveRefreshTokenFromCode(Long userId, String authorizationCode) {
        if (authorizationCode == null || authorizationCode.isBlank()) {
            return;
        }
        try {
            AppleTokenResponse tokenResponse = tokenClient.exchangeCode(authorizationCode);
            saveRefreshToken(userId, tokenResponse.refreshToken());
        } catch (Exception e) {
            log.warn("Apple authorizationCode 교환 실패로 refresh_token을 저장하지 못했습니다. userId={}", userId, e);
        }
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        appleRefreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        existing -> existing.updateRefreshToken(refreshToken),
                        () -> appleRefreshTokenRepository.save(new AppleRefreshToken(userId, refreshToken))
                );
    }
}

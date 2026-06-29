package org.swyp.com.backend.auth.oauth.apple.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleTokenResponse;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleAuthService {

    private final AppleTokenVerifier tokenVerifier;
    private final AppleTokenClient tokenClient;
    private final UserRepository userRepository;

    @Transactional
    public SocialAuthResult loginWithIdentityToken(String identityToken) {
        Claims claims = tokenVerifier.verify(identityToken);
        return processAppleUser(claims);
    }

    @Transactional
    public SocialAuthResult loginWithCode(String code) {
        AppleTokenResponse appleToken = tokenClient.exchangeCode(code);
        Claims claims = tokenVerifier.verify(appleToken.idToken());
        return processAppleUser(claims);
    }

    private SocialAuthResult processAppleUser(Claims claims) {
        String sub = claims.getSubject();
        String email = claims.get("email", String.class);

        // 1. Apple sub로 기존 가입 유저 조회
        User user = userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, sub)
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

        return new SocialAuthResult(user.getId(), user.getRole());
    }
}

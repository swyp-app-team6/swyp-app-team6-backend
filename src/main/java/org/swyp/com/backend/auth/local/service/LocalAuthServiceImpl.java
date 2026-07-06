package org.swyp.com.backend.auth.local.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.terms.service.TermsService;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalAuthServiceImpl implements LocalAuthService {

    private static final String LOGIN_FAIL_MESSAGE = "아이디 또는 비밀번호가 일치하지 않습니다.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TermsService termsService;

    @Override
    @Transactional
    public SocialAuthResult signup(String email, String rawPassword) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 사용중인 아이디입니다.");
        });

        User user = userRepository.save(User.createLocalUser(email, passwordEncoder.encode(rawPassword), UserRole.USER));

        boolean requiresTermsAgreement = !termsService.hasCompletedRequiredAgreements(user);
        return new SocialAuthResult(user.getId(), user.getRole(), requiresTermsAgreement);
    }

    @Override
    @Transactional
    public SocialAuthResult login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, LOGIN_FAIL_MESSAGE));

        if (user.getProvider() != OAuthProvider.LOCAL
                || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, LOGIN_FAIL_MESSAGE);
        }

        boolean requiresTermsAgreement = !termsService.hasCompletedRequiredAgreements(user);
        return new SocialAuthResult(user.getId(), user.getRole(), requiresTermsAgreement);
    }
}

package org.swyp.com.backend.auth.local.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;
import static org.swyp.com.backend.support.UserTestFixture.createUser;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.terms.service.TermsService;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LocalAuthServiceTest {

    private static final String RAW_PASSWORD = "password1234";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TermsService termsService;

    LocalAuthService localAuthService;

    @BeforeEach
    void setUp() {
        localAuthService = new LocalAuthServiceImpl(userRepository, passwordEncoder, termsService);
    }

    @Test
    void signup_신규아이디_저장후토큰발급대상반환() {
        // given
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        User savedUser = User.createLocalUser(TEST_USER_EMAIL, ENCODED_PASSWORD, TEST_ROLE);
        ReflectionTestUtils.setField(savedUser, "id", TEST_USER_ID);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(termsService.hasCompletedRequiredAgreements(savedUser)).thenReturn(false);

        // when
        SocialAuthResult result = localAuthService.signup(TEST_USER_EMAIL, RAW_PASSWORD);

        // then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        assertThat(result.requiresTermsAgreement()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_이미존재하는아이디_BusinessException() {
        // given
        when(userRepository.findByEmail(TEST_USER_EMAIL))
                .thenReturn(Optional.of(createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE)));

        // when & then
        assertThrows(BusinessException.class, () -> localAuthService.signup(TEST_USER_EMAIL, RAW_PASSWORD));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_성공_토큰발급대상반환() {
        // given
        User user = User.createLocalUser(TEST_USER_EMAIL, ENCODED_PASSWORD, TEST_ROLE);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(termsService.hasCompletedRequiredAgreements(user)).thenReturn(true);

        // when
        SocialAuthResult result = localAuthService.login(TEST_USER_EMAIL, RAW_PASSWORD);

        // then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        assertThat(result.requiresTermsAgreement()).isFalse();
    }

    @Test
    void login_존재하지않는아이디_BusinessException() {
        // given
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> localAuthService.login(TEST_USER_EMAIL, RAW_PASSWORD));
    }

    @Test
    void login_비밀번호불일치_BusinessException() {
        // given
        User user = User.createLocalUser(TEST_USER_EMAIL, ENCODED_PASSWORD, TEST_ROLE);
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        // when & then
        assertThrows(BusinessException.class, () -> localAuthService.login(TEST_USER_EMAIL, RAW_PASSWORD));
    }

    @Test
    void login_SSO로가입된아이디_BusinessException() {
        // given
        User ssoUser = User.createOAuthUser(TEST_USER_EMAIL, OAuthProvider.GOOGLE, "google-sub", TEST_ROLE);
        ReflectionTestUtils.setField(ssoUser, "id", TEST_USER_ID);
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(Optional.of(ssoUser));

        // when & then
        assertThrows(BusinessException.class, () -> localAuthService.login(TEST_USER_EMAIL, RAW_PASSWORD));
        verify(passwordEncoder, never()).matches(any(), any());
    }
}

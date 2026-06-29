package org.swyp.com.backend.auth.oauth.apple.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AppleAuthServiceTest {

    @Mock
    AppleTokenVerifier tokenVerifier;
    @Mock
    AppleTokenClient tokenClient;
    @Mock
    UserRepository userRepository;
    @Mock
    Claims claims;

    AppleAuthService appleAuthService;

    static final String APPLE_SUB = "000539.c623727...";
    static final String EMAIL = "test@example.com";

    @BeforeEach
    void setup() {
        appleAuthService = new AppleAuthService(tokenVerifier, tokenClient, userRepository);
    }

    @Test
    void loginWithIdentityToken_신규Apple유저_저장() {
        //given
        when(tokenVerifier.verify(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);
        when(claims.get("email", String.class)).thenReturn(EMAIL);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        User savedUser = User.createOAuthUser(EMAIL, OAuthProvider.APPLE, APPLE_SUB, UserRole.USER);
        setUserId(savedUser, TEST_USER_ID);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //when
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token");

        //then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        assertThat(result.role()).isEqualTo(UserRole.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginWithIdentityToken_기존Apple유저_조회() {
        //given
        when(tokenVerifier.verify(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);

        User existingUser = User.createOAuthUser(EMAIL, OAuthProvider.APPLE, APPLE_SUB, UserRole.USER);
        setUserId(existingUser, TEST_USER_ID);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.of(existingUser));

        //when
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token");

        //then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginWithIdentityToken_동일이메일타Provider유저_조회() {
        //given
        when(tokenVerifier.verify(any())).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);
        when(claims.get("email", String.class)).thenReturn(EMAIL);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.empty());

        User googleUser = User.createOAuthUser(EMAIL, OAuthProvider.GOOGLE, "google-sub", UserRole.USER);
        setUserId(googleUser, TEST_USER_ID);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(googleUser));

        //when
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token");

        //then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    private void setUserId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

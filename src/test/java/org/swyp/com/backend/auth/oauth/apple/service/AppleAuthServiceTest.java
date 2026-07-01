package org.swyp.com.backend.auth.oauth.apple.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.auth.oauth.apple.domain.AppleRefreshToken;
import org.swyp.com.backend.auth.oauth.apple.domain.repository.AppleRefreshTokenRepository;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleTokenResponse;
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
    AppleRefreshTokenRepository appleRefreshTokenRepository;
    @Mock
    Claims claims;

    AppleAuthService appleAuthService;

    static final String APPLE_SUB = "000539.c623727...";
    static final String EMAIL = "test@example.com";

    @BeforeEach
    void setup() {
        appleAuthService = new AppleAuthService(tokenVerifier, tokenClient, userRepository, appleRefreshTokenRepository);
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
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token", null);

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
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token", null);

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
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token", null);

        //then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginWithIdentityToken_authorizationCode있으면_refreshToken신규저장() {
        //given
        when(tokenVerifier.verify("dummy.token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);

        User existingUser = User.createOAuthUser(EMAIL, OAuthProvider.APPLE, APPLE_SUB, UserRole.USER);
        setUserId(existingUser, TEST_USER_ID);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.of(existingUser));

        when(tokenClient.exchangeCode("auth-code"))
                .thenReturn(new AppleTokenResponse("access", "bearer", 3600, "apple-refresh-token", "id-token"));
        when(appleRefreshTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        //when
        appleAuthService.loginWithIdentityToken("dummy.token", "auth-code");

        //then
        ArgumentCaptor<AppleRefreshToken> captor = ArgumentCaptor.forClass(AppleRefreshToken.class);
        verify(appleRefreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(captor.getValue().getRefreshToken()).isEqualTo("apple-refresh-token");
    }

    @Test
    void loginWithIdentityToken_authorizationCode있고_기존토큰있으면_갱신() {
        //given
        when(tokenVerifier.verify("dummy.token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);

        User existingUser = User.createOAuthUser(EMAIL, OAuthProvider.APPLE, APPLE_SUB, UserRole.USER);
        setUserId(existingUser, TEST_USER_ID);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.of(existingUser));

        when(tokenClient.exchangeCode("auth-code"))
                .thenReturn(new AppleTokenResponse("access", "bearer", 3600, "new-refresh-token", "id-token"));
        AppleRefreshToken existingToken = new AppleRefreshToken(TEST_USER_ID, "old-refresh-token");
        when(appleRefreshTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(existingToken));

        //when
        appleAuthService.loginWithIdentityToken("dummy.token", "auth-code");

        //then
        assertThat(existingToken.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(appleRefreshTokenRepository, never()).save(any(AppleRefreshToken.class));
    }

    @Test
    void loginWithIdentityToken_authorizationCode없으면_토큰교환스킵() {
        //given
        when(tokenVerifier.verify("dummy.token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);

        User existingUser = User.createOAuthUser(EMAIL, OAuthProvider.APPLE, APPLE_SUB, UserRole.USER);
        setUserId(existingUser, TEST_USER_ID);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.of(existingUser));

        //when
        appleAuthService.loginWithIdentityToken("dummy.token", null);

        //then
        verify(tokenClient, never()).exchangeCode(any());
        verify(appleRefreshTokenRepository, never()).findByUserId(any());
    }

    @Test
    void loginWithIdentityToken_authorizationCode교환실패해도_로그인은성공() {
        //given
        when(tokenVerifier.verify("dummy.token")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(APPLE_SUB);

        User existingUser = User.createOAuthUser(EMAIL, OAuthProvider.APPLE, APPLE_SUB, UserRole.USER);
        setUserId(existingUser, TEST_USER_ID);
        when(userRepository.findByProviderAndProviderUserId(OAuthProvider.APPLE, APPLE_SUB))
                .thenReturn(Optional.of(existingUser));

        when(tokenClient.exchangeCode("auth-code")).thenThrow(new RuntimeException("apple token exchange failed"));

        //when
        SocialAuthResult result = appleAuthService.loginWithIdentityToken("dummy.token", "auth-code");

        //then
        assertThat(result.userId()).isEqualTo(TEST_USER_ID);
        verify(appleRefreshTokenRepository, never()).save(any(AppleRefreshToken.class));
    }

    @Test
    void revoke_저장된토큰있으면_apple에revoke요청후로컬삭제() {
        //given
        AppleRefreshToken token = new AppleRefreshToken(TEST_USER_ID, "apple-refresh-token");
        when(appleRefreshTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(token));

        //when
        appleAuthService.revoke(TEST_USER_ID);

        //then
        verify(tokenClient).revoke("apple-refresh-token");
        verify(appleRefreshTokenRepository).delete(token);
    }

    @Test
    void revoke_저장된토큰없으면_아무일도하지않음() {
        //given
        when(appleRefreshTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        //when
        appleAuthService.revoke(TEST_USER_ID);

        //then
        verify(tokenClient, never()).revoke(any());
        verify(appleRefreshTokenRepository, never()).delete(any());
    }

    @Test
    void revoke_apple요청실패해도_로컬레코드는삭제() {
        //given
        AppleRefreshToken token = new AppleRefreshToken(TEST_USER_ID, "apple-refresh-token");
        when(appleRefreshTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(token));
        doThrow(new RuntimeException("apple revoke failed")).when(tokenClient).revoke("apple-refresh-token");

        //when
        appleAuthService.revoke(TEST_USER_ID);

        //then
        verify(appleRefreshTokenRepository).delete(token);
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

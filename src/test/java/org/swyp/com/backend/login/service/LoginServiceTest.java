package org.swyp.com.backend.login.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.swyp.com.backend.global.auth.domain.RefreshToken;
import org.swyp.com.backend.global.auth.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.global.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.login.dto.TokenResponse;
import org.swyp.com.backend.support.JwtTestFixture;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    static final String TEST_PASSWORD = "password";

    @Mock
    UserRepository userRepository;
    @Mock
    RefreshTokenRepository refreshTokenRepository;

    PasswordEncoder passwordEncoder;
    TokenProvider tokenProvider;
    LoginService loginService;
    User testUser;
    Key testKey;

    @BeforeEach
    void set() {
        passwordEncoder = new BCryptPasswordEncoder();
        testKey = JwtTestFixture.buildKey();
        tokenProvider = JwtTestFixture.buildTokenProvider(testKey);
        loginService = new LoginServiceImpl(userRepository, passwordEncoder, refreshTokenRepository, tokenProvider);
        testUser = new User(JwtTestFixture.TEST_USER_ID, "user@example.com", passwordEncoder.encode(TEST_PASSWORD),
                JwtTestFixture.ROLE, LocalDateTime.now(), null);
    }

    @Test
    void loginSuccessTest() {
        // given
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        // when
        TokenResponse token = loginService.login("user@example.com", TEST_PASSWORD);

        // then
        Assertions.assertInstanceOf(TokenResponse.class, token);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        Assertions.assertNotNull(token.accessToken());
        Assertions.assertNotNull(token.refreshToken());
    }

    @Test
    void loginSuccessTest_whenRefreshTokenExists_shouldUpdate() {
        // given
        RefreshToken existingToken = new RefreshToken(JwtTestFixture.TEST_USER_ID, "old-jti", new Date());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID)).thenReturn(Optional.of(existingToken));

        // when
        loginService.login("user@example.com", TEST_PASSWORD);

        // then
        verify(refreshTokenRepository, never()).save(any());
        assertNotEquals("old-jti", existingToken.getJti());
    }

    @Test
    void loginFailTest_accountNotExist() {
        // given & when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            loginService.login("user@example.com", TEST_PASSWORD);
        });
    }

    @Test
    void loginFailTest_passwordNotMatch() {
        // given
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            loginService.login("user@example.com", "notValidPassword");
        });
    }

    @Test
    void refreshSuccessTest() {
        // given
        Date issuedDate = new Date();
        Date expiresDate = new Date(issuedDate.getTime() + JwtTestFixture.REFRESH_EXP);
        String jti = UUID.randomUUID().toString();
        String storedToken = JwtTestFixture.buildToken(testKey, jti, new Date(0), expiresDate);

        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID))
                .thenReturn(Optional.of(new RefreshToken(JwtTestFixture.TEST_USER_ID, jti, expiresDate)));

        // when
        TokenResponse rftokenResponse = loginService.refreshTokens(storedToken);

        // then
        assertNotEquals(storedToken, rftokenResponse.refreshToken());
    }

    @Test
    void refreshFailTest_notValidRequestData() {
        // given & when & then
        Assertions.assertThrows(MalformedJwtException.class, () -> {
            loginService.refreshTokens("notValidToken");
        });
    }

    @Test
    void refreshFailTest_notValidRefreshToken() {
        // given
        Key invalidKey = JwtTestFixture.buildKey();
        Date issuedDate = new Date();
        Date expiresDate = new Date(issuedDate.getTime() + JwtTestFixture.REFRESH_EXP);
        String generatedToken = JwtTestFixture.buildToken(invalidKey, UUID.randomUUID().toString(), issuedDate,
                expiresDate);

        // when & then
        Assertions.assertThrows(SignatureException.class, () -> {
            loginService.refreshTokens(generatedToken);
        });
    }

    @Test
    void refreshFailTest_tokenUUIDNotMatch() {
        // given
        Date issuedDate = new Date();
        Date expiresDate = new Date(issuedDate.getTime() + JwtTestFixture.REFRESH_EXP);
        String jti = UUID.randomUUID().toString();
        String storedToken = JwtTestFixture.buildToken(testKey, jti, new Date(0), expiresDate);

        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID))
                .thenReturn(Optional.of(new RefreshToken(JwtTestFixture.TEST_USER_ID, "not_match_uuid", expiresDate)));

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            loginService.refreshTokens(storedToken);
        });
    }
}

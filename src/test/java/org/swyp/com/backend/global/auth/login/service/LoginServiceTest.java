package org.swyp.com.backend.global.auth.login.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import org.swyp.com.backend.global.auth.dto.TokenResponse;
import org.swyp.com.backend.global.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.auth.jwt.service.TokenService;
import org.swyp.com.backend.global.auth.jwt.service.TokenServiceImpl;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.support.JwtTestFixture;
import org.swyp.com.backend.user.domain.User;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    PasswordEncoder passwordEncoder;
    TokenProvider tokenProvider;
    TokenService tokenService;
    LoginService loginService;
    User testUser;
    Key testKey;

    @BeforeEach
    void set() {
        passwordEncoder = new BCryptPasswordEncoder();
        testKey = JwtTestFixture.buildKey();
        tokenProvider = JwtTestFixture.buildTokenProvider(testKey);
        tokenService = new TokenServiceImpl(tokenProvider, refreshTokenRepository);
        loginService = new LoginServiceImpl(tokenService);
        testUser = new User(JwtTestFixture.TEST_USER_ID, "user@example.com",
                JwtTestFixture.ROLE, null, null, LocalDateTime.now(), null);
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

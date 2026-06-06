package org.swyp.com.backend.login.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.swyp.com.backend.global.auth.JwtTokenProvider;
import org.swyp.com.backend.global.auth.RefreshTokenRepository;
import org.swyp.com.backend.global.auth.TokenProvider;
import org.swyp.com.backend.global.exception.LoginException;
import org.swyp.com.backend.login.dto.TokenResponse;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

class LoginServiceTest {
    PasswordEncoder passwordEncoder;
    String testEmail;
    String testPassword;
    String testEncodedPassword;
    String[] roles;
    Key testKey;
    long accessExp;
    long refreshExp;
    LocalDateTime currentTime;

    @BeforeEach
    void set() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.testEmail = "user@example.com";
        this.testPassword = "password";
        this.testEncodedPassword = passwordEncoder.encode(testPassword);
        this.roles = new String[]{"USER"};
        this.testKey = Jwts.SIG.HS256.key().build();
        this.accessExp = 1000L * 60 * 30;
        this.refreshExp = 1000L * 60 * 60 * 24 * 14;
        this.currentTime = LocalDateTime.now();
    }

    @Test
    void loginSuccessTest() {
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);

        User testUser = new User(1L, testEmail, testEncodedPassword, roles, currentTime, null);

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        LoginService loginService = new LoginServiceImpl(userRepository, passwordEncoder, refreshTokenRepository,
                tokenProvider);

        TokenResponse token = loginService.login(testEmail, testPassword);

        Assertions.assertInstanceOf(TokenResponse.class, token);
        Assertions.assertNotNull(token.accessToken());
        Assertions.assertNotNull(token.refreshToken());
    }

    @Test
    void loginFailTest_accountNotExist() { //계정 정보 없음
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);

        LoginService loginService = new LoginServiceImpl(userRepository, passwordEncoder, refreshTokenRepository,
                tokenProvider);

        Assertions.assertThrows(LoginException.class, () -> {
            loginService.login(testEmail, testPassword);
        });
    }

    @Test
    void loginFailTest_passwordNotMatch() { //패스워드 불일치
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        UserRepository userRepository = mock(UserRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);

        User testUser = new User(1L, testEmail, "notValidPassword", roles, currentTime, null);

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        LoginService loginService = new LoginServiceImpl(userRepository, passwordEncoder, refreshTokenRepository,
                tokenProvider);

        Assertions.assertThrows(LoginException.class, () -> {
            loginService.login(testEmail, testPassword);
        });
    }
}
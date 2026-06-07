package org.swyp.com.backend.login.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.swyp.com.backend.global.auth.JwtTokenProvider;
import org.swyp.com.backend.global.auth.RefreshToken;
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
    String encoded;
    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    TokenProvider tokenProvider;
    User testUser;
    LoginService loginService;
    Date issuedDate;
    Date expiresDate;

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
        this.encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());
        this.userRepository = mock(UserRepository.class);
        this.refreshTokenRepository = mock(RefreshTokenRepository.class);
        this.tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);
        this.testUser = new User(1L, testEmail, testEncodedPassword, roles, currentTime, null);
        this.loginService = new LoginServiceImpl(userRepository, passwordEncoder, refreshTokenRepository,
                tokenProvider);
    }

    @Test
    void loginSuccessTest() {
        // given
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        // when
        TokenResponse token = loginService.login(testEmail, testPassword);

        // then
        Assertions.assertInstanceOf(TokenResponse.class, token);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        Assertions.assertNotNull(token.accessToken());
        Assertions.assertNotNull(token.refreshToken());
    }

    @Test
    void loginFailTest_accountNotExist() { //계정 정보 없음
        Assertions.assertThrows(LoginException.class, () -> {
            loginService.login(testEmail, testPassword);
        });
    }

    @Test
    void loginFailTest_passwordNotMatch() { //패스워드 불일치
        // given
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));

        // then
        Assertions.assertThrows(LoginException.class, () -> {
            loginService.login(testEmail, "notValidPassword");
        });
    }

    @Test
    void refreshSuccessTest() {
        // given
        issuedDate = new Date();
        expiresDate = new Date(issuedDate.getTime() + refreshExp);
        String storedToken = setToken(new Date(0), expiresDate);
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findByAccountId(testEmail))
                .thenReturn(Optional.of(new RefreshToken(testEmail, storedToken, expiresDate)));

        // when
        TokenResponse rftokenResponse = loginService.refreshTokens(testEmail, roles);

        // then
        Assertions.assertNotEquals(storedToken, rftokenResponse.refreshToken());
    }

    private String setToken(Date issuedDate, Date expiresDate) {
        return Jwts.builder()
                .subject(testEmail)
                .claim("roles", roles)
                .issuedAt(issuedDate)
                .expiration(expiresDate)
                .signWith(testKey)
                .compact();
    }
}
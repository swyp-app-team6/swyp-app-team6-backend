package org.swyp.com.backend.global.auth.jwt.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.global.auth.jwt.CustomClaims;
import org.swyp.com.backend.global.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.auth.jwt.domain.RefreshToken;
import org.swyp.com.backend.global.auth.jwt.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.global.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.support.JwtTestFixture;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    TokenService tokenService;
    TokenProvider tokenProvider;
    Key testKey;

    @BeforeEach
    void setUp() {
        testKey = JwtTestFixture.buildKey();
        tokenProvider = JwtTestFixture.buildTokenProvider(testKey);
        tokenService = new TokenServiceImpl(tokenProvider, refreshTokenRepository);
    }

    @Test
    void issueTokenPair_issuance() {
        // given
        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID))
                .thenReturn(Optional.empty());

        // when
        TokenResponse actual = tokenService.issueTokenPair(
                JwtTestFixture.TEST_USER_ID, JwtTestFixture.ROLE);

        // then
        CustomClaims accessClaims = tokenProvider.validateToken(actual.accessToken());
        CustomClaims refreshClaims = tokenProvider.validateToken(actual.refreshToken());

        assertAll(
                () -> assertEquals(JwtTestFixture.TEST_USER_ID, accessClaims.getUserId()),
                () -> assertEquals(JwtTestFixture.TEST_USER_ID, refreshClaims.getUserId()),
                () -> verify(refreshTokenRepository).save(any(RefreshToken.class))
        );
    }

    @Test
    void issueTokenPair_jtiRenewal() {
        // given
        Date expiresDate = JwtTestFixture.getExpiresDate();
        String oldJti = UUID.randomUUID().toString();
        RefreshToken existing = new RefreshToken(JwtTestFixture.TEST_USER_ID, oldJti, expiresDate);
        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID))
                .thenReturn(Optional.of(existing));

        // when
        TokenResponse actual = tokenService.issueTokenPair(
                JwtTestFixture.TEST_USER_ID, JwtTestFixture.ROLE);

        // then
        CustomClaims newRefreshClaims = tokenProvider.validateToken(actual.refreshToken());

        assertAll(
                () -> assertNotEquals(oldJti, existing.getJti()),
                () -> assertEquals(newRefreshClaims.getJti(), existing.getJti()),
                () -> verify(refreshTokenRepository, never()).save(any(RefreshToken.class))
        );
    }

    @Test
    void reissueTokenPair_success() {
        // given
        Date expiresDate = JwtTestFixture.getExpiresDate();
        String jti = UUID.randomUUID().toString();
        String storedToken = JwtTestFixture.buildToken(testKey, jti, new Date(), expiresDate);

        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID))
                .thenReturn(Optional.of(new RefreshToken(JwtTestFixture.TEST_USER_ID, jti, expiresDate)));

        // when
        TokenResponse actual = tokenService.reissueTokenPair(storedToken);

        // then
        assertNotEquals(storedToken, actual.refreshToken());
    }

    @Test
    void reissueTokenPair_fail_notValidToken() {
        assertThrows(MalformedJwtException.class, () ->
                tokenService.reissueTokenPair("notValidToken"));
    }

    @Test
    void reissueTokenPair_fail_otherKey() {
        // given
        Key invalidKey = JwtTestFixture.buildKey();
        String token = JwtTestFixture.buildToken(invalidKey, UUID.randomUUID().toString(),
                new Date(), JwtTestFixture.getExpiresDate());

        // when & then
        assertThrows(SignatureException.class, () ->
                tokenService.reissueTokenPair(token));
    }

    @Test
    void reissueTokenPair_fail_jti_notMatched() {
        // given
        String jti = UUID.randomUUID().toString();
        String storedToken = JwtTestFixture.buildToken(testKey, jti, new Date(), JwtTestFixture.getExpiresDate());

        when(refreshTokenRepository.findByUserId(JwtTestFixture.TEST_USER_ID))
                .thenReturn(Optional.of(new RefreshToken(JwtTestFixture.TEST_USER_ID, "not_match_uuid",
                        JwtTestFixture.getExpiresDate())));

        // when & then
        assertThrows(BusinessException.class, () ->
                tokenService.reissueTokenPair(storedToken));
    }
}

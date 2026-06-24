package org.swyp.com.backend.auth;

import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swyp.com.backend.auth.jwt.CustomClaims;
import org.swyp.com.backend.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.enumeration.TokenType;
import org.swyp.com.backend.support.JwtTestFixture;

class TokenProviderTest {

    Key testKey;
    TokenProvider tokenProvider;

    @BeforeEach
    void set() {
        testKey = JwtTestFixture.buildKey();
        tokenProvider = JwtTestFixture.buildTokenProvider(testKey);
    }

    @Test
    void tokenGenerationSuccessTest() {
        // when
        CustomClaims claims = tokenProvider.generateToken(TokenType.ACCESS, TEST_USER_ID,
                TEST_ROLE);

        // then
        Assertions.assertInstanceOf(CustomClaims.class, claims);
        Assertions.assertEquals(TEST_USER_ID, claims.getUserId());
    }

    @Test
    void tokenValidationSuccessTest() {
        // given
        CustomClaims generated = tokenProvider.generateToken(TokenType.ACCESS, TEST_USER_ID,
                TEST_ROLE);

        // when
        CustomClaims verified = tokenProvider.validateToken(generated.getToken());

        // then
        Assertions.assertEquals(generated.getToken(), verified.getToken());
    }

    @Test
    void tokenValidationFailTest_expiredJwt() {
        // given
        String generatedToken = JwtTestFixture.buildToken(TEST_USER_ID, TEST_ROLE, testKey,
                UUID.randomUUID().toString(), new Date(0),
                new Date(0));

        // when & then
        Assertions.assertThrows(ExpiredJwtException.class, () -> {
            tokenProvider.validateToken(generatedToken);
        });
    }

    @Test
    void tokenValidationFailTest_invalidKey() {
        // given
        Key invalidKey = Jwts.SIG.HS256.key().build();
        Date issuedDate = new Date();
        Date expiresDate = new Date(issuedDate.getTime() + JwtTestFixture.REFRESH_EXP);

        String generatedToken = JwtTestFixture.buildToken(TEST_USER_ID, TEST_ROLE, invalidKey,
                UUID.randomUUID().toString(), issuedDate,
                expiresDate);

        // when & then
        Assertions.assertThrows(SignatureException.class, () -> {
            tokenProvider.validateToken(generatedToken);
        });
    }
}

package org.swyp.com.backend.global.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swyp.com.backend.global.auth.JwtTokenProvider.CustomClaims;

class TokenProviderTest {
    String testEmail;
    String[] roles;
    Key testKey;
    long accessExp;
    long refreshExp;

    @BeforeEach
    void set() {
        this.testEmail = "user@example.com";
        this.roles = new String[]{"USER"};
        this.testKey = Jwts.SIG.HS256.key().build();
        this.accessExp = 1000L * 60 * 30;
        this.refreshExp = 1000L * 60 * 60 * 24 * 14;
    }

    @Test
    void tokenGenerationSuccessTest() {
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);
        CustomClaims claims = tokenProvider.generateToken("ACCESS", testEmail, roles);

        Assertions.assertInstanceOf(CustomClaims.class, claims);
        Assertions.assertEquals(testEmail, claims.getAccountId());
    }

    @Test
    void tokenValidationSuccessTest() {
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);
        CustomClaims generated = tokenProvider.generateToken("ACCESS", testEmail, roles);

        CustomClaims verified = tokenProvider.validateToken(generated.getToken());

        Assertions.assertEquals(generated.getToken(), verified.getToken());
    }

    @Test
    void tokenValidationFailTest_expiredJwt() {
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);

        String generatedToken = Jwts.builder()
                .subject(testEmail)
                .claim("roles", roles)
                .issuedAt(new Date(0))
                .expiration(new Date(0))
                .signWith(testKey)
                .compact();

        Assertions.assertThrows(ExpiredJwtException.class, () -> {
            tokenProvider.validateToken(generatedToken);
        });
    }

    @Test
    void tokenValidationFailTest_invalidKey() {
        Key invalidKey = Jwts.SIG.HS256.key().build();
        String encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());

        TokenProvider tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);

        String generatedToken = Jwts.builder()
                .subject(testEmail)
                .claim("roles", roles)
                .issuedAt(new Date(0))
                .expiration(new Date(0))
                .signWith(invalidKey)
                .compact();

        Assertions.assertThrows(SignatureException.class, () -> {
            tokenProvider.validateToken(generatedToken);
        });
    }
}
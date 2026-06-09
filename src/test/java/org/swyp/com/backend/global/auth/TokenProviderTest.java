package org.swyp.com.backend.global.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.swyp.com.backend.global.auth.JwtTokenProvider.CustomClaims;
import org.swyp.com.backend.global.enumeration.UserRole;

class TokenProviderTest {
    String testEmail;
    List<UserRole> roles;
    Key testKey;
    long accessExp;
    long refreshExp;
    String encoded;
    TokenProvider tokenProvider;

    @BeforeEach
    void set() {
        this.testEmail = "user@example.com";
        this.roles = List.of(UserRole.USER);
        this.testKey = Jwts.SIG.HS256.key().build();
        this.accessExp = 1000L * 60 * 30;
        this.refreshExp = 1000L * 60 * 60 * 24 * 14;
        encoded = Base64.getEncoder().encodeToString(testKey.getEncoded());
        tokenProvider = new JwtTokenProvider(encoded, accessExp, refreshExp);
    }

    @Test
    void tokenGenerationSuccessTest() {
        CustomClaims claims = tokenProvider.generateToken("ACCESS", testEmail, roles);

        Assertions.assertInstanceOf(CustomClaims.class, claims);
        Assertions.assertEquals(testEmail, claims.getAccountId());
    }

    @Test
    void tokenValidationSuccessTest() {
        CustomClaims generated = tokenProvider.generateToken("ACCESS", testEmail, roles);
        CustomClaims verified = tokenProvider.validateToken(generated.getToken());

        Assertions.assertEquals(generated.getToken(), verified.getToken());
    }

    @Test
    void tokenValidationFailTest_expiredJwt() {
        String generatedToken = setToken(UUID.randomUUID().toString(), new Date(0), new Date(0));

        Assertions.assertThrows(ExpiredJwtException.class, () -> {
            tokenProvider.validateToken(generatedToken);
        });
    }

    @Test
    void tokenValidationFailTest_invalidKey() {
        Key invalidKey = Jwts.SIG.HS256.key().build();
        Date issuedDate = new Date();
        Date expiresDate = new Date(issuedDate.getTime() + refreshExp);

        String generatedToken = Jwts.builder()
                .subject(testEmail)
                .claim("roles", roles)
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedDate)
                .expiration(expiresDate)
                .signWith(invalidKey)
                .compact();

        Assertions.assertThrows(SignatureException.class, () -> {
            tokenProvider.validateToken(generatedToken);
        });
    }

    private String setToken(String jti, Date issuedDate, Date expiresDate) {
        return Jwts.builder()
                .subject(testEmail)
                .claim("roles", roles)
                .id(jti)
                .issuedAt(issuedDate)
                .expiration(expiresDate)
                .signWith(testKey)
                .compact();
    }
}
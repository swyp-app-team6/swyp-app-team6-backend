package org.swyp.com.backend.global.auth;

import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Base64;
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
        this.roles = new String[] {"USER"};
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
}
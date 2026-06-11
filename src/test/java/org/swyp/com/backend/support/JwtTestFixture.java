package org.swyp.com.backend.support;

import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.swyp.com.backend.global.auth.jwt.JwtTokenProvider;
import org.swyp.com.backend.global.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.enumeration.UserRole;

public final class JwtTestFixture {

    public static final Long TEST_USER_ID = 1L;
    public static final List<UserRole> ROLES = List.of(UserRole.USER);
    public static final long ACCESS_EXP = 1_800_000L;
    public static final long REFRESH_EXP = 1_209_600_000L;

    private JwtTestFixture() {
    }

    public static Key buildKey() {
        return Jwts.SIG.HS256.key().build();
    }

    public static TokenProvider buildTokenProvider(Key key) {
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        return new JwtTokenProvider(encoded, ACCESS_EXP, REFRESH_EXP);
    }

    public static String buildToken(Key key, String jti, Date issuedAt, Date expiresAt) {
        return Jwts.builder()
                .subject(TEST_USER_ID.toString())
                .claim("roles", ROLES)
                .id(jti)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(key)
                .compact();
    }
}

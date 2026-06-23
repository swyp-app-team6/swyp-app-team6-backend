package org.swyp.com.backend.support;

import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.swyp.com.backend.auth.jwt.JwtTokenProvider;
import org.swyp.com.backend.auth.jwt.TokenProvider;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.UserRole;

public final class JwtTestFixture {

    public static final Long TEST_USER_ID = 1L;
    public static final String TEST_USER_EMAIL = "test@example.com";
    public static final UserRole TEST_ROLE = UserRole.USER;

    public static final Long TEST_PROFILE_ID = 1L;
    public static final String TEST_PROFILE_NICKNAME = "TestNickName";
    public static final Gender TEST_GENDER = Gender.M;
    public static final String TEST_IMAGE_KEY = "test/image-key";
    public static final String TEST_BIO = "testBio";
    public static final String TEST_KEYWORD = "testKeyword";
    public static final String TEST_TOPIC = "testTopic";

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
                .claim("role", TEST_ROLE.name())
                .id(jti)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(key)
                .compact();
    }

    public static Date getExpiresDate() {
        Date issuedDate = new Date();
        return new Date(issuedDate.getTime() + JwtTestFixture.REFRESH_EXP);
    }
}

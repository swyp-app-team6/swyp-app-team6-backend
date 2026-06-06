package org.swyp.com.backend.global.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider{
    private final Key key;
    private final long ACCESS_EXP;
    private final long REFRESH_EXP;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.access-exp}") long accessExp, @Value("${jwt.refresh-exp}") long refreshExp) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.ACCESS_EXP = accessExp;
        this.REFRESH_EXP = refreshExp;
    }

    public CustomClaims generateToken(String type, String accountId, String[] role) {
        Date now = new Date();
        Date expiry = type.equals("ACCESS")? new Date(now.getTime()+ACCESS_EXP) : new Date(now.getTime()+REFRESH_EXP);

        String generatedToken = Jwts.builder()
                                    .subject(accountId)
                                    .claim("roles", role)
                                    .issuedAt(now)
                                    .expiration(expiry)
                                    .signWith(key)
                                    .compact();

        return new CustomClaims(accountId, generatedToken, now.toInstant(), expiry.toInstant());
    }

    @Override
    public CustomClaims validateToken(String token) {
        throw new UnsupportedOperationException();
    }

    @Getter
    @AllArgsConstructor
    public static class CustomClaims {
        private String accountId;
        private String token;
        private Instant issuedAt;
        private Instant expiresAt;
    }
}

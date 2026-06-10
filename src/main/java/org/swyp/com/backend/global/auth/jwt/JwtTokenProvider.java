package org.swyp.com.backend.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.swyp.com.backend.global.enumeration.UserRole;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final Key key;
    private final long ACCESS_EXP;
    private final long REFRESH_EXP;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.access-exp}") long accessExp,
                            @Value("${jwt.refresh-exp}") long refreshExp) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.ACCESS_EXP = accessExp;
        this.REFRESH_EXP = refreshExp;
    }

    public CustomClaims generateToken(String type, String accountId, List<UserRole> roles) {
        Date now = new Date();
        Date expiry =
                type.equals("ACCESS") ? new Date(now.getTime() + ACCESS_EXP) : new Date(now.getTime() + REFRESH_EXP);
        String jti = UUID.randomUUID().toString();

        String generatedToken = Jwts.builder()
                .subject(accountId)
                .claim("roles", roles)
                .id(jti)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();

        return new CustomClaims(accountId, generatedToken, roles, jti, now, expiry);
    }

    @Override
    public CustomClaims validateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<?> jwtRoles = claims.get("roles", List.class);
        List<UserRole> roles = jwtRoles.stream()
                .map(r -> UserRole.valueOf(r.toString()))
                .toList();

        return new CustomClaims(claims.getSubject(), token, roles, claims.getId(), claims.getIssuedAt(),
                claims.getExpiration());

    }
}

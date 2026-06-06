package org.swyp.com.backend.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
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
    /**
     * JWT 토큰을 생성합니다.
     *
     * <p>전달받은 토큰 타입에 따라 Access Token 또는 Refresh Token을 생성하며,
     * 계정 ID와 권한 정보를 JWT Claim에 포함합니다.</p>
     *
     * <ul>
     *     <li>subject : 계정 ID</li>
     *     <li>roles : 사용자 권한 목록</li>
     *     <li>issuedAt : 토큰 발급 시간</li>
     *     <li>expiration : 토큰 만료 시간</li>
     * </ul>
     *
     * @param type 생성할 토큰 타입 ("ACCESS" 또는 "REFRESH")
     * @param accountId 토큰의 주체(사용자 계정 ID)
     * @param roles 사용자 권한 목록
     * @return 생성된 JWT 문자열과 클레임 정보를 포함하는 {@code CustomClaims} 객체
     * @throws io.jsonwebtoken.security.SecurityException JWT 서명 과정에서 보안 오류가 발생한 경우
     * @throws io.jsonwebtoken.JwtException JWT 생성 과정에서 오류가 발생한 경우
     */
    public CustomClaims generateToken(String type, String accountId, String[] roles) {
        Date now = new Date();
        Date expiry = type.equals("ACCESS")? new Date(now.getTime()+ACCESS_EXP) : new Date(now.getTime()+REFRESH_EXP);

        String generatedToken = Jwts.builder()
                                    .subject(accountId)
                                    .claim("roles", roles)
                                    .issuedAt(now)
                                    .expiration(expiry)
                                    .signWith(key)
                                    .compact();

        return new CustomClaims(accountId, generatedToken, roles, now, expiry);
    }
    /**
     * JWT 토큰을 검증하고 토큰에 포함된 클레임 정보를 반환합니다.
     *
     * <p>토큰의 서명을 검증하고 Payload에 저장된 사용자 식별 정보와 권한 정보를
     * 추출하여 {@link CustomClaims} 객체로 변환합니다.</p>
     *
     * <p>토큰이 유효하지 않거나 만료된 경우, JWT 라이브러리에서 제공하는 예외가
     * 호출자에게 전파됩니다.</p>
     *
     * @param token 검증할 JWT 문자열
     * @return 토큰에 저장된 사용자 정보, 권한 정보, 발급 시간 및 만료 시간을 포함한
     *         {@code CustomClaims} 객체
     *
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰이 만료된 경우
     * @throws io.jsonwebtoken.security.SignatureException 토큰 서명 검증에 실패한 경우
     * @throws io.jsonwebtoken.MalformedJwtException 토큰 형식이 올바르지 않은 경우
     * @throws io.jsonwebtoken.UnsupportedJwtException 지원되지 않는 JWT 형식인 경우
     * @throws io.jsonwebtoken.JwtException 기타 JWT 처리 과정에서 오류가 발생한 경우
     */
    @Override
    public CustomClaims validateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> roles = (List) claims.get("roles", List.class);

        return new CustomClaims(claims.getSubject(), token, roles.toArray(new String[0]), claims.getIssuedAt(),
                claims.getExpiration());

    }

    @Getter
    @AllArgsConstructor
    public static class CustomClaims {
        private String accountId;
        private String token;
        private String[] roles;
        private Date issuedAt;
        private Date expiresAt;
    }
}

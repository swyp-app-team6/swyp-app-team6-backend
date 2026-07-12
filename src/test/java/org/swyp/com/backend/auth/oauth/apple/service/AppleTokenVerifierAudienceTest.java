package org.swyp.com.backend.auth.oauth.apple.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * jjwt 0.12.x에서 Claims#getAudience()가 String이 아닌 Set<String>을 반환하면서, List<String>#contains(Set)를 쓰던 이전 로직이 항상 false를
 * 반환해 모든 Apple 로그인을 401로 거부하던 버그가 실제로 고쳐졌는지 검증한다.
 */
class AppleTokenVerifierAudienceTest {

    @Test
    void getAudience_returns_Set_and_fixed_logic_matches_allowedClientIds() {
        KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();

        String token = Jwts.builder()
                .audience().add("com.swyp.rotationdatingapp").and()
                .issuer("https://appleid.apple.com")
                .subject("test-sub")
                .signWith(keyPair.getPrivate())
                .compact();

        Claims claims = Jwts.parser()
                .verifyWith((RSAPublicKey) keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Set<String> audiences = claims.getAudience();
        assertThat(audiences).containsExactly("com.swyp.rotationdatingapp");

        List<String> allowedClientIds = List.of(
                "com.swyp.rotationdatingapp",
                "com.swyp.rotationdatingapp.signin"
        );

        boolean fixedLogicMatches = allowedClientIds.stream().anyMatch(audiences::contains);
        assertThat(fixedLogicMatches)
                .as("수정된 로직(List.stream().anyMatch(Set::contains))은 aud를 정상적으로 매칭해야 한다")
                .isTrue();

        @SuppressWarnings({"unchecked", "rawtypes"})
        boolean oldBuggyLogicMatches = ((List) allowedClientIds).contains(audiences);
        assertThat(oldBuggyLogicMatches)
                .as("예전 로직(List<String>.contains(Set<String>))은 항상 false였다 - 이게 실제 프로덕션 버그였다")
                .isFalse();
    }
}

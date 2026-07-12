package org.swyp.com.backend.auth.oauth.apple.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.swyp.com.backend.auth.oauth.apple.config.AppleProperties;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.global.exception.ExternalApiConnectionException;

@Component
@RequiredArgsConstructor
public class AppleTokenVerifier {

    private final AppleProperties appleProperties;

    private static final RestClient restClient = RestClient.builder()
            .baseUrl("https://appleid.apple.com")
            .build();

    public Claims verify(String identityToken) {
        try {
            JWKSet jwkSet = fetchApplePublicKeys();
            SignedJWT signedJWT = SignedJWT.parse(identityToken);

            String kid = signedJWT.getHeader().getKeyID();
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED, "Apple 공개키를 찾을 수 없습니다.");
            }

            // Apple id_token은 RS256(RSA) 서명
            RSAPublicKey publicKey = ((RSAKey) jwk).toRSAPublicKey();

            if (!signedJWT.verify(new RSASSAVerifier(publicKey))) {
                throw new BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple identityToken입니다.");
            }

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer("https://appleid.apple.com")
                    .build()
                    .parseSignedClaims(identityToken)
                    .getPayload();

            if (!appleProperties.getAllowedClientIds()
                    .contains(claims.getAudience())) {

                throw new BusinessException(
                        HttpStatus.UNAUTHORIZED,
                        "유효하지 않은 Apple identityToken입니다."
                );
            }

            return claims;

        } catch (BusinessException e) {
            throw e;
        } catch (ParseException | JOSEException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple identityToken입니다.");
        } catch (io.jsonwebtoken.JwtException e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple identityToken입니다.: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiConnectionException(
                    "Apple 공개키 서버와의 연결에 실패했습니다. (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")",
                    "APPLE");
        }
    }

    private JWKSet fetchApplePublicKeys() {
        try {
            String response = restClient.get()
                    .uri("/auth/keys")
                    .retrieve()
                    .body(String.class);
            return JWKSet.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException("Apple JWK 파싱 실패", e);
        }
    }
}

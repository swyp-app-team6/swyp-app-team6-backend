package org.swyp.com.backend.auth.oauth.apple.service;

import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.swyp.com.backend.auth.oauth.apple.config.AppleProperties;

@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final AppleProperties appleProperties;

    public String generate() {
        try {
            ECPrivateKey privateKey = parsePrivateKey(appleProperties.getPrivateKey());
            Date now = new Date();
            Date expiration = new Date(now.getTime() + Duration.ofMinutes(10).toMillis());

            return Jwts.builder()
                    .header().add("kid", appleProperties.getKeyId()).and()
                    .issuer(appleProperties.getTeamId())
                    .issuedAt(now)
                    .expiration(expiration)
                    .audience().add("https://appleid.apple.com").and()
                    .subject(appleProperties.getClientId())
                    .signWith(privateKey, Jwts.SIG.ES256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Apple client_secret 생성 실패", e);
        }
    }

    private ECPrivateKey parsePrivateKey(String pem) throws Exception {
        String cleaned = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(keySpec);
    }
}

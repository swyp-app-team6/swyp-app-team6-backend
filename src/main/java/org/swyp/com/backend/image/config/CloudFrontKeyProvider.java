package org.swyp.com.backend.image.config;

import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * private key 조달 방식(현재는 env var 문자열)을 이 클래스 안에 격리해두어,
 * 추후 Secrets Manager로 교체할 때 이 클래스만 바꾸면 되도록 한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CloudFrontKeyProvider {

    private final CloudFrontProperties cloudFrontProperties;

    private PrivateKey privateKey;

    @PostConstruct
    void init() {
        try {
            privateKey = loadPrivateKey();
            if (privateKey == null) {
                log.warn("CloudFront private key가 설정되지 않았습니다. 이미지 서명 URL 발급이 필요한 시점에 실패합니다.");
            }
        } catch (Exception e) {
            log.warn("CloudFront private key 로드에 실패했습니다: {}", e.getMessage());
        }
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            throw new IllegalStateException("CloudFront private key가 설정되지 않았습니다.");
        }
        return privateKey;
    }

    private PrivateKey loadPrivateKey() throws Exception {
        String pem = cloudFrontProperties.getPrivateKey();
        if (!StringUtils.hasText(pem)) {
            return null;
        }

        String cleaned = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}

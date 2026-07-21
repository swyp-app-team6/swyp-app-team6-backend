package org.swyp.com.backend.image.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloudfront")
@Getter
@Setter
public class CloudFrontProperties {

    private String domain;
    private String keyPairId;
    private String privateKey;
    private int ttlSeconds = 86400;
}

package org.swyp.com.backend.auth.oauth.apple.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple")
@Getter
@Setter
public class AppleProperties {
    private String teamId;
    private String keyId;
    private String clientId;
    private String redirectUri;
    private String privateKey;

    public void setClientId(String clientId) {
        this.clientId = clientId.replace(".signin", "");
    }
}

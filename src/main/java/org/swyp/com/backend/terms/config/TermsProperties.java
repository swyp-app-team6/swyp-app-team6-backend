package org.swyp.com.backend.terms.config;

import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.swyp.com.backend.global.enumeration.TermsType;

@Component
@ConfigurationProperties(prefix = "terms")
@Getter
@Setter
public class TermsProperties {

    private Map<TermsType, String> contentUrl = new EnumMap<>(TermsType.class);

    public String getContentUrl(TermsType termsType) {
        return contentUrl.get(termsType);
    }
}

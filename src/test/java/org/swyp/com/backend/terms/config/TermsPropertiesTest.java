package org.swyp.com.backend.terms.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.swyp.com.backend.global.enumeration.TermsType;

class TermsPropertiesTest {

    @Test
    void bindsContentUrlByTermsType_fromRelaxedPropertyNames() {
        // given
        Map<String, Object> source = new HashMap<>();
        source.put("terms.content-url.service", "https://example.com/service-terms");
        source.put("terms.content-url.privacy", "https://example.com/privacy-policy");

        Binder binder = new Binder(new MapConfigurationPropertySource(source));

        // when
        TermsProperties properties = binder.bind("terms", Bindable.of(TermsProperties.class)).get();

        // then
        assertThat(properties.getContentUrl(TermsType.SERVICE)).isEqualTo("https://example.com/service-terms");
        assertThat(properties.getContentUrl(TermsType.PRIVACY)).isEqualTo("https://example.com/privacy-policy");
        assertThat(properties.getContentUrl(TermsType.AGE_OVER_14)).isNull();
    }
}

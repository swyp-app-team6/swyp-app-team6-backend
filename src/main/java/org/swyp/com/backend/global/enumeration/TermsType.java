package org.swyp.com.backend.global.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TermsType {
    SERVICE(true, 1, "서비스 이용약관", "https://www.notion.so/service-terms"),
    PRIVACY(true, 1, "개인정보 처리방침", "https://www.notion.so/privacy-policy"),
    AGE_OVER_14(true, 1, "만 14세 이상입니다", null);

    private final boolean required;
    private final int currentVersion;
    private final String label;
    private final String contentUrl;
}

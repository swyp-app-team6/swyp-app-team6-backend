package org.swyp.com.backend.auth.oauth.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "SSO 로그인 응답")
public record SsoLoginResponse(
        @JsonProperty("access_token")
        @Schema(description = "API 인증에 사용되는 access token")
        String accessToken,
        @JsonProperty("refresh_token")
        @Schema(description = "access token 재발급에 사용되는 refresh token")
        String refreshToken,
        @JsonProperty("requires_terms_agreement")
        @Schema(description = "필수 약관에 아직 동의하지 않은 사용자인지 여부")
        boolean requiresTermsAgreement
) {
}

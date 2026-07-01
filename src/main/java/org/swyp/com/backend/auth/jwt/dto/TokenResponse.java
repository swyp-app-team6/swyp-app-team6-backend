package org.swyp.com.backend.auth.jwt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발급된 액세스/리프레시 토큰 정보")
public record TokenResponse(
        @JsonProperty("access_token")
        @Schema(description = "API 인증에 사용되는 access token")
        String accessToken,
        @JsonProperty("refresh_token")
        @Schema(description = "access token 재발급에 사용되는 refresh token")
        String refreshToken) {

}

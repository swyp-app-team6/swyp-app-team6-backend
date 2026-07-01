package org.swyp.com.backend.auth.jwt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "액세스 토큰 재발급 요청")
public record RefreshTokenRequest(
        @JsonProperty("refresh_token")
        @NotBlank(message = "refresh token 값을 입력해주세요.")
        @Schema(description = "로그인 시 발급받은 refresh token")
        String refreshToken) {

}

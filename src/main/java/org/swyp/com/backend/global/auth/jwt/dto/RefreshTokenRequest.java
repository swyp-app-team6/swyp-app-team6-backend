package org.swyp.com.backend.global.auth.jwt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @JsonProperty("refresh_token")
        @NotBlank(message = "refresh token 값을 입력해주세요.")
        String refreshToken) {

}

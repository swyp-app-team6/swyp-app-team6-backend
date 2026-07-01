package org.swyp.com.backend.auth.oauth.google.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Google 로그인 요청")
public record GoogleLoginRequest(
        @NotBlank(message = "idToken은 필수입니다.")
        @Schema(description = "Google SDK에서 발급받은 idToken")
        String idToken
) {}

package org.swyp.com.backend.auth.oauth.apple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
        @NotBlank
        @Schema(description = "iOS/Android Apple SDK에서 발급받은 identityToken")
        String identityToken
) {}

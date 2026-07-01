package org.swyp.com.backend.auth.oauth.apple.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
        @NotBlank
        @Schema(description = "iOS/Android Apple SDK에서 발급받은 identityToken")
        String identityToken,
        @Schema(description = "iOS/Android Apple SDK에서 identityToken과 함께 발급받은 authorizationCode. "
                + "회원탈퇴 시 Apple 계정 연결 해제(revoke)에 필요하므로 가능하면 함께 전달한다.")
        String authorizationCode
) {}

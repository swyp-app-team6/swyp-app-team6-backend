package org.swyp.com.backend.global.auth.oauth.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAppLoginRequest(
        @NotBlank(message = "idToken은 필수입니다.")
        String idToken
) {

}

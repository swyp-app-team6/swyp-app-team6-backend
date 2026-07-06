package org.swyp.com.backend.auth.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "일반 로그인 요청")
public record LocalLoginRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        @Schema(description = "가입 시 등록한 아이디", example = "myid123")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "비밀번호", example = "password1234")
        String password
) {
}

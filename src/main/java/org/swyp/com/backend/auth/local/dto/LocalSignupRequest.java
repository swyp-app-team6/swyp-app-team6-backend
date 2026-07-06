package org.swyp.com.backend.auth.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "일반 회원가입 요청")
public record LocalSignupRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        @Schema(description = "로그인에 사용할 아이디 (이메일 형식이 아니어도 됩니다)", example = "myid123")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
        @Schema(description = "비밀번호 (8~64자)", example = "password1234")
        String password
) {
}

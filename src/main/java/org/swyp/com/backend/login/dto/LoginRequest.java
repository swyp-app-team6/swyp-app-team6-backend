package org.swyp.com.backend.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, max = 14, message = "비밀번호는 8자 이상 14자 이하로 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[*_!@#$%^&+=]).+$",
                message = "비밀번호는 최소 하나의 대문자, 숫자, 특수문자(*_!@#$%^&+=)를 포함해야 합니다."
        )
        String password
) {

}

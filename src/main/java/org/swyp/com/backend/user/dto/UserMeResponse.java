package org.swyp.com.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.user.domain.User;

@Schema(description = "현재 로그인된 사용자 정보 응답")
public record UserMeResponse(
        @Schema(description = "사용자 ID")
        Long id,
        @Schema(description = "이메일")
        String email,
        @Schema(description = "권한")
        UserRole role,
        @Schema(description = "소셜 로그인 제공자")
        OAuthProvider provider) {

    public static UserMeResponse from(User user) {
        return new UserMeResponse(user.getId(), user.getEmail(), user.getRole(), user.getProvider());
    }
}

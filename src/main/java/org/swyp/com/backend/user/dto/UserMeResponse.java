package org.swyp.com.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        OAuthProvider provider,
        @JsonProperty("profile_registered")
        @Schema(description = "프로필을 1번이라도 등록한 적 있는지 여부")
        Boolean profileRegistered,
        @JsonProperty("profile_exchanged")
        @Schema(description = "프로필을 1번이라도 교환한 적 있는지 여부")
        Boolean profileExchanged,
        @JsonProperty("review_registered")
        @Schema(description = "교환한 프로필에 후기를 1번이라도 등록한 적 있는지 여부")
        Boolean reviewRegistered) {

    public static UserMeResponse from(User user) {
        return new UserMeResponse(user.getId(), user.getEmail(), user.getRole(), user.getProvider(),
                user.getProfileRegistered(), user.getProfileExchanged(), user.getReviewRegistered());
    }
}

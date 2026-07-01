package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;

@Schema(description = "프로필 정보 응답")
public record ProfileResponse(
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "성별")
        Gender gender,
        @JsonProperty("image_key")
        @Schema(description = "프로필 이미지 키, URL 조합 목적으로 사용")
        String imageKey,
        @Schema(description = "자기소개")
        String bio,
        @Schema(description = "프로필 키워드")
        String keyword,
        @Schema(description = "프로필 토픽")
        String topic,
        @Schema(description = "관심사 목록")
        List<InterestType> interests
) {

}

package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.Region;

@Schema(description = "내 프로필 상세 응답")
public record MyProfileResponse(
        @Schema(description = "프로필 ID")
        Long id,
        @Schema(description = "닉네임")
        String nickname,
        @JsonProperty("image_key")
        @Schema(description = "프로필 이미지 키")
        String imageKey,
        @Schema(description = "성별")
        Gender gender,
        @Schema(description = "나이")
        Integer age,
        @Schema(description = "거주지")
        Region region,
        @Schema(description = "직업")
        String job,
        @Schema(description = "관심사 목록")
        List<InterestType> interests,
        @Schema(description = "자기소개")
        String bio,
        @JsonProperty("cosmic_type")
        @Schema(description = "Cosmic 테스트 결과로 산출된 코스믹 유형")
        CosmicDatingType cosmicType,
        @JsonProperty("cosmic_type_image_key")
        String cosmicTypeImageKey,
        @JsonProperty("cosmic_type_detail")
        String cosmicTypeDetail,
        @JsonProperty("choice_template")
        @Schema(description = "객관식 질문 템플릿 응답 목록")
        List<ChoiceTemplate> choiceTemplate,
        @JsonProperty("short_template")
        @Schema(description = "주관식 질문 템플릿 응답 목록")
        List<ShortTemplate> shortTemplate
) {
}

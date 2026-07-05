package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.RegionDetail;

@Schema(description = "프로필 등록 요청")
public record ProfileRegisterRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 3, max = 10, message = "닉네임은 3~10자여야 합니다.")
        @Schema(description = "닉네임 (3~10자)")
        String nickname,
        @JsonProperty("image_key")
        @NotBlank(message = "프로필 사진을 등록해주세요.")
        @Schema(description = "Presigned URL 발급 시 받은 프로필 이미지 키")
        String imageKey,
        @NotNull(message = "성별을 입력해주세요.")
        @Schema(description = "성별")
        Gender gender,
        @NotNull(message = "나이를 입력해주세요.")
        @Schema(description = "나이")
        Integer age,
        @NotNull(message = "거주지를 입력해주세요.")
        @Schema(description = "거주지")
        RegionDetail region,
        @NotNull(message = "직업을 입력해주세요.")
        @Size(max = 10, message = "직무분야는 최대 10자여야 합니다.")
        @Schema(description = "직업")
        String job,
        @NotEmpty(message = "관심사를 입력해주세요.")
        @Size(min = 3, max = 5, message = "관심사는 3~5개까지 선택 가능합니다.")
        @Schema(description = "관심사 목록 (3~5개)")
        List<InterestType> interests,
        @Size(max = 100, message = "자기소개는 최대 100자까지 입력 가능합니다.")
        @Schema(description = "자기소개 (최대 100자)")
        String bio,
        @JsonProperty("cosmic_type")
        @Schema(description = "Cosmic 테스트 결과로 산출된 코스믹 유형")
        CosmicDatingType cosmicType,
        @JsonProperty("choice_template")
        @Schema(description = "객관식 질문 템플릿 응답 목록")
        List<ChoiceTemplate> choiceTemplate,
        @JsonProperty("short_template")
        @Schema(description = "주관식 질문 템플릿 응답 목록")
        List<ShortTemplate> shortTemplate
) {
}

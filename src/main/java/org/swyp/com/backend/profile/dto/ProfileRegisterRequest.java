package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.Region;
import org.swyp.com.backend.question.dto.ChoiceTemplate;
import org.swyp.com.backend.question.dto.ShortTemplate;

public record ProfileRegisterRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 3, max = 10, message = "닉네임은 3~10자여야 합니다.")
        String nickname,
        @JsonProperty("image_key")
        @NotBlank(message = "프로필 사진을 등록해주세요.")
        String imageKey,
        @NotNull(message = "성별을 입력해주세요.")
        Gender gender,
        @NotNull(message = "나이를 입력해주세요.")
        Integer age,
        @NotNull(message = "거주지를 입력해주세요.")
        Region region,
        @NotNull(message = "직업을 입력해주세요.")
        String job,
        @NotEmpty(message = "관심사를 입력해주세요.")
        @Size(min = 3, max = 5, message = "관심사는 3~5개까지 선택 가능합니다.")
        List<InterestType> interests,
        @Size(max = 20, message = "자기소개는 최대 20자까지 입력 가능합니다.")
        String bio,
        @JsonProperty("cosmic_type")
        CosmicDatingType cosmicType,
        @JsonProperty("choice_template")
        List<ChoiceTemplate> choiceTemplate,
        @JsonProperty("short_template")
        List<ShortTemplate> shortTemplate
) {
}

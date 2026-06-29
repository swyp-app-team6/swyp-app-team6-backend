package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.Region;

public record ProfileUpdateRequest(
        @Size(min = 3, max = 10, message = "닉네임은 3~10자여야 합니다.")
        String nickname,
        @JsonProperty("image_key")
        String imageKey,
        Integer age,
        Region region,
        String job,
        @Size(min = 3, max = 5, message = "관심사는 3~5개까지 선택 가능합니다.")
        List<InterestType> interests,
        @Size(max = 50, message = "자기소개는 최대 50자까지 입력 가능합니다.")
        String bio,
        @JsonProperty("cosmic_type")
        CosmicDatingType cosmicType,
        @JsonProperty("choice_template")
        List<ChoiceTemplate> choiceTemplate,
        @JsonProperty("short_template")
        List<ShortTemplate> shortTemplate
) {
}

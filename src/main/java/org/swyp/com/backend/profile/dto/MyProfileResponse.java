package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.Region;
import org.swyp.com.backend.question.dto.ChoiceTemplate;
import org.swyp.com.backend.question.dto.ShortTemplate;

public record MyProfileResponse(
        Long id,
        String nickname,
        @JsonProperty("image_key")
        String imageKey,
        Gender gender,
        Integer age,
        Region region,
        String job,
        List<InterestType> interests,
        String bio,
        @JsonProperty("cosmic_type")
        CosmicDatingType cosmicType,
        @JsonProperty("choice_template")
        List<ChoiceTemplate> choiceTemplate,
        @JsonProperty("short_template")
        List<ShortTemplate> shortTemplate
) {
}

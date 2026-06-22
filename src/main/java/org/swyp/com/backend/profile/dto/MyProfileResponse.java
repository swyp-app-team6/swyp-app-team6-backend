package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;

public record MyProfileResponse(
        Long id,
        String nickname,
        @JsonProperty("image_url")
        String imageUrl,
        Gender gender,
        String bio,
        String keyword,
        String topic,
        List<InterestType> interests
) {
}

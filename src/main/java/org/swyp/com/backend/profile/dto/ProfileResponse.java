package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;

public record ProfileResponse(
        String nickname,
        Gender gender,
        @JsonProperty("image_key")
        String imageKey,
        String bio,
        String keyword,
        String topic,
        List<InterestType> interests
) {
}

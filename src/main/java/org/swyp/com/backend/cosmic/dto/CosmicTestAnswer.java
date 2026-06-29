package org.swyp.com.backend.cosmic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

public record CosmicTestAnswer(
        @JsonProperty("answer_id")
        Integer answerId,
        String answer,
        @JsonProperty("cosmic_type")
        CosmicDatingType cosmicType,
        Integer score
) {
}

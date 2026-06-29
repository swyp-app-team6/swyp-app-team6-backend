package org.swyp.com.backend.cosmic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

public record CosmicTestAnswer(
        @JsonProperty("answer_id")
        Integer answerId,
        String answer,
        CosmicDatingType cosmic,
        Integer score
) {
}

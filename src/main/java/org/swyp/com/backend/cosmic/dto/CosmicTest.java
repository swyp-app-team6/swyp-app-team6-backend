package org.swyp.com.backend.cosmic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CosmicTest(
        @JsonProperty("question_id")
        Integer questionId,
        String question,
        List<CosmicTestAnswer> answers
) {
}

package org.swyp.com.backend.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MultipleAnswer(
        @JsonProperty("answer_id")
        Integer answerId,
        String content
) {
}

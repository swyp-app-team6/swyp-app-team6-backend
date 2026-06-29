package org.swyp.com.backend.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

public record ShortTemplate(
        @JsonProperty("question_id")
        Long questionId,
        @JsonProperty("question_type")
        CustomQuestionType questionType,
        String question,
        String answer
) {
}

package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

public record ChoiceTemplate(
        @JsonProperty("question_id")
        @NotNull(message = "question_id_null")
        Long questionId,
        @JsonProperty("question_type")
        CustomQuestionType questionType,
        String question,
        @JsonProperty("answer_id")
        @NotNull(message = "answer_id_null")
        Integer answerId,
        String answer
) {
}

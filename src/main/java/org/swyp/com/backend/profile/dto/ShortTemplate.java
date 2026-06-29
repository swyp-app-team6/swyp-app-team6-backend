package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

public record ShortTemplate(
        @JsonProperty("question_id")
        @NotNull(message = "question_id_null")
        Long questionId,
        @JsonProperty("question_type")
        CustomQuestionType questionType,
        String question,
        @NotBlank(message = "question_id_null")
        @Size(min = 1, max = 20, message = "입력값은 1~20자여야 합니다.")
        String answer
) {
}

package org.swyp.com.backend.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record CustomQuestionResponse(
        @JsonProperty("multiple_questions")
        List<MultipleQuestion> multipleQuestions,
        @JsonProperty("short_questions")
        List<ShortQuestion> shortQuestions
) {
}

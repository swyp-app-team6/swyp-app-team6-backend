package org.swyp.com.backend.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "프로필 작성용 질문 템플릿 목록 응답")
public record CustomQuestionResponse(
        @JsonProperty("multiple_questions")
        @Schema(description = "객관식 질문 목록")
        List<MultipleQuestion> multipleQuestions,
        @JsonProperty("short_questions")
        @Schema(description = "주관식 질문 목록")
        List<ShortQuestion> shortQuestions
) {
}

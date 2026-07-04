package org.swyp.com.backend.profile.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

@Schema(description = "객관식 질문 템플릿 응답")
public record ChoiceTemplate(
        @JsonProperty("question_id")
        @NotNull(message = "question_id_null")
        @Schema(description = "질문 ID (응답시 포함)")
        Long questionId,
        @JsonProperty("question_type")
        @Schema(description = "질문 유형")
        CustomQuestionType questionType,
        @Schema(description = "질문 내용")
        String question,
        @JsonProperty("answer_id")
        @NotNull(message = "answer_id_null")
        @Schema(description = "선택한 답변 ID (응답시 포함)")
        Integer answerId,
        @Schema(description = "답변 내용")
        String answer
) {
}

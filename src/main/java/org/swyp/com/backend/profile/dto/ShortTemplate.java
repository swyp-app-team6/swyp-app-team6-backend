package org.swyp.com.backend.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

@Schema(description = "주관식 질문 템플릿 응답")
public record ShortTemplate(
        @JsonProperty("question_id")
        @NotNull(message = "question_id_null")
        @Schema(description = "질문 ID")
        Long questionId,
        @JsonProperty("question_type")
        @Schema(description = "질문 유형 (응답 시에만 포함)")
        CustomQuestionType questionType,
        @Schema(description = "질문 내용 (응답 시에만 포함)")
        String question,
        @NotBlank(message = "question_id_null")
        @Size(min = 1, max = 20, message = "입력값은 1~20자여야 합니다.")
        @Schema(description = "입력한 답변 (1~20자)")
        String answer
) {
}

package org.swyp.com.backend.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

@Schema(description = "주관식 질문")
public record ShortQuestion(
        @Schema(description = "질문 ID")
        Long id,
        @Schema(description = "질문 유형")
        CustomQuestionType type,
        @Schema(description = "질문 내용")
        String content
) {
}

package org.swyp.com.backend.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;

@Schema(description = "객관식 질문")
public record MultipleQuestion(
        @Schema(description = "질문 ID")
        Long id,
        @Schema(description = "질문 유형")
        CustomQuestionType type,
        @Schema(description = "질문 내용")
        String content,
        @Schema(description = "선택지 목록")
        List<MultipleAnswer> answers
) {
}

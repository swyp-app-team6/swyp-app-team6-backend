package org.swyp.com.backend.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "객관식 질문 선택지")
public record MultipleAnswer(
        @JsonProperty("answer_id")
        @Schema(description = "선택지 ID")
        Integer answerId,
        @Schema(description = "선택지 내용")
        String content
) {
}

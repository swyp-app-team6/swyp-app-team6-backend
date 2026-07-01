package org.swyp.com.backend.cosmic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Cosmic 테스트 질문")
public record CosmicTest(
        @JsonProperty("question_id")
        @Schema(description = "질문 ID")
        Integer questionId,
        @Schema(description = "질문 내용")
        String question,
        @Schema(description = "선택지 목록")
        List<CosmicTestAnswer> answers
) {
}

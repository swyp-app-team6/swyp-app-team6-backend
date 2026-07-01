package org.swyp.com.backend.cosmic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Cosmic 테스트 질문 목록 응답")
public record CosmicTestResponse(
        @Schema(description = "Cosmic 테스트 질문 목록")
        List<CosmicTest> questions
) {
}

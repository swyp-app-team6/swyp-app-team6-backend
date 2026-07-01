package org.swyp.com.backend.cosmic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Schema(description = "Cosmic 테스트 선택지")
public record CosmicTestAnswer(
        @JsonProperty("answer_id")
        @Schema(description = "선택지 ID")
        Integer answerId,
        @Schema(description = "선택지 내용")
        String answer,
        @JsonProperty("cosmic_type")
        @Schema(description = "선택 시 가중치가 부여되는 코스믹 유형")
        CosmicDatingType cosmicType,
        @Schema(description = "선택 시 해당 코스믹 유형에 부여되는 점수")
        Integer score
) {
}

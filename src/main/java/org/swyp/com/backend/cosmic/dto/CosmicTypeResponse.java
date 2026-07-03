package org.swyp.com.backend.cosmic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Cosmic 타입 상세 응답")
public record CosmicTypeResponse(
        @Schema(description = "Cosmic 타입 기본 정보")
        @JsonProperty("cosmic_type")
        CosmicTypeLabel cosmicType,
        @Schema(description = "타입 상세 설명", example = "사랑을 아낌없이 표현하는 열정적인 연애")
        String detail,
        @Schema(description = "이미지 키", example = "cosmic/sola.png")
        @JsonProperty("image_key")
        String imageKey,
        @Schema(description = "타입 특징 리스트", example = "[\"감정을 솔직하게 표현해요.\"]")
        List<String> features,
        @Schema(description = "궁합이 좋은 타입 목록")
        List<CosmicTypeLabel> matches,
        @Schema(description = "유저 언급 문장 리스트")
        List<String> mentions,
        @Schema(description = "태그 리스트")
        List<String> tags
) {
}

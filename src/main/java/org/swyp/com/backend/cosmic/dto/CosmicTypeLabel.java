package org.swyp.com.backend.cosmic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Schema(description = "Cosmic 타입 라벨 정보")
public record CosmicTypeLabel(
        @Schema(description = "코스믹 타입 유형", example = "SOLA")
        CosmicDatingType type,
        @Schema(description = "타입 라벨", example = "솔라 형")
        String label
) {
}

package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Schema(description = "교환 당시 매칭에 사용한 내 프로필 요약")
public record ExchangeMyProfileSummary(
        @Schema(description = "내 닉네임")
        String nickname,
        @JsonProperty("cosmic_type")
        @Schema(description = "내 코스믹 유형")
        CosmicDatingType cosmicType,
        @JsonProperty("cosmic_type_image_key")
        String cosmicTypeImageKey
) {
}

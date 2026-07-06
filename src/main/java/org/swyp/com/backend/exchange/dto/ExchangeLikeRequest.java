package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "보관함 항목 좋아요 표시 요청")
public record ExchangeLikeRequest(
        @JsonProperty("liked")
        @NotNull(message = "liked 값을 입력해주세요.")
        @Schema(description = "좋아요 표시 여부", example = "true")
        Boolean liked
) {
}

package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보관함 항목 좋아요 표시 결과")
public record ExchangeLikeResponse(
        @JsonProperty("exchange_id")
        @Schema(description = "보관함 항목 ID")
        Long exchangeId,
        @JsonProperty("is_liked")
        @Schema(description = "변경된 좋아요 표시 여부")
        Boolean isLiked
) {
}

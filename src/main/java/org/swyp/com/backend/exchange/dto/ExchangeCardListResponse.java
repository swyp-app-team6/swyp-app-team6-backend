package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "교환한 프로필 목록(커서 페이징)")
public record ExchangeCardListResponse(
        @Schema(description = "교환한 프로필 카드 목록")
        List<ExchangeCardResponse> exchanges,
        @JsonProperty("total_count")
        @Schema(description = "조건에 맞는 전체 개수")
        long totalCount,
        @JsonProperty("next_cursor")
        @Schema(description = "다음 페이지 커서. 마지막 페이지면 null")
        String nextCursor
) {
}

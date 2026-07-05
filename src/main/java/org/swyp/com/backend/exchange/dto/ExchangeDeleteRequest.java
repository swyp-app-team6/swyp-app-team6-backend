package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "교환한 프로필 다건 삭제 요청")
public record ExchangeDeleteRequest(
        @JsonProperty("exchange_ids")
        @NotEmpty(message = "삭제할 항목을 선택해주세요.")
        @Schema(description = "삭제할 보관함 항목 ID 목록")
        List<Long> exchangeIds
) {
}

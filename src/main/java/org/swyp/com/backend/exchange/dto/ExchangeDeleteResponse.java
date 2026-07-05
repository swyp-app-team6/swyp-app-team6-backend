package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "교환한 프로필 다건 삭제 결과")
public record ExchangeDeleteResponse(
        @JsonProperty("deleted_count")
        @Schema(description = "실제로 삭제된 개수")
        int deletedCount,
        @JsonProperty("deleted_ids")
        @Schema(description = "실제로 삭제된 보관함 항목 ID 목록")
        List<Long> deletedIds
) {
}

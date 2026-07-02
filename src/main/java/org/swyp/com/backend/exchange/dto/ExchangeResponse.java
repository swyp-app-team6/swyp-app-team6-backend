package org.swyp.com.backend.exchange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.ExchangeStatus;

@Schema(description = "프로필 교환 결과")
public record ExchangeResponse(
        @Schema(description = "교환 수락 여부")
        ExchangeStatus status,
        ExchangeResult result
) {
}

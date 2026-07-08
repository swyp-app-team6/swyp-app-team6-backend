package org.swyp.com.backend.block.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "차단 요청")
public record BlockCreateRequest(
        @JsonProperty("profile_exchange_id")
        @NotNull(message = "차단할 대상을 선택해주세요.")
        @Schema(
                description = "차단 대상이 된 보관함(교환) 항목 ID. "
                        + "`GET /exchange/archive` 또는 `GET /exchange/archive/{exchangeId}` 응답의 `exchange_id`를 그대로 전달합니다. "
                        + "차단 대상 유저는 이 값으로 서버가 내부적으로 조회하므로, 상대방의 회원 ID를 별도로 알 필요가 없습니다.",
                example = "1"
        )
        Long profileExchangeId
) {

}

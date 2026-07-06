package org.swyp.com.backend.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.swyp.com.backend.global.enumeration.ReportReasonCode;

@Schema(description = "프로필 신고 요청")
public record ReportCreateRequest(
        @JsonProperty("profile_exchange_id")
        @NotNull(message = "신고할 대상을 선택해주세요.")
        @Schema(description = "신고 대상이 된 보관함(교환) 항목 ID")
        Long profileExchangeId,

        @JsonProperty("reason_codes")
        @NotEmpty(message = "신고 사유를 선택해주세요.")
        @Schema(description = "신고 사유 코드 목록 (다중 선택 가능)")
        List<ReportReasonCode> reasonCodes,

        @JsonProperty("etc_detail")
        @Size(max = ETC_DETAIL_MAX_LENGTH, message = "기타 사유는 " + ETC_DETAIL_MAX_LENGTH + "자를 초과할 수 없습니다.")
        @Schema(description = "기타 사유 선택 시 입력하는 상세 사유 (최대 300자)")
        String etcDetail
) {

    public static final int ETC_DETAIL_MAX_LENGTH = 300;
}

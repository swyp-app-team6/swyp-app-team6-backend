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
        @Schema(
                description = "신고 대상이 된 보관함(교환) 항목 ID. "
                        + "`GET /exchange/archive` 또는 `GET /exchange/archive/{exchangeId}` 응답의 `exchange_id`를 그대로 전달합니다. "
                        + "신고 대상 유저는 이 값으로 서버가 내부적으로 조회하므로, 상대방의 회원 ID를 별도로 알 필요가 없습니다.",
                example = "1"
        )
        Long profileExchangeId,

        @JsonProperty("reason_codes")
        @NotEmpty(message = "신고 사유를 선택해주세요.")
        @Schema(
                description = """
                        신고 사유 코드 목록 (다중 선택 가능, 최소 1개 필수). 별도의 사유 목록 조회 API는 없으므로 아래 값을 클라이언트에서 직접 사용하세요.
                        - `INAPPROPRIATE_LANGUAGE`: 부적절한 언행/욕설
                        - `FRAUD_OR_MONEY_REQUEST`: 사기/금전 요구
                        - `FAKE_PROFILE`: 허위 프로필
                        - `ETC`: 기타 (선택 시 `etc_detail` 필수)

                        신고 사유 항목은 정책 확정에 따라 추후 값이 추가될 수 있습니다.
                        """,
                example = "[\"FAKE_PROFILE\"]"
        )
        List<ReportReasonCode> reasonCodes,

        @JsonProperty("etc_detail")
        @Size(max = ETC_DETAIL_MAX_LENGTH, message = "기타 사유는 " + ETC_DETAIL_MAX_LENGTH + "자를 초과할 수 없습니다.")
        @Schema(
                description = "`reason_codes`에 `ETC`가 포함된 경우에만 필수인 상세 사유 (최대 300자). "
                        + "`ETC`를 선택하지 않았다면 값을 보내도 무시되고 저장되지 않습니다.",
                example = "게시글에 다른 사람 사진을 도용한 것 같아요"
        )
        String etcDetail
) {

    public static final int ETC_DETAIL_MAX_LENGTH = 300;
}

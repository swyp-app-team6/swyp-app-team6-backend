package org.swyp.com.backend.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.swyp.com.backend.global.enumeration.ReportStatus;

@Schema(description = "프로필 신고 접수 응답")
public record ReportResponse(
        @Schema(description = "신고 ID")
        Long reportId,
        @Schema(
                description = "처리 상태. 접수 직후에는 항상 `RECEIVED`이며, "
                        + "이후 상태(`IN_REVIEW`, `RESOLVED`)는 운영 검토 결과에 따라 내부적으로 변경됩니다. "
                        + "이 값을 조회/폴링하는 별도 API는 아직 없으므로 앱에서는 접수 완료 안내에만 사용하세요."
        )
        ReportStatus status,
        @Schema(description = "신고 접수 시각")
        LocalDateTime createdAt
) {
}

package org.swyp.com.backend.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.swyp.com.backend.global.enumeration.ReportStatus;

@Schema(description = "프로필 신고 접수 응답")
public record ReportResponse(
        @Schema(description = "신고 ID")
        Long reportId,
        @Schema(description = "처리 상태")
        ReportStatus status,
        @Schema(description = "신고 접수 시각")
        LocalDateTime createdAt
) {
}

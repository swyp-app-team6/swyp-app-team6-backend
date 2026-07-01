package org.swyp.com.backend.upload.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "S3 Presigned URL 발급 응답")
public record PresignedUploadResponse(
        @Schema(description = "파일을 PUT 방식으로 직접 업로드할 수 있는 Presigned URL (10분간 유효)")
        String uploadUrl,
        @Schema(description = "업로드된 파일을 식별하는 키. 프로필 등록/수정 API에 사용")
        String imageKey
) {

}

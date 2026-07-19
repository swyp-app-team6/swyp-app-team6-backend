package org.swyp.com.backend.block.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "차단 항목")
public record BlockResponse(
        @JsonProperty("block_id")
        @Schema(description = "차단 ID (차단 해제에 사용)")
        Long blockId,
        @Schema(description = "차단한 상대방 닉네임")
        String nickname,
        @JsonProperty("image_key")
        @Schema(description = "차단한 상대방 프로필 이미지 URL (CloudFront Signed URL, 필드명은 image_key 유지)")
        String imageKey,
        @JsonProperty("created_at")
        @Schema(description = "차단 시각")
        LocalDateTime createdAt
) {

}

package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.swyp.com.backend.interest.dto.InterestTypeLabel;
import org.swyp.com.backend.profile.dto.ProfileResponse;

@Schema(description = "교환한 프로필 상세")
public record ExchangeResult(
        @JsonProperty("exchange_id")
        @Schema(description = "보관함 항목 ID (상세 조회/삭제에 사용)")
        Long exchangeId,
        @Schema(description = "교환 완료 시각")
        @JsonProperty("exchanged_at")
        LocalDateTime createdAt,
        @Schema(description = "관심사 매칭 여부")
        @JsonProperty("is_matched")
        Boolean isMatched,
        @Schema(description = "매칭된 관심사")
        @JsonProperty("matched_interests")
        List<InterestTypeLabel> matchedInterests,
        @Schema(description = "후기")
        String memo,
        @Schema(description = "점수")
        Integer score,
        @JsonProperty("is_liked")
        @Schema(description = "좋아요 표시 여부")
        Boolean isLiked,
        @Schema(description = "상대방 프로필 상세 (TMI, 키워드 등 전체 정보 포함)")
        ProfileResponse profile
) {
}

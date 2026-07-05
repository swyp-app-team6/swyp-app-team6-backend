package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.swyp.com.backend.interest.dto.InterestTypeLabel;
import org.swyp.com.backend.profile.dto.ProfileResponse;

@Schema(description = "교환한 프로필 상세")
public record ExchangeDetailResponse(
        @JsonProperty("exchange_id")
        @Schema(description = "보관함 항목 ID")
        Long exchangeId,
        @JsonProperty("exchanged_at")
        @Schema(description = "교환 완료 시각")
        LocalDateTime exchangedAt,
        @JsonProperty("is_matched")
        @Schema(description = "관심사 매칭 여부")
        Boolean isMatched,
        @JsonProperty("matched_interests")
        @Schema(description = "교환 당시 매칭된 관심사")
        List<InterestTypeLabel> matchedInterests,
        @Schema(description = "내가 남긴 후기")
        String memo,
        @Schema(description = "내가 남긴 점수")
        Integer score,
        @JsonProperty("my_profile")
        @Schema(description = "교환 당시 매칭에 사용한 내 프로필")
        ExchangeMyProfileSummary myProfile,
        @Schema(description = "상대방 프로필 상세 (TMI, 키워드 등 전체 정보 포함)")
        ProfileResponse profile
) {
}

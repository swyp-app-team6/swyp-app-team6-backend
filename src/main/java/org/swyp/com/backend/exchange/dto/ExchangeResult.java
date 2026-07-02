package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.swyp.com.backend.profile.dto.InterestTypeLabel;
import org.swyp.com.backend.profile.dto.ProfileResponse;

@Schema(description = "교환 상세 정보")
public record ExchangeResult(
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
        @Schema(description = "교환 시간")
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("profile_response")
        ProfileResponse profileResponse
) {
}

package org.swyp.com.backend.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.interest.dto.InterestTypeLabel;
import org.swyp.com.backend.region.dto.RegionLabel;

@Schema(description = "교환한 프로필 카드")
public record ExchangeCardResponse(
        @JsonProperty("exchange_id")
        @Schema(description = "보관함 항목 ID (상세 조회/삭제에 사용)")
        Long exchangeId,
        @Schema(description = "상대방 닉네임")
        String nickname,
        @JsonProperty("image_key")
        @Schema(description = "상대방 프로필 이미지 키")
        String imageKey,
        @Schema(description = "나이")
        Integer age,
        @Schema(description = "거주지")
        RegionLabel region,
        @Schema(description = "직업")
        String job,
        @JsonProperty("cosmic_type")
        @Schema(description = "상대방 코스믹 유형")
        CosmicDatingType cosmicType,
        @JsonProperty("cosmic_type_image_key")
        String cosmicTypeImageKey,
        @Schema(description = "상대방 관심사 목록")
        List<InterestTypeLabel> interests,
        @Schema(description = "상대방 한줄 자기소개")
        String bio,
        @JsonProperty("matched_interests")
        @Schema(description = "교환 당시 매칭된 관심사")
        List<InterestTypeLabel> matchedInterests,
        @Schema(description = "내가 남긴 후기")
        String memo,
        @Schema(description = "내가 남긴 점수")
        Integer score,
        @JsonProperty("is_liked")
        @Schema(description = "좋아요 표시 여부")
        Boolean isLiked,
        @JsonProperty("exchanged_at")
        @Schema(description = "교환 완료 시각")
        LocalDateTime exchangedAt
) {
}

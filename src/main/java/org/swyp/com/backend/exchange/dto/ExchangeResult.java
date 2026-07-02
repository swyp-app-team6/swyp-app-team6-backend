package org.swyp.com.backend.exchange.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.profile.dto.ProfileResponse;

public record ExchangeResult(
        Boolean isMatched,
        List<InterestType> matchedInterests,
        String memo,
        Integer score,
        LocalDateTime createdAt,
        ProfileResponse profileResponse
) {
}

package org.swyp.com.backend.exchange.dto.cursor;

import java.time.LocalDateTime;

public record ExchangeCursor(
        LocalDateTime createdAt,
        Long profileExchangeId
) {
}

package org.swyp.com.backend.exchange.dto;

import org.swyp.com.backend.global.enumeration.ExchangeStatus;

public record ExchangeResponse(
        ExchangeStatus status,
        ExchangeResult result
) {
}

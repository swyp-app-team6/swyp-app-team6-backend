package org.swyp.com.backend.interest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.InterestType;

@Schema(description = "관심사, 레이블")
public record InterestTypeLabel(
        InterestType type,
        String label
) {
}

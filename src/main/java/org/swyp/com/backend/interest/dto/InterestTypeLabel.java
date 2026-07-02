package org.swyp.com.backend.interest.dto;

import org.swyp.com.backend.global.enumeration.InterestType;

public record InterestTypeLabel(
        InterestType type,
        String label
) {
}

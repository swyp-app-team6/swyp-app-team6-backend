package org.swyp.com.backend.cosmic.dto;

import org.swyp.com.backend.global.enumeration.CosmicDatingType;

public record CosmicTypeLabel(
        CosmicDatingType type,
        String label
) {
}

package org.swyp.com.backend.cosmic.dto;

import java.util.List;

public record CosmicTypeResponse(
        CosmicTypeLabel cosmicType,
        String detail,
        String imageKey,
        List<String> features,
        List<CosmicTypeLabel> matches,
        List<String> mentions,
        List<String> tags
) {
}

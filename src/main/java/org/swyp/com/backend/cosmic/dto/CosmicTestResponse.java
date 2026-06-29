package org.swyp.com.backend.cosmic.dto;

import java.util.List;

public record CosmicTestResponse(
        List<CosmicTest> questions
) {
}

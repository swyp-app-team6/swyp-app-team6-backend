package org.swyp.com.backend.profile.dto.region;

import java.util.List;

public record RegionResponse(
        List<RegionGroup> regions
) {
}

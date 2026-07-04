package org.swyp.com.backend.profile.dto.region;

import org.swyp.com.backend.global.enumeration.Region;
import org.swyp.com.backend.global.enumeration.RegionDetail;

public record RegionGroup(
        Region region,
        String label,
        RegionDetail detail
) {
}

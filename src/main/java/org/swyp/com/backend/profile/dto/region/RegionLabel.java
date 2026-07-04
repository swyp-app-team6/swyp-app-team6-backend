package org.swyp.com.backend.profile.dto.region;

import org.swyp.com.backend.global.enumeration.RegionDetail;

public record RegionLabel(
        RegionDetail detail,
        String label
) {
}

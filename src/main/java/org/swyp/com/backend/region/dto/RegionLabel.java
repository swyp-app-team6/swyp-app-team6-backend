package org.swyp.com.backend.region.dto;

import org.swyp.com.backend.global.enumeration.RegionDetail;

public record RegionLabel(
        RegionDetail detail,
        String label
) {
}

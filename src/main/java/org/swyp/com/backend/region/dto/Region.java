package org.swyp.com.backend.region.dto;

import java.util.List;
import org.swyp.com.backend.global.enumeration.RegionGroup;

public record Region(
        RegionGroup group,
        String label,
        List<RegionLabel> regions
) {
}

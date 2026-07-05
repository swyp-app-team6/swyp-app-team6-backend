package org.swyp.com.backend.region.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.swyp.com.backend.global.enumeration.RegionGroup;

public record Region(
        RegionGroup group,
        @Schema(description = "서울, 부산... 상위 시도")
        String label,
        List<RegionLabel> regions
) {
}

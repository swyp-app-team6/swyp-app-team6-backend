package org.swyp.com.backend.region.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.RegionDetail;

public record RegionLabel(
        @Schema(description = "서울, 부산... 상위 시도")
        String group,
        @Schema(description = "세부 주소 ENUM값(등록, 생성시 전달)")
        RegionDetail detail,
        @Schema(description = "세부 주소 레이블")
        String label
) {
}

package org.swyp.com.backend.interest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "선택 가능한 모든 관심사")
public record InterestResponse(
        List<InterestTypeLabel> interests
) {
}

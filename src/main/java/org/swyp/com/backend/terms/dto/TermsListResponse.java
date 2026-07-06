package org.swyp.com.backend.terms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "약관 목록 응답")
public record TermsListResponse(
        @Schema(description = "약관 목록")
        List<TermsItemResponse> terms
) {
}

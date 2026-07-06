package org.swyp.com.backend.terms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.swyp.com.backend.global.enumeration.TermsType;

@Schema(description = "약관 동의 처리 응답")
public record TermsAgreementResponse(
        @JsonProperty("agreed_types")
        @Schema(description = "동의 처리된 약관 종류 목록")
        List<TermsType> agreedTypes,
        @JsonProperty("agreed_at")
        @Schema(description = "동의 처리 시각")
        LocalDateTime agreedAt
) {
}

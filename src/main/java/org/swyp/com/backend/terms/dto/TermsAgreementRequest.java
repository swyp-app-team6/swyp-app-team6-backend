package org.swyp.com.backend.terms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.swyp.com.backend.global.enumeration.TermsType;

@Schema(description = "약관 동의 요청")
public record TermsAgreementRequest(
        @JsonProperty("agreed_types")
        @NotEmpty(message = "동의할 약관을 선택해주세요.")
        @Schema(description = "동의한 약관 종류 목록")
        List<TermsType> agreedTypes
) {
}

package org.swyp.com.backend.terms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.swyp.com.backend.global.enumeration.TermsType;

@Schema(description = "약관 항목")
public record TermsItemResponse(
        @Schema(description = "약관 종류")
        TermsType type,
        @Schema(description = "필수 동의 여부")
        boolean required,
        @Schema(description = "약관 버전")
        int version,
        @Schema(description = "약관 명칭")
        String label,
        @JsonProperty("content_url")
        @Schema(description = "약관 상세 내용 링크")
        String contentUrl
) {

    public static TermsItemResponse from(TermsType termsType, String contentUrl) {
        return new TermsItemResponse(termsType, termsType.isRequired(), termsType.getCurrentVersion(),
                termsType.getLabel(), contentUrl);
    }
}

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
        @Schema(
                description = "사용자가 동의 체크한 약관 종류 목록. "
                        + "`GET /terms` 응답에서 `required: true`인 항목은 전부 포함되어야 하며, 하나라도 빠지면 400이 반환되고 아무것도 저장되지 않습니다. "
                        + "화면의 \"전체 동의\" 토글은 별도 필드 없이 `GET /terms`가 내려준 모든 `type` 값을 그대로 담아 보내는 방식으로 구현하세요. "
                        + "동의 시점의 버전은 클라이언트가 지정하지 않고, 서버가 각 항목의 현재 버전(`GET /terms`의 `version`)으로 자동 기록합니다.",
                example = "[\"SERVICE\", \"PRIVACY\", \"AGE_OVER_14\"]"
        )
        List<TermsType> agreedTypes
) {
}

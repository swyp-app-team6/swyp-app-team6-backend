package org.swyp.com.backend.terms.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.terms.dto.TermsAgreementRequest;
import org.swyp.com.backend.terms.dto.TermsAgreementResponse;
import org.swyp.com.backend.terms.dto.TermsListResponse;

@Tag(name = "Terms", description = "이용약관 동의 API")
@SecurityRequirement(name = "bearerAuth")
public interface TermsControllerApiSpec {

    @Operation(
            summary = "약관 목록 조회",
            description = "동의 화면을 그리기 위한 약관 종류/필수 여부/버전/내용 링크 목록을 조회합니다.",
            operationId = "getTermsList"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "terms": [
                                                {"type": "SERVICE", "required": true, "version": 1, "label": "서비스 이용약관", "content_url": "https://www.notion.so/service-terms"},
                                                {"type": "PRIVACY", "required": true, "version": 1, "label": "개인정보 처리방침", "content_url": "https://www.notion.so/privacy-policy"},
                                                {"type": "AGE_OVER_14", "required": true, "version": 1, "label": "만 14세 이상입니다", "content_url": null}
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<TermsListResponse> getTermsList();

    @Operation(
            summary = "약관 동의 처리",
            description = "동의한 약관 종류 목록을 기록합니다. 필수 약관 중 하나라도 누락되면 400을 반환합니다.",
            operationId = "agreeToTerms"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "동의 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "agreed_types": ["SERVICE", "PRIVACY", "AGE_OVER_14"],
                                              "agreed_at": "2026-07-06T18:30:26.371178"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수 약관 누락",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "필수 약관에 모두 동의해야 합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<TermsAgreementResponse> agreeToTerms(
            UserDetails userDetails,
            TermsAgreementRequest request
    );
}

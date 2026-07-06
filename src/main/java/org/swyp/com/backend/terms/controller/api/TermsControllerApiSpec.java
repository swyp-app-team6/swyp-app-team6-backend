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
            description = """
                    회원가입(최초 로그인) 직후 동의 체크리스트 화면을 그리기 위한 약관 종류/필수 여부/버전/명칭/내용 링크 목록을 조회합니다.

                    **호출 시점**
                    - Google/Apple 앱 SSO 로그인 응답(`SsoLoginResponse`)의 `requires_terms_agreement`가 `true`이면, 다른 화면으로 넘어가기 전에 이 API로 목록을 받아 동의 화면을 띄워주세요.
                    - 로그인 이후에도 항상 호출 가능합니다(예: 설정 화면에서 약관 재열람).

                    **응답 사용법**
                    - `required: true`인 항목만 동의가 필수이며, 전부 동의해야 `POST /terms/agreements` 호출이 성공합니다.
                    - `content_url`이 있는 항목은 노션 페이지 등 약관 원문 링크이므로 화면에서 링크로 열어주세요(`null`이면 텍스트만 표시, 만 14세 이상 확인 항목이 해당).
                    - `version`은 약관 개정 시 올라갈 수 있습니다. 개정되면 기존에 동의했던 사용자도 다음 로그인 시 `requires_terms_agreement`가 다시 `true`로 내려와 재동의 화면이 노출됩니다.
                    """,
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
            description = """
                    `GET /terms`로 받은 목록을 화면에 그린 뒤, 사용자가 체크한 약관 종류를 전달하면 동의 이력으로 기록합니다.

                    **연동 순서**
                    1. `GET /terms`로 목록을 받아 개별 동의 체크박스 + 전체 동의 토글 화면을 구성합니다.
                    2. 사용자가 필수 항목에 모두 체크(또는 전체 동의)해야 "동의 완료하기" 버튼을 활성화합니다(버튼 비활성화는 앱 책임).
                    3. 체크된 `type` 값들을 `agreed_types` 배열로 담아 이 API를 호출합니다.
                    4. 이 API는 **필수 게이트 역할이 아닙니다.** 호출 성공 여부와 무관하게 다른 API 호출 자체가 백엔드에서 막히지는 않으므로, 앱에서 반드시 이 API 성공 응답을 받은 뒤에만 다음 화면으로 진입시켜주세요.

                    **주의사항**
                    - 필수(`required: true`) 항목이 하나라도 `agreed_types`에 빠지면 요청 전체가 400으로 거부되고 아무 것도 저장되지 않습니다(부분 저장 없음).
                    - 이미 동의한 약관에 다시 동의를 보내도 에러 없이 새 이력으로 누적 저장됩니다(약관 개정 시 재동의 처리와 동일한 경로).
                    """,
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

package org.swyp.com.backend.report.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.report.dto.ReportCreateRequest;
import org.swyp.com.backend.report.dto.ReportResponse;

@Tag(name = "Report", description = "프로필 신고 API")
@SecurityRequirement(name = "bearerAuth")
public interface ReportControllerApiSpec {

    @Operation(
            summary = "프로필 신고",
            description = """
                    교환한 프로필 보관함 상세 화면(`GET /exchange/archive/{exchangeId}`)에서 상대방 프로필을 신고할 때 사용합니다.

                    **연동 순서**
                    1. 보관함 상세 화면에 진입할 때 이미 가지고 있는 `exchange_id` 값을 그대로 `profile_exchange_id`로 전달합니다.
                    2. 신고 사유(`reason_codes`)를 1개 이상 선택해 배열로 전달합니다(다중 선택 UI, 체크박스 형태를 가정).
                    3. `ETC`를 선택한 경우에만 `etc_detail`에 상세 사유를 함께 담아 보냅니다(최대 300자, 미입력/공백이면 400).
                    4. 응답의 `reportId`로 접수 완료 안내만 하면 되고, 이후 처리 상태를 앱에서 추적할 필요는 없습니다.

                    **주의사항**
                    - 신고 대상 유저는 서버가 `profile_exchange_id`로부터 내부적으로 판별합니다. 상대방의 회원 ID를 앱에서 별도로 알거나 전달할 필요가 없고, 전달할 수도 없습니다.
                    - 신고 사유 목록을 조회하는 별도 API는 없습니다(정책 확정 전이라 우선 앱에서 하드코딩). `reason_codes`에 사용 가능한 값은 `ReportCreateRequest`의 필드 설명을 참고하세요.
                    - 같은 대상에 대해 여러 번 신고를 접수해도 서버가 막지 않습니다(중복 방지 로직 없음). 재신고 방지 UX가 필요하면 앱 쪽에서 버튼 비활성화 등으로 처리해주세요.
                    - 이번 범위에는 차단(block) 기능이 포함되어 있지 않습니다. 화면 UX상 신고와 차단이 함께 노출되더라도, 차단은 별도로 안내드릴 API를 기다려주세요.
                    """,
            operationId = "createReport"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "신고 접수 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "reportId": 1,
                                              "status": "RECEIVED",
                                              "createdAt": "2026-07-06T18:30:26.371178"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "신고 사유 누락 또는 기타 사유 상세 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "기타 사유를 선택한 경우 상세 사유를 입력해주세요."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 본인 소유가 아닌 보관함 항목",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "NOT_FOUND",
                                              "status": 404,
                                              "detail": "신고할 대상을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ReportResponse> createReport(
            UserDetails userDetails,
            ReportCreateRequest request
    );
}

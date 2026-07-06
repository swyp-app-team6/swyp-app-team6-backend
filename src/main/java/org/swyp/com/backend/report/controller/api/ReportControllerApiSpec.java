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
            description = "보관함 항목(profile_exchange_id)을 근거로 상대방 프로필을 신고합니다. "
                    + "신고 사유는 다중 선택 가능하며, 기타 사유 선택 시 상세 사유가 필수입니다.",
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

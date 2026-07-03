package org.swyp.com.backend.exchange.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.async.DeferredResult;
import org.swyp.com.backend.exchange.dto.ExchangeResponse;
import org.swyp.com.backend.profile.dto.ProfileResponse;

@Tag(name = "Exchange", description = "교환 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface ExchangeControllerApiSpec {

    @Operation(
            summary = "프로필 교환 대기 시작",
            description = "현재 사용자가 교환 대기 상태로 진입합니다. 상대방이 QR코드를 스캔하기까지 대기합니다.",
            operationId = "waitProfileResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상대방의 교환 시작 요청시 상대방의 프로필 정보 응답.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "nickname": "홍길동",
                                              "image_key": "image-key",
                                              "gender": "M",
                                              "age": 26,
                                              "region": "SEOUL",
                                              "job": "개발자",
                                              "interests": ["TRAVEL", "SPORTS", "CAFE"],
                                              "bio": "여행과 운동을 좋아해요",
                                              "cosmic_type": "GALAXY",
                                              "choice_template": [],
                                              "short_template": []
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "교환 취소 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "USER_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "사용자 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PROFILE_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "프로필 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "408",
                    description = "요청 타임 아웃",
                    content = @Content
            )
    })
    DeferredResult<ResponseEntity<ProfileResponse>> waitProfileResponse(
            UserDetails userDetails
    );


    @Operation(
            summary = "프로필 교환 시작 및 결과 응답 대기",
            description = "QR코드를 스캔하여 프로필 교환을 시작하고 상대방의 수락/거절을 기다립니다.",
            operationId = "waitExchangeResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상대방의 프로필 교환 수락,거절에 대한 교환 결과 응답",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExchangeResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "ACCEPTED",
                                            value = """
                                                    {
                                                      "status": "ACCEPTED",
                                                      "result": {
                                                        "isMatched": true,
                                                        "matchedInterests": ["TRAVEL", "SPORTS"],
                                                        "memo": null,
                                                        "score": null,
                                                        "createdAt": "2026-07-02T18:30:26.3711781",
                                                        "profileResponse": {
                                                          "id": 1,
                                                          "nickname": "홍길동",
                                                          "image_key": "image-key",
                                                          "gender": "M",
                                                          "age": 26,
                                                          "region": "SEOUL",
                                                          "job": "개발자",
                                                          "interests": ["TRAVEL", "SPORTS", "CAFE"],
                                                          "bio": "여행과 운동을 좋아해요",
                                                          "cosmic_type": null,
                                                          "cosmic_type_image_key": null,
                                                          "cosmic_type_detail": null,
                                                          "choice_template": [],
                                                          "short_template": []
                                                        }
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "DECLINED",
                                            value = """
                                                    {
                                                      "status": "DECLINED",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "교환 취소 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "INVALID_PARAMETER",
                                            value = """
                                                    {
                                                      "title": "BAD_REQUEST",
                                                      "status": 400,
                                                      "detail": "요청 파라미터 형식이 잘못되었습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "QR_EXPIRED",
                                            value = """
                                                    {
                                                      "title": "BAD_REQUEST",
                                                      "status": 400,
                                                      "detail": "만료된 QR 코드 입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "USER_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "사용자 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PROFILE_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "프로필 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "408",
                    description = "요청 타임 아웃",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "상대방이 교환 대기 중이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "CONFLICT",
                                              "status": 409,
                                              "detail": "상대방이 교환 대기 중이 아닙니다."
                                            }
                                            """
                            )
                    )
            )
    })
    DeferredResult<ResponseEntity<ExchangeResponse>> waitExchangeResponse(
            @Parameter(
                    description = "QR 스캔하여 추출된 UUID 값",
                    required = true,
                    example = "78eaffda-af5c-4cea-911a-c2e852ac8da2"
            )
            UUID uuid,
            UserDetails userDetails
    );


    @Operation(
            summary = "교환 수락",
            description = "상대방의 교환 시작에 대한 교환 수락을 수행합니다.",
            operationId = "acceptExchange"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "교환 수락하여 교환 결과를 응답.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExchangeResponse.class),
                            examples =
                            @ExampleObject(
                                    name = "ACCEPTED",
                                    value = """
                                            {
                                              "status": "ACCEPTED",
                                              "result": {
                                                "isMatched": true,
                                                "matchedInterests": ["TRAVEL", "SPORTS"],
                                                "memo": null,
                                                "score": null,
                                                "createdAt": "2026-07-02T18:30:26.3711781",
                                                "profileResponse": {
                                                  "id": 1,
                                                  "nickname": "홍길동",
                                                  "image_key": "image-key",
                                                  "gender": "M",
                                                  "age": 26,
                                                  "region": "SEOUL",
                                                  "job": "개발자",
                                                  "interests": ["TRAVEL", "SPORTS", "CAFE"],
                                                  "bio": "여행과 운동을 좋아해요",
                                                  "cosmic_type": null,
                                                  "cosmic_type_image_key": null,
                                                  "cosmic_type_detail": null,
                                                  "choice_template": [],
                                                  "short_template": []
                                                }
                                              }
                                            }
                                            """
                            )

                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "요청 파라미터 형식이 잘못되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "USER_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "사용자 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PROFILE_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "프로필 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "상대방이 교환 대기 중이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "CONFLICT",
                                              "status": 409,
                                              "detail": "상대방이 교환 대기 중이 아닙니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ExchangeResponse> acceptExchange(
            @Parameter(
                    description = "미리보기 프로필 Response에 담긴 프로필 id 값",
                    required = true,
                    example = "1"
            )
            Long profileId,
            UserDetails userDetails
    );


    @Operation(
            summary = "교환 거절",
            description = "상대방의 교환 요청을 거절합니다. 상대방은 DECLINED 상태를 받습니다.",
            operationId = "declineExchange"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "교환 거절 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "요청 파라미터 형식이 잘못되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "USER_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "사용자 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PROFILE_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "프로필 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "상대방이 교환 대기 중이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "CONFLICT",
                                              "status": 409,
                                              "detail": "상대방이 교환 대기 중이 아닙니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<Void> declineExchange(
            @Parameter(
                    description = "미리보기 프로필 Response에 담긴 프로필 id 값",
                    required = true,
                    example = "1"
            )
            Long profileId,
            UserDetails userDetails
    );


    @Operation(
            summary = "교환 대기 취소",
            description = "내 교환 대기 요청을 취소합니다.",
            operationId = "cancelExchangeWait"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "교환 대기 취소 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "USER_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "사용자 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PROFILE_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "프로필 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "교환 대기 중이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "CONFLICT",
                                              "status": 409,
                                              "detail": "교환 대기 중이 아닙니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<Void> cancelExchangeWait(
            UserDetails userDetails
    );


    @Operation(
            summary = "교환 시작 취소",
            description = "내 교환 시작 요청을 취소합니다.",
            operationId = "cancelExchangeStart"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "교환 시작 요청 취소 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "요청 파라미터 형식이 잘못되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "UNAUTHORIZED",
                                              "status": 401,
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "USER_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "사용자 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PROFILE_NOT_FOUND",
                                            value = """
                                                    {
                                                      "title": "NOT_FOUND",
                                                      "status": 404,
                                                      "detail": "프로필 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "교환 대기 중이 아님",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "CONFLICT",
                                              "status": 409,
                                              "detail": "교환 대기 중이 아닙니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<Void> cancelExchangeStart(
            @Parameter(
                    description = "미리보기 프로필 Response에 담긴 프로필 id 값",
                    required = true,
                    example = "1"
            )
            Long profileId,
            UserDetails userDetails
    );
}

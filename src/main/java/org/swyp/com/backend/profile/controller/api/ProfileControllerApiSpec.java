package org.swyp.com.backend.profile.controller.api;

import io.swagger.v3.oas.annotations.Operation;
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
import org.swyp.com.backend.profile.dto.ProfileCosmicUpdateRequest;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.QrResponse;

@Tag(name = "Profile", description = "프로필 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface ProfileControllerApiSpec {

    @Operation(
            summary = "내 프로필 조회",
            description = "현재 로그인된 사용자의 프로필 정보를 조회합니다.",
            operationId = "getProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
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
                                              "age": 25,
                                              "region": {
                                                  "group": "서울",
                                                  "detail": "SEOUL_GANGNAM",
                                                  "label": "강남"
                                              },
                                              "job": "개발자",
                                              "interests": [
                                                "TRAVEL",
                                                "SPORTS",
                                                "MUSIC"
                                              ],
                                              "bio": "여행을 좋아해요",
                                              "cosmic_type": "GALAXY",
                                              "choice_template": [
                                                {
                                                  "question_id": 1,
                                                  "question_type": "BINARY",
                                                  "question": "저는 호감이 생기면",
                                                  "answer_id": 1,
                                                  "answer": "티가 나는 편이에요"
                                                }
                                              ],
                                              "short_template": [
                                                {
                                                  "question_id": 5,
                                                  "question_type": "BLANK",
                                                  "question": "나는 자주 이런 말을 들어요 “너는 진짜 ______ 같아”",
                                                  "answer": "긍정적인 사람"
                                                }
                                              ]
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
            )
    })
    ResponseEntity<ProfileResponse> getMyProfile(
            UserDetails userDetails
    );

    @Operation(
            summary = "프로필 등록",
            description = "현재 로그인된 사용자의 프로필을 등록합니다. "
                    + "필수 정보(닉네임, 이미지, 성별, 나이, 지역, 직업, 관심사), 부가 정보(자기 소개, 코스믹 유형, 객관식 질문 템플릿, 주관식 질문 템플릿) 저장합니다.",
            operationId = "registerProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 등록 성공",
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
                                              "age": 25,
                                              "region": {
                                                  "group": "서울",
                                                  "detail": "SEOUL_GANGNAM",
                                                  "label": "강남"
                                              },
                                              "job": "개발자",
                                              "interests": [
                                                "TRAVEL",
                                                "SPORTS",
                                                "MUSIC"
                                              ],
                                              "bio": "여행을 좋아해요",
                                              "cosmic_type": "GALAXY",
                                              "choice_template": [
                                                {
                                                  "question_id": 1,
                                                  "question_type": "BINARY",
                                                  "question": "저는 호감이 생기면",
                                                  "answer_id": 1,
                                                  "answer": "티가 나는 편이에요"
                                                },
                                                {
                                                  "question_id": 2,
                                                  "question_type": "BINARY",
                                                  "question": "애프터 신청은",
                                                  "answer_id": 2,
                                                  "answer": "상대가 해주면 좋아요"
                                                }
                                              ],
                                              "short_template": [
                                                {
                                                  "question_id": 5,
                                                  "question_type": "BLANK",
                                                  "question": "나는 자주 이런 말을 들어요 “너는 진짜 ______ 같아”",
                                                  "answer": "긍정적인 사람"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "닉네임은 3~10자여야 합니다."
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
                    description = "사용자 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "NOT_FOUND",
                                              "status": 404,
                                              "detail": "사용자 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 프로필이 존재함",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "CONFLICT",
                                              "status": 409,
                                              "detail": "이미 프로필을 생성하였습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ProfileResponse> registerProfile(
            UserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 등록 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileRegisterRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "nickname": "홍길동",
                                              "image_key": "image-key",
                                              "gender": "M",
                                              "age": 25,
                                              "region": "SEOUL_GANGNAM",
                                              "job": "개발자",
                                              "interests": [
                                                "TRAVEL",
                                                "SPORTS",
                                                "MUSIC"
                                              ],
                                              "bio": "여행을 좋아해요",
                                              "cosmic_type": "GALAXY",
                                              "choice_template": [
                                                {
                                                  "question_id": 1,
                                                  "answer_id": 1
                                                },
                                                {
                                                  "question_id": 2,
                                                  "answer_id": 2
                                                }
                                              ],
                                              "short_template": [
                                                {
                                                  "question_id": 5,
                                                  "answer": "긍정적인 사람"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            ProfileRegisterRequest registerRequest
    );

    @Operation(
            summary = "프로필 수정",
            description = "현재 로그인된 사용자의 프로필을 수정합니다. "
                    + "기본 정보(닉네임, 이미지, 나이, 지역, 직업, 관심사), 부가 정보(자기 소개, 코스믹 유형, 객관식 질문 템플릿, 주관식 질문 템플릿)를 수정합니다."
                    + "수정하려는 정보만 입력합니다.",
            operationId = "updateProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공",
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
                                              "region": {
                                                  "group": "서울",
                                                  "detail": "SEOUL_GANGNAM",
                                                  "label": "강남"
                                              },
                                              "job": "백엔드 개발자",
                                              "interests": [
                                                "TRAVEL",
                                                "SPORTS",
                                                "CAFE"
                                              ],
                                              "bio": "여행과 운동을 좋아해요",
                                              "choice_template": [
                                                {
                                                  "question_id": 1,
                                                  "question_type": "BINARY",
                                                  "question": "저는 호감이 생기면",
                                                  "answer_id": 2,
                                                  "answer": "살짝 숨기는 편이에요"
                                                },
                                                {
                                                  "question_id": 2,
                                                  "question_type": "BINARY",
                                                  "question": "애프터 신청은",
                                                  "answer_id": 2,
                                                  "answer": "상대가 해주면 좋아요"
                                                }
                                              ],
                                              "short_template": [
                                                {
                                                  "question_id": 5,
                                                  "question_type": "BLANK",
                                                  "question": "나는 자주 이런 말을 들어요 “너는 진짜 ______ 같아”",
                                                  "answer": "긍정적인 사람"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "닉네임은 3~10자여야 합니다."
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
            )
    })
    ResponseEntity<ProfileResponse> updateProfile(
            UserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 수정 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileUpdateRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "age": 26,
                                              "interests": [
                                                "TRAVEL",
                                                "SPORTS",
                                                "CAFE"
                                              ],
                                              "bio": "여행과 운동을 좋아해요",
                                              "choice_template": [
                                                {
                                                  "question_id": 1,
                                                  "answer_id": 2
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
            ProfileUpdateRequest profileUpdateRequest
    );

    @Operation(
            summary = "프로필 코스믹 유형 수정",
            description = "현재 로그인된 사용자의 코스믹 유형을 수정합니다.",
            operationId = "updateProfileCosmic"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "코스믹 유형 수정 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "유효하지 않은 코스믹 유형입니다."
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
            )
    })
    ResponseEntity<Void> updateProfileCosmic(
            UserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 코스믹 유형 수정 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfileCosmicUpdateRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "cosmic_type": "GALAXY"
                                            }
                                            """
                            )
                    )
            )
            ProfileCosmicUpdateRequest profileCosmicUpdateRequest
    );

    @Operation(
            summary = "프로필 삭제",
            description = "현재 로그인된 사용자의 프로필을 삭제합니다. "
                    + "프로필 정보와 관심사, 객관식 질문 템플릿, 주관식 질문 템플릿 등 프로필에 연결된 모든 정보가 함께 삭제됩니다.",
            operationId = "deleteProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "프로필 삭제 성공",
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
            )
    })
    ResponseEntity<Void> deleteProfile(
            UserDetails userDetails
    );

    @Operation(
            summary = "UUID 조회 및 생성",
            description = "QR 생성을 위한 자신의 프로필 UUID 정보를 응답합니다.",
            operationId = "getQrUUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "UUID 응답 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QrResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "uuid": "550e8400-e29b-41d4-a716-446655440000"
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
            )
    })
    ResponseEntity<QrResponse> getQrUUID(
            UserDetails userDetails
    );

    @Operation(
            summary = "상대방 프로필 조회",
            description = "QR 코드 UUID를 이용하여 상대방의 프로필 정보를 조회합니다.",
            operationId = "getProfileByUUID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
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
                                              "region": {
                                                  "group": "서울",
                                                  "detail": "SEOUL_GANGNAM",
                                                  "label": "강남"
                                              },
                                              "job": "개발자",
                                              "interests": [
                                                "TRAVEL",
                                                "SPORTS",
                                                "CAFE"
                                              ],
                                              "bio": "여행과 운동을 좋아해요",
                                              "cosmic_type": null,
                                              "cosmic_type_image_key": null,
                                              "cosmic_type_detail": null,
                                              "choice_template": [
                                                {
                                                  "question_id": 1,
                                                  "question_type": "BINARY",
                                                  "question": "저는 호감이 생기면",
                                                  "answer_id": 2,
                                                  "answer": "살짝 숨기는 편이에요"
                                                }
                                              ],
                                              "short_template": []
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
                    description = "프로필 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "NOT_FOUND",
                                              "status": 404,
                                              "detail": "프로필 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ProfileResponse> getProfile(
            UUID uuid,
            UserDetails userDetails
    );
}

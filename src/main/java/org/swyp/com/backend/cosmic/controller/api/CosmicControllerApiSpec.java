package org.swyp.com.backend.cosmic.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.swyp.com.backend.cosmic.dto.CosmicTestResponse;
import org.swyp.com.backend.cosmic.dto.CosmicTypeResponse;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;

@Tag(name = "Cosmic", description = "Cosmic 테스트 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface CosmicControllerApiSpec {

    @Operation(
            summary = "Cosmic 테스트 질문 조회",
            description = "Cosmic 테스트 질문과 답변 목록을 조회합니다.",
            operationId = "getCosmicTestResponse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cosmic 테스트 질문 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CosmicTestResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "questions": [
                                                {
                                                  "question_id": 1,
                                                  "question": "연인과의 약속이 취소됐을 때 나는",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "혼자만의 시간을 보내요.",
                                                      "cosmic": "GALAXY",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "다른 친구를 만나러 가요.",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "다음 약속을 다시 잡아요.",
                                                      "cosmic": "LUNA",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "조금 서운한 마음이 들어요.",
                                                      "cosmic": "SOLA",
                                                      "score": 1
                                                    }
                                                  ]
                                                },
                                                {
                                                  "question_id": 2,
                                                  "question": "연인이 고민이 있다고 말하면 나는",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "도와주고, 생각할 시간을 존중해요.",
                                                      "cosmic": "GALAXY",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "기분이 나아지도록 웃게 해줘요.",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "차분히 이야기를 끝까지 들어줘요.",
                                                      "cosmic": "LUNA",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "함께 공감하며 감정을 나눠요.",
                                                      "cosmic": "SOLA",
                                                      "score": 1
                                                    }
                                                  ]
                                                },
                                                {
                                                  "question_id": 3,
                                                  "question": "가장 끌리는 데이트는?",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "각자 하고 싶은 걸 하며 만나는 데이트",
                                                      "cosmic": "GALAXY",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "액티비티나 놀러 다니는 데이트",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "맛집, 산책처럼 편안한 데이트",
                                                      "cosmic": "LUNA",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "둘만의 시간을 오래 보내는 데이트",
                                                      "cosmic": "SOLA",
                                                      "score": 1
                                                    }
                                                  ]
                                                },
                                                {
                                                  "question_id": 4,
                                                  "question": "연인과 다툰 뒤 나는",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "잠시 시간을 가져요.",
                                                      "cosmic": "GALAXY",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "금방 풀고 웃어요.",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "대화로 해결해요.",
                                                      "cosmic": "LUNA",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "감정을 충분히 표현해요.",
                                                      "cosmic": "SOLA",
                                                      "score": 1
                                                    }
                                                  ]
                                                },
                                                {
                                                  "question_id": 5,
                                                  "question": "가장 받고 싶은 선물은?",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "원하는 걸 살 수 있는 용돈",
                                                      "cosmic": "GALAXY",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "깜짝 이벤트",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "오래 쓸 수 있는 선물",
                                                      "cosmic": "LUNA",
                                                      "score": 1
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "손편지나 커플 아이템",
                                                      "cosmic": "SOLA",
                                                      "score": 1
                                                    }
                                                  ]
                                                },
                                                {
                                                  "question_id": 6,
                                                  "question": "호감이 생기면 나는",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "자연스럽게 다가가요.",
                                                      "cosmic": "GALAXY",
                                                      "score": 2
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "장난을 많이 쳐요.",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 2
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "천천히 가까워져요.",
                                                      "cosmic": "LUNA",
                                                      "score": 2
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "적극적으로 표현해요.",
                                                      "cosmic": "SOLA",
                                                      "score": 2
                                                    }
                                                  ]
                                                },
                                                {
                                                  "question_id": 7,
                                                  "question": "이상적인 연애는?",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "answer": "서로를 존중하는 연애",
                                                      "cosmic": "GALAXY",
                                                      "score": 5
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "answer": "친구처럼 웃는 연애",
                                                      "cosmic": "SHOOTING_STAR",
                                                      "score": 5
                                                    },
                                                    {
                                                      "answer_id": 3,
                                                      "answer": "오래 함께하는 연애",
                                                      "cosmic": "LUNA",
                                                      "score": 5
                                                    },
                                                    {
                                                      "answer_id": 4,
                                                      "answer": "서로에게 푹 빠지는 연애",
                                                      "cosmic": "SOLA",
                                                      "score": 5
                                                    }
                                                  ]
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
            )
    })
    ResponseEntity<CosmicTestResponse> getCosmicTestResponse();


    @Operation(
            summary = "Cosmic 타입 조회",
            description = "CosmicDatingType 기반으로 해당 타입의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CosmicTypeResponse.class,
                                    example = """
                                            {
                                              "cosmicType": {
                                                "type": "SOLA",
                                                "label": "솔라 형"
                                              },
                                              "detail": "사랑을 아낌없이 표현하는 열정적인 연애",
                                              "imageKey": "",
                                              "features": [
                                                "둘만의 특별한 추억을 중요하게 생각해요.",
                                                "감정을 솔직하게 표현해요.",
                                                "연인에게 시간과 정성을 아끼지 않아요.",
                                                "깊은 교감과 애정을 원해요."
                                              ],
                                              "matches": [
                                                {
                                                  "type": "SHOOTING_STAR",
                                                  "label": "슈팅스타 형"
                                                },
                                                {
                                                  "type": "LUNA",
                                                  "label": "루나 형"
                                                }
                                              ],
                                              "mentions": [
                                                "'표현을 정말 잘 한다.'",
                                                "'사랑받는다는 느낌이 들어.'"
                                              ],
                                              "tags": [
                                                "애정표현",
                                                "열정",
                                                "직진"
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST - 요청 파라미터 형식이 잘못되었습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "title": "BAD_REQUEST",
                                                "status": 400,
                                                "detail": "요청 파라미터 형식이 잘못되었습니다.",
                                                "instance": "/cosmic/SOLAC"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED - 인증이 필요합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
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
                    description = "NOT_FOUND - 코스믹 타입 정보를 찾을 수 없습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = """
                                            {
                                                "title": "NOT_FOUND",
                                                "status": 404,
                                                "detail": "코스믹 타입 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<CosmicTypeResponse> getCosmicType(
            @Parameter(
                    description = "조회할 Cosmic Dating Type",
                    required = true,
                    example = "SOLA"
            )
            CosmicDatingType type
    );
}

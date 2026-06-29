package org.swyp.com.backend.question.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.swyp.com.backend.question.dto.CustomQuestionResponse;

@Tag(
        name = "Question",
        description = "질문 템플릿 관련 API"
)
@SecurityRequirement(name = "bearerAuth")
public interface QuestionControllerApiSpec {

    @Operation(
            summary = "질문 템플릿 조회",
            description = "프로필 작성에 사용되는 객관식 질문과 주관식 질문 목록을 조회합니다.",
            operationId = "getCustomQuestionResponse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "질문 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomQuestionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "multiple_questions": [
                                                {
                                                  "id": 1,
                                                  "type": "BINARY",
                                                  "content": "저는 호감이 생기면",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "티가 나는 편이에요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "살짝 숨기는 편이에요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 2,
                                                  "type": "BINARY",
                                                  "content": "애프터 신청은",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "제가 먼저 하는 편이에요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "상대가 해주면 좋아요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 3,
                                                  "type": "BINARY",
                                                  "content": "저는 상대방 연락이 뜸해지면",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "먼저 연락하는 편이에요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "조금 기다리는 편이에요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 4,
                                                  "type": "BINARY",
                                                  "content": "소개팅에서 더 끌리는 건",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "설레는 분위기에요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "편안한 분위기에요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 5,
                                                  "type": "DISCUSSION",
                                                  "content": "애인 게임 닉네임이 전애인 이름이라면?",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "괜찮아요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "바로 바꿔줬으면 해요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 6,
                                                  "type": "DISCUSSION",
                                                  "content": "내가 먼저 들어간 뒤, 애인과 친구가 둘이 술을 마신다면?",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "괜찮아요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "조금 신경 쓰여요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 7,
                                                  "type": "DISCUSSION",
                                                  "content": "남녀 사이에 친구는",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "있을 수 있어요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "어렵다고 봐요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 8,
                                                  "type": "DISCUSSION",
                                                  "content": "이성친구 간섭은",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "안 했으면 해요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "안 하면 서운해요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 9,
                                                  "type": "BALANCE_GAME",
                                                  "content": "연락하는 텀은 차라리",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "6시간에 한 번"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "10분에 한 번"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 10,
                                                  "type": "BALANCE_GAME",
                                                  "content": "데이트 스타일은",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "밖에서 데이트"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "집에서 데이트"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 11,
                                                  "type": "BALANCE_GAME",
                                                  "content": "싸운 뒤에는",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "바로 푸는 편이에요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "조금 있다 푸는 편이에요"
                                                    }
                                                  ]
                                                },
                                                {
                                                  "id": 12,
                                                  "type": "BALANCE_GAME",
                                                  "content": "힘든 일이 생기면",
                                                  "answers": [
                                                    {
                                                      "answer_id": 1,
                                                      "content": "바로 공유해요"
                                                    },
                                                    {
                                                      "answer_id": 2,
                                                      "content": "혼자 정리해요"
                                                    }
                                                  ]
                                                }
                                              ],
                                              "short_questions": [
                                                {
                                                  "id": 1,
                                                  "type": "BLANK",
                                                  "content": "나는 자주 이런 말을 들어요 너는 진짜 ______ 같아"
                                                },
                                                {
                                                  "id": 2,
                                                  "type": "BLANK",
                                                  "content": "내가 생각하는 인연은 ______ 같아요"
                                                },
                                                {
                                                  "id": 3,
                                                  "type": "BLANK",
                                                  "content": "다시 태어나도 ______ 하고 싶어요"
                                                },
                                                {
                                                  "id": 4,
                                                  "type": "BLANK",
                                                  "content": "꼭 만나고 싶은 사람은 ______ 같은 사람이에요"
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
    ResponseEntity<CustomQuestionResponse> getCustomQuestionResponse();
}
package org.swyp.com.backend.interest.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.swyp.com.backend.interest.dto.InterestResponse;

@Tag(name = "Interest", description = "관심사 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface InterestControllerApiSpec {
    @Operation(
            summary = "관심사 목록 조회",
            description = "사용자가 선택 가능한 관심사 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "관심사 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InterestResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "interests": [
                                                { "type": "TRAVEL", "label": "여행" },
                                                { "type": "SPORTS", "label": "운동" },
                                                { "type": "MUSIC", "label": "음악" },
                                                { "type": "VIDEO", "label": "유튜브" },
                                                { "type": "RESTAURANT", "label": "맛집탐방" },
                                                { "type": "CAFE", "label": "카페투어" },
                                                { "type": "CULTURE", "label": "문화생활" },
                                                { "type": "READING", "label": "독서" },
                                                { "type": "GAME", "label": "게임" },
                                                { "type": "SELF_DEVELOPMENT", "label": "자기계발" },
                                                { "type": "INVESTING", "label": "재테크" },
                                                { "type": "MOVIE", "label": "영화감상" }
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
    ResponseEntity<InterestResponse> getInterestList();
}

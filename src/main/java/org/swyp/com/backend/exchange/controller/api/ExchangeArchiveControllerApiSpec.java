package org.swyp.com.backend.exchange.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.exchange.dto.ExchangeCardListResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDeleteRequest;
import org.swyp.com.backend.exchange.dto.ExchangeDeleteResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDetailResponse;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.RegionDetail;

@Tag(name = "Exchange Archive", description = "교환한 프로필(보관함) 조회/삭제 API")
@SecurityRequirement(name = "bearerAuth")
public interface ExchangeArchiveControllerApiSpec {

    @Operation(
            summary = "교환한 프로필 목록 조회",
            description = "커서 기반 페이징으로 내가 교환 완료한 상대방 프로필 카드 목록을 조회합니다. "
                    + "검색어/지역/유형/정렬방향은 모두 선택 파라미터이며 조합 가능합니다.",
            operationId = "getArchiveList"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "exchanges": [
                                                {
                                                  "exchange_id": 1,
                                                  "nickname": "홍길동",
                                                  "cosmic_type": "GALAXY",
                                                  "cosmic_type_image_key": "cosmic/galaxy.png",
                                                  "interests": [{"type": "TRAVEL", "label": "여행"}],
                                                  "bio": "여행과 운동을 좋아해요",
                                                  "matched_interests": [{"type": "TRAVEL", "label": "여행"}],
                                                  "memo": null,
                                                  "score": null,
                                                  "exchanged_at": "2026-07-02T18:30:26.371178"
                                                }
                                              ],
                                              "total_count": 1,
                                              "next_cursor": null
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
                                              "detail": "잘못된 커서 값입니다."
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
    ResponseEntity<ExchangeCardListResponse> getArchiveList(
            UserDetails userDetails,
            @Parameter(description = "닉네임 검색어") String keyword,
            @Parameter(description = "지역 필터 (다중 선택)") List<RegionDetail> regions,
            @Parameter(description = "유형 필터 (다중 선택)") List<CosmicDatingType> types,
            @Parameter(description = "정렬 방향") ExchangeSortDirection sort,
            @Parameter(description = "다음 페이지 커서") String cursor,
            @Parameter(description = "페이지 크기 (1~50)") int size
    );

    @Operation(
            summary = "교환한 프로필 상세 조회",
            description = "보관함 항목(exchange_id) 하나의 상세 정보를 조회합니다. "
                    + "목록보다 풍부한 정보(TMI, 키워드, 매칭 당시 내 프로필 등)를 포함합니다.",
            operationId = "getArchiveDetail"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상세 조회 성공"
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
                    description = "존재하지 않거나 본인 소유가 아닌 교환 정보",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "NOT_FOUND",
                                              "status": 404,
                                              "detail": "교환 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ExchangeDetailResponse> getArchiveDetail(
            UserDetails userDetails,
            @Parameter(description = "보관함 항목 ID", required = true, example = "1") Long exchangeId
    );

    @Operation(
            summary = "교환한 프로필 다건 삭제",
            description = "내 보관함에서 선택한 항목들을 하드 delete로 제거합니다. "
                    + "본인 소유가 아닌 id는 무시되고, 실제로 삭제된 개수/id만 응답됩니다. 상대방의 보관함에는 영향이 없습니다.",
            operationId = "deleteArchives"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 처리 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "deleted_count": 2,
                                              "deleted_ids": [1, 2]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "삭제할 항목을 선택하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "BAD_REQUEST",
                                              "status": 400,
                                              "detail": "삭제할 항목을 선택해주세요."
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
    ResponseEntity<ExchangeDeleteResponse> deleteArchives(
            UserDetails userDetails,
            ExchangeDeleteRequest request
    );
}

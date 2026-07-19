package org.swyp.com.backend.block.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.block.dto.BlockCreateRequest;
import org.swyp.com.backend.block.dto.BlockResponse;

@Tag(name = "Block", description = "프로필 차단 API")
@SecurityRequirement(name = "bearerAuth")
public interface BlockControllerApiSpec {

    @Operation(
            summary = "프로필 차단",
            description = """
                    교환한 프로필 보관함 상세 화면(`GET /exchange/archive/{exchangeId}`)에서 상대방 프로필을 차단할 때 사용합니다.
                    
                    **연동 순서**
                    1. 보관함 상세 화면에 진입할 때 이미 가지고 있는 `exchange_id` 값을 그대로 `profile_exchange_id`로 전달합니다.
                    2. 응답의 `block_id`를 차단 해제(`DELETE /blocks/{blockId}`)에 사용합니다.
                    
                    **동작 방식**
                    - 차단 대상 유저는 서버가 `profile_exchange_id`로부터 내부적으로 판별합니다. 상대방의 회원 ID를 앱에서 별도로 알거나 전달할 필요가 없습니다.
                    - 차단해도 내 보관함 목록(`GET /exchange/archive`)에서는 그 상대방 항목이 계속 보입니다. 다만 응답의 `is_blocked`가 `true`로, `block_id`가 이번 차단의 ID로 내려가 보관함 화면에서 바로 차단 해제(`DELETE /blocks/{blockId}`)할 수 있습니다. 실제 보관함 데이터는 삭제되지 않습니다.
                    - 반대로 상대방(피차단자)의 보관함에서는 나와의 항목이 보이지 않게 됩니다. 차단 사실이 어떤 방식으로도 노출되지 않고, 자신의 보관함 목록에서 내가 조용히 사라지는 것과 동일하게 보입니다.
                    - 같은 대상을 이미 차단한 상태에서 다시 요청해도 에러 없이 기존 차단 항목을 그대로 반환합니다(중복 생성 없음).
                    - 이번 범위에서는 차단 이후 두 사용자 사이에 새로운 교환(매칭)이 성사되는 것까지 막지는 않습니다.
                    """,
            operationId = "createBlock"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "차단 성공(이미 차단 중이었다면 기존 항목 반환)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "block_id": 1,
                                              "nickname": "달빛여우",
                                              "image_url": "https://cdn.orbitss.xyz/cosmic/fox.png?...",
                                              "created_at": "2026-07-09T18:30:26.371178"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
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
                                              "detail": "차단할 대상을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<BlockResponse> createBlock(UserDetails userDetails, BlockCreateRequest request);

    @Operation(
            summary = "차단 목록 조회",
            description = "내가 차단한 사용자 목록을 차단한 순서(최신순)로 조회합니다. 상대방 프로필이 이미 삭제된 경우 `nickname`/`image_url`은 `null`로 내려갑니다.",
            operationId = "getBlockList"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<BlockResponse>> getBlockList(UserDetails userDetails);

    @Operation(
            summary = "차단 해제",
            description = """
                    차단을 해제합니다. 차단 시 실제 보관함 데이터는 삭제되지 않았으므로, \
                    해제 즉시 상대방의 보관함 목록에도 원래 항목이 그대로 다시 노출됩니다(내 보관함에서는 애초에 계속 보이고 있었으며, `is_blocked`만 `false`로 바뀝니다).
                    """,
            operationId = "deleteBlock"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "차단 해제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 본인 소유가 아닌 차단 항목",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "NOT_FOUND",
                                              "status": 404,
                                              "detail": "차단 내역을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<Void> deleteBlock(UserDetails userDetails, Long blockId);
}

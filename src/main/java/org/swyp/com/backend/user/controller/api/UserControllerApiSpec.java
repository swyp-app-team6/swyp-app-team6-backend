package org.swyp.com.backend.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.user.dto.UserMeResponse;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserControllerApiSpec {

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인된 사용자의 기본 정보(ID, 이메일, 역할, OAuth 제공자)를 반환합니다."
                    + "구글 로그인 주소 참고는 /oauth2/authorization/google",
            operationId = "getMe"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "email": "user@example.com",
                                              "role": "USER",
                                              "provider": "GOOGLE"
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
    ResponseEntity<UserMeResponse> me(UserDetails userDetails);

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인된 사용자의 계정을 삭제합니다."
                    + "사용자와 연관된 프로필 및 관심사 정보도 함께 삭제됩니다.",
            operationId = "deleteUser"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "회원 탈퇴 성공"
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
            )
    })
    ResponseEntity<Void> deleteUser(UserDetails userDetails);
}

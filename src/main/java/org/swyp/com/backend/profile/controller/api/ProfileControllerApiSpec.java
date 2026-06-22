package org.swyp.com.backend.profile.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;

@Tag(name = "Profile", description = "프로필 관련 API")
@SecurityRequirement(name = "bearerAuth")
public interface ProfileControllerApiSpec {

    @Operation(
            summary = "프로필 등록",
            description = "현재 로그인된 사용자의 프로필을 등록합니다. "
                    + "닉네임, 성별, 프로필 이미지, 소개, 키워드, 주제, 관심사를 저장합니다.",
            operationId = "registerProfile"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": 1,
                                              "nickname": "홍길동",
                                              "image_key": "profile/user-uuid",
                                              "gender": "M",
                                              "bio": "여행을 좋아해요",
                                              "keyword": "여행",
                                              "topic": "맛집",
                                              "interests": [
                                                "TRAVEL",
                                                "FOOD"
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
                                              "detail": "인증 정보가 유효하지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<MyProfileResponse> registerProfile(
            UserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "프로필 등록 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "nickname": "홍길동",
                                              "gender": "M",
                                              "image_key": "profile/user-uuid",
                                              "bio": "여행을 좋아해요",
                                              "keyword": "여행",
                                              "topic": "맛집",
                                              "interests": [
                                                "TRAVEL",
                                                "FOOD"
                                              ]
                                            }
                                            """
                            )
                    )
            )
            ProfileRegisterRequest registerRequest
    );
}

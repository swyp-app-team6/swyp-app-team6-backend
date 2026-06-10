package org.swyp.com.backend.login.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.swyp.com.backend.login.dto.LoginRequest;
import org.swyp.com.backend.login.dto.RefreshTokenRequest;
import org.swyp.com.backend.login.dto.TokenResponse;

public interface LoginApiSpec {

    @Operation(
            summary = "로그인 처리",
            operationId = "Login"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "JWT 토큰 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
                                              "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "로그인 준비 세션 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "FORBIDDEN",
                                              "status": 403,
                                              "detail": "세션 정보가 유효하지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<TokenResponse> login(LoginRequest request);

    @Operation(
            summary = "토큰 재발급 처리",
            operationId = "Refresh"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "JWT 토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
                                              "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "토큰 유효하지 않음",
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
    ResponseEntity<TokenResponse> refresh(RefreshTokenRequest refreshToken);

}

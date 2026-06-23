package org.swyp.com.backend.global.auth.jwt.contorller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.swyp.com.backend.global.auth.jwt.dto.RefreshTokenRequest;
import org.swyp.com.backend.global.auth.jwt.dto.TokenResponse;

public interface TokenApiSpec {

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
                                              "detail": "인증이 필요합니다."
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<TokenResponse> refresh(RefreshTokenRequest refreshToken);

}

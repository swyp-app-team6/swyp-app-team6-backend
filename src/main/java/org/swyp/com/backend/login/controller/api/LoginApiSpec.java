package org.swyp.com.backend.login.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.swyp.com.backend.login.dto.LoginRequest;

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
                                              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
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
    ResponseEntity<Void> login(LoginRequest request);

}

package org.swyp.com.backend.auth.oauth.apple.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleLoginRequest;

@Tag(name = "App Apple SSO", description = "앱(Android/iOS) 전용 Apple 소셜 로그인")
public interface AppleAuthApiSpec {

    @Operation(
            summary = "앱 Apple 로그인",
            description = "Android/iOS 클라이언트에서 Apple SDK로 발급받은 identityToken을 검증하고 서비스 토큰을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 identityToken"),
                    @ApiResponse(responseCode = "503", description = "Apple 서버 연결 실패")
            }
    )
    ResponseEntity<TokenResponse> appleAppLogin(@Valid @RequestBody AppleLoginRequest request);
}

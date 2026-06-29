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
            description = """
                    Android/iOS 클라이언트에서 Apple SDK 로그인 후 발급받은 identityToken을 전달하면 서비스 자체 토큰을 반환합니다.
                    
                    **연동 순서**
                    1. 앱에서 Apple SDK로 로그인
                    2. 로그인 결과에서 `identityToken` 추출
                    3. 이 엔드포인트에 POST
                    4. 응답의 `accessToken`을 이후 모든 API 요청 헤더에 포함: `Authorization: Bearer <accessToken>`
                    5. `accessToken` 만료 시 `POST /auth/refresh`에 `refreshToken` 전달하여 재발급
                    
                    **주의사항**
                    - `identityToken`은 발급 후 **5분** 이내에 전달해야 합니다.
                    - 동일한 `identityToken`은 재사용할 수 없습니다 (Apple 정책).
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 identityToken"),
                    @ApiResponse(responseCode = "503", description = "Apple 서버 연결 실패")
            }
    )
    ResponseEntity<TokenResponse> appleAppLogin(@Valid @RequestBody AppleLoginRequest request);
}

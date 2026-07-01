package org.swyp.com.backend.auth.oauth.apple.controller.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleLoginRequest;

@Tag(name = "App Apple SSO", description = "앱(Android/iOS) 전용 Apple 소셜 로그인")
public interface AppleAuthApiSpec {

    @Operation(
            summary = "Apple 웹 로그인 (테스트용)",
            description = """
                    `https://api.orbitss.xyz/auth/apple`
                    브라우저에서 직접 접속하면 Apple 로그인 페이지로 리다이렉트됩니다.
                    로그인 완료 후 서비스 자체 토큰(`accessToken`, `refreshToken`)이 JSON으로 반환됩니다.
                    
                    **테스트 순서**
                    1. 브라우저 주소창에 이 엔드포인트 URL을 직접 입력하여 접속
                    2. Apple 계정으로 로그인
                    3. 응답 JSON에서 `accessToken` 복사
                    4. Swagger UI 우측 상단 **Authorize** → 토큰 입력
                    5. `POST /auth/apple/token` 또는 `GET /users/me` 등으로 인증 확인
                    """,
            responses = @ApiResponse(responseCode = "302", description = "Apple 로그인 페이지로 리다이렉트")
    )
    void redirectToApple(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "앱 Apple 로그인",
            description = """
                    Android/iOS 클라이언트에서 Apple SDK 로그인 후 발급받은 identityToken을 전달하면 서비스 자체 토큰을 반환합니다.
                    
                    **연동 순서**
                    1. 앱에서 Apple SDK로 로그인
                    2. 로그인 결과에서 `identityToken`, `authorizationCode` 추출
                    3. 이 엔드포인트에 POST (`authorizationCode`는 회원탈퇴 시 Apple 계정 연결 해제에 필요하므로 함께 전달 권장)
                    4. 응답의 `accessToken`을 이후 모든 API 요청 헤더에 포함: `Authorization: Bearer <accessToken>`
                    5. `accessToken` 만료 시 `POST /auth/refresh`에 `refreshToken` 전달하여 재발급

                    **주의사항**
                    - `identityToken`은 발급 후 **5분** 이내에 전달해야 합니다.
                    - 동일한 `identityToken`은 재사용할 수 없습니다 (Apple 정책).
                    - `authorizationCode`가 없으면 로그인은 정상 처리되지만, 이후 회원탈퇴 시 Apple 서버 쪽 연결 해제(revoke)는 수행되지 않습니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 identityToken"),
                    @ApiResponse(responseCode = "503", description = "Apple 서버 연결 실패")
            }
    )
    ResponseEntity<TokenResponse> appleAppLogin(@Valid @RequestBody AppleLoginRequest request);

    @Hidden
    ResponseEntity<TokenResponse> callback(
            @RequestParam String code,
            @RequestParam(required = false) String id_token,
            @RequestParam(required = false) String state);
}

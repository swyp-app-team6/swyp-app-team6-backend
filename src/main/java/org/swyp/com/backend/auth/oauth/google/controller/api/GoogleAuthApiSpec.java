package org.swyp.com.backend.auth.oauth.google.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.swyp.com.backend.auth.oauth.common.SsoLoginResponse;
import org.swyp.com.backend.auth.oauth.google.dto.GoogleLoginRequest;

@Tag(name = "App Google SSO", description = "앱(Android/iOS) 전용 Google 소셜 로그인")
public interface GoogleAuthApiSpec {

    @Operation(
            summary = "앱 Google 로그인",
            description = "Android/iOS 클라이언트에서 발급받은 Google idToken을 검증하고 서비스 토큰을 반환합니다. "
                    + "`requires_terms_agreement`가 true이면 필수 약관에 아직 동의하지 않은 상태이므로 "
                    + "`GET /terms`, `POST /terms/agreements`로 동의 화면을 먼저 처리해야 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = SsoLoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 idToken"),
                    @ApiResponse(responseCode = "503", description = "Google 서버 연결 실패")
            }
    )
    ResponseEntity<SsoLoginResponse> googleAppLogin(@Valid @RequestBody GoogleLoginRequest request);
}

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
            description = """
                    Android/iOS 클라이언트에서 발급받은 Google idToken을 검증하고, 최초 로그인이면 회원가입까지 함께 처리한 뒤 서비스 자체 토큰을 반환합니다.

                    **연동 순서**
                    1. 앱에서 Google SDK로 로그인 후 `idToken`을 추출해 이 엔드포인트에 POST 합니다(최초 로그인/기존 로그인 여부를 앱이 미리 구분할 필요 없이 동일하게 호출).
                    2. 응답의 `access_token`을 이후 모든 API 요청 헤더에 포함: `Authorization: Bearer <access_token>`.
                    3. `access_token` 만료 시 `POST /auth/refresh`에 `refresh_token`을 전달하여 재발급.
                    4. **응답의 `requires_terms_agreement`가 `true`이면, 다른 화면으로 넘어가기 전에 반드시 약관 동의 화면을 먼저 띄워야 합니다.** \
                    `GET /terms`로 목록을 받아 화면을 그리고, 사용자가 필수 항목에 모두 동의하면 `POST /terms/agreements`를 호출하세요. \
                    (백엔드는 이 플래그로 다른 API 호출 자체를 막지 않는 소프트 게이트이므로, 화면 전환 제어는 앱이 책임집니다.)
                    5. `requires_terms_agreement`가 `false`이면 이미 필수 약관에 최신 버전으로 동의된 사용자이므로 약관 화면 없이 바로 다음 화면으로 진입하면 됩니다.

                    **주의사항**
                    - 이 플래그는 "방금 가입했는지"가 아니라 "현재 필수 약관에 전부 최신 버전으로 동의했는지"를 매 로그인마다 다시 계산한 값입니다. \
                    따라서 온보딩 중 약관 동의를 마치지 못하고 앱이 종료된 사용자는 재로그인해도 다시 `true`가 내려오고, \
                    약관이 개정되면 기존에 동의했던 사용자도 다음 로그인부터 다시 `true`가 내려와 재동의 화면이 노출됩니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = SsoLoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 idToken"),
                    @ApiResponse(responseCode = "503", description = "Google 서버 연결 실패")
            }
    )
    ResponseEntity<SsoLoginResponse> googleAppLogin(@Valid @RequestBody GoogleLoginRequest request);
}

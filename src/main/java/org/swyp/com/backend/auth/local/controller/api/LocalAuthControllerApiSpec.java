package org.swyp.com.backend.auth.local.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.swyp.com.backend.auth.local.dto.LocalLoginRequest;
import org.swyp.com.backend.auth.local.dto.LocalSignupRequest;
import org.swyp.com.backend.auth.oauth.common.SsoLoginResponse;

@Tag(name = "일반 회원가입/로그인", description = "아이디(이메일 형식 강제 없음)+비밀번호 기반 회원가입/로그인")
public interface LocalAuthControllerApiSpec {

    @Operation(
            summary = "일반 회원가입",
            description = """
                    아이디+비밀번호로 신규 계정을 생성하고, 가입과 동시에 서비스 자체 토큰을 발급합니다. \
                    이메일 인증, 비밀번호 재설정 등은 지원하지 않는 최소 구현입니다. `email` 필드는 로그인 식별자로만 쓰이며 \
                    실제 이메일 형식일 필요는 없습니다.

                    **연동 순서**
                    1. 아이디/비밀번호를 입력받아 이 엔드포인트에 POST 합니다.
                    2. 응답의 `access_token`을 이후 모든 API 요청 헤더에 포함: `Authorization: Bearer <access_token>`.
                    3. `access_token` 만료 시 `POST /auth/refresh`에 `refresh_token`을 전달하여 재발급.
                    4. `requires_terms_agreement`가 `true`이면(가입 직후에는 항상 `true`), 다른 화면으로 넘어가기 전에 \
                    약관 동의 화면을 먼저 띄우세요. `GET /terms`로 목록을 받아 화면을 그리고, 필수 항목에 모두 동의하면 \
                    `POST /terms/agreements`를 호출합니다.

                    **주의사항**
                    - 이미 사용 중인 아이디로 가입을 시도하면 409가 반환됩니다. 이 아이디가 SSO로 가입된 계정인지 여부는 \
                    노출되지 않습니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "가입 및 로그인 성공",
                            content = @Content(schema = @Schema(implementation = SsoLoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 (아이디/비밀번호 누락, 비밀번호 길이 미달)"),
                    @ApiResponse(responseCode = "409", description = "이미 사용중인 아이디")
            }
    )
    ResponseEntity<SsoLoginResponse> signup(@Valid @RequestBody LocalSignupRequest request);

    @Operation(
            summary = "일반 로그인",
            description = """
                    가입된 아이디+비밀번호로 로그인하고 서비스 자체 토큰을 발급합니다.

                    **연동 순서**
                    1. 아이디/비밀번호를 입력받아 이 엔드포인트에 POST 합니다.
                    2. 응답의 `access_token`을 이후 모든 API 요청 헤더에 포함: `Authorization: Bearer <access_token>`.
                    3. `access_token` 만료 시 `POST /auth/refresh`에 `refresh_token`을 전달하여 재발급.
                    4. `requires_terms_agreement`가 `true`이면 다른 화면으로 넘어가기 전에 약관 동의 화면을 먼저 띄우세요 \
                    (상세는 회원가입 API 설명 참고).

                    **주의사항**
                    - 아이디가 존재하지 않는 경우, 비밀번호가 틀린 경우, 해당 아이디가 SSO로 가입되어 비밀번호 로그인이 \
                    불가능한 경우 모두 동일하게 401과 동일한 오류 메시지로 응답합니다(가입 경로 노출 방지).
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = SsoLoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "요청 값 검증 실패 (아이디/비밀번호 누락)"),
                    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 일치하지 않음")
            }
    )
    ResponseEntity<SsoLoginResponse> login(@Valid @RequestBody LocalLoginRequest request);
}

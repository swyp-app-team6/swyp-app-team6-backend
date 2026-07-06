package org.swyp.com.backend.auth.local.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.jwt.service.TokenService;
import org.swyp.com.backend.auth.local.controller.api.LocalAuthControllerApiSpec;
import org.swyp.com.backend.auth.local.dto.LocalLoginRequest;
import org.swyp.com.backend.auth.local.dto.LocalSignupRequest;
import org.swyp.com.backend.auth.local.service.LocalAuthService;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.auth.oauth.common.SsoLoginResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LocalAuthController implements LocalAuthControllerApiSpec {

    private final LocalAuthService localAuthService;
    private final TokenService tokenService;

    @Override
    @PostMapping("/signup")
    public ResponseEntity<SsoLoginResponse> signup(@Valid @RequestBody LocalSignupRequest request) {
        SocialAuthResult result = localAuthService.signup(request.email(), request.password());
        return ResponseEntity.ok(toResponse(result));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<SsoLoginResponse> login(@Valid @RequestBody LocalLoginRequest request) {
        SocialAuthResult result = localAuthService.login(request.email(), request.password());
        return ResponseEntity.ok(toResponse(result));
    }

    private SsoLoginResponse toResponse(SocialAuthResult result) {
        TokenResponse tokenResponse = tokenService.issueTokenPair(result.userId(), result.role());
        return new SsoLoginResponse(tokenResponse.accessToken(), tokenResponse.refreshToken(),
                result.requiresTermsAgreement());
    }
}

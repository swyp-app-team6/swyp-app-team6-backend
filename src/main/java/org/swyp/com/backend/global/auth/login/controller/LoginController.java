package org.swyp.com.backend.global.auth.login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.global.auth.dto.RefreshTokenRequest;
import org.swyp.com.backend.global.auth.dto.TokenResponse;
import org.swyp.com.backend.global.auth.login.controller.api.LoginApiSpec;
import org.swyp.com.backend.global.auth.login.service.LoginService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController implements LoginApiSpec {

    private final LoginService loginService;

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse token = loginService.refreshTokens(request.refreshToken());
        return ResponseEntity.ok(token);
    }
}


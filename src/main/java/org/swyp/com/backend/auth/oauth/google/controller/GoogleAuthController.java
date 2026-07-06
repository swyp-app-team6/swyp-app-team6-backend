package org.swyp.com.backend.auth.oauth.google.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.jwt.service.TokenService;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;
import org.swyp.com.backend.auth.oauth.common.SsoLoginResponse;
import org.swyp.com.backend.auth.oauth.google.controller.api.GoogleAuthApiSpec;
import org.swyp.com.backend.auth.oauth.google.dto.GoogleLoginRequest;
import org.swyp.com.backend.auth.oauth.google.service.GoogleAuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
public class GoogleAuthController implements GoogleAuthApiSpec {

    private final GoogleAuthService googleAuthService;
    private final TokenService tokenService;

    @Override
    @PostMapping("/app")
    public ResponseEntity<SsoLoginResponse> googleAppLogin(@Valid @RequestBody GoogleLoginRequest request) {
        SocialAuthResult result = googleAuthService.authenticate(request.idToken());
        TokenResponse tokenResponse = tokenService.issueTokenPair(result.userId(), result.role());
        SsoLoginResponse response = new SsoLoginResponse(tokenResponse.accessToken(), tokenResponse.refreshToken(),
                result.requiresTermsAgreement());
        return ResponseEntity.ok(response);
    }
}

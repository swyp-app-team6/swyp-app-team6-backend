package org.swyp.com.backend.auth.oauth.apple.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.jwt.service.TokenService;
import org.swyp.com.backend.auth.oauth.apple.config.AppleProperties;
import org.swyp.com.backend.auth.oauth.apple.controller.api.AppleAuthApiSpec;
import org.swyp.com.backend.auth.oauth.apple.dto.AppleLoginRequest;
import org.swyp.com.backend.auth.oauth.apple.service.AppleAuthService;
import org.swyp.com.backend.auth.oauth.common.SocialAuthResult;

@RestController
@RequestMapping("/auth/apple")
@RequiredArgsConstructor
public class AppleAuthController implements AppleAuthApiSpec {

    private final AppleAuthService appleAuthService;
    private final AppleProperties appleProperties;
    private final TokenService tokenService;

    @Override
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> appleAppLogin(@Valid @RequestBody AppleLoginRequest request) {
        SocialAuthResult result = appleAuthService.loginWithIdentityToken(request.identityToken());
        TokenResponse tokenResponse = tokenService.issueTokenPair(result.userId(), result.role());
        return ResponseEntity.ok(tokenResponse);
    }

    @Override
    @GetMapping
    public void redirectToApple(HttpServletResponse response) throws IOException {
        String state = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        String url = "https://appleid.apple.com/auth/authorize" +
                "?client_id=" + appleProperties.getClientId() +
                "&redirect_uri=" + appleProperties.getRedirectUri() +
                "&response_type=code id_token" +
                "&response_mode=form_post" +
                "&scope=name email" +
                "&state=" + state;
        response.sendRedirect(url);
    }

    @Override
    @PostMapping("/callback")
    public ResponseEntity<TokenResponse> callback(
            @RequestParam String code,
            @RequestParam(required = false) String id_token,
            @RequestParam(required = false) String state) {

        SocialAuthResult result;
        if (id_token != null && !id_token.isBlank()) {
            result = appleAuthService.loginWithIdentityToken(id_token);
        } else {
            result = appleAuthService.loginWithCode(code);
        }

        TokenResponse tokenResponse = tokenService.issueTokenPair(result.userId(), result.role());
        return ResponseEntity.ok(tokenResponse);
    }
}

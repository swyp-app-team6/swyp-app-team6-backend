package org.swyp.com.backend.auth.oauth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.jwt.service.TokenService;
import org.swyp.com.backend.auth.oauth.controller.api.GoogleAppLoginApiSpec;
import org.swyp.com.backend.auth.oauth.dto.GoogleAppLoginRequest;
import org.swyp.com.backend.auth.oauth.dto.GoogleAuthResult;
import org.swyp.com.backend.auth.oauth.service.GoogleAppLoginService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google")
public class GoogleAppLoginController implements GoogleAppLoginApiSpec {

    private final GoogleAppLoginService googleAppLoginService;
    private final TokenService tokenService;

    @Override
    @PostMapping("/app")
    public ResponseEntity<TokenResponse> googleAppLogin(@Valid @RequestBody GoogleAppLoginRequest request) {
        GoogleAuthResult authResult = googleAppLoginService.authenticate(request.idToken());
        TokenResponse tokenResponse = tokenService.issueTokenPair(authResult.userId(), authResult.role());
        return ResponseEntity.ok(tokenResponse);
    }
}

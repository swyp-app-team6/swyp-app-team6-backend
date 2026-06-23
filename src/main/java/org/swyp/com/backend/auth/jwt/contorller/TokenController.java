package org.swyp.com.backend.auth.jwt.contorller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.auth.jwt.contorller.api.TokenApiSpec;
import org.swyp.com.backend.auth.jwt.dto.RefreshTokenRequest;
import org.swyp.com.backend.auth.jwt.dto.TokenResponse;
import org.swyp.com.backend.auth.jwt.service.TokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenController implements TokenApiSpec {

    private final TokenService tokenService;

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = tokenService.reissueTokenPair(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}

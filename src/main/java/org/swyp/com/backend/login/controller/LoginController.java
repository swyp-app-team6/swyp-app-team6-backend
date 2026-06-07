package org.swyp.com.backend.login.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.login.controller.api.LoginApiSpec;
import org.swyp.com.backend.login.dto.LoginRequest;
import org.swyp.com.backend.login.dto.TokenResponse;
import org.swyp.com.backend.login.service.LoginService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController implements LoginApiSpec {
    private final LoginService loginService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info(request.email(), request.password());
        TokenResponse token = loginService.login(request.email(), request.password());
        return ResponseEntity.ok(token);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@AuthenticationPrincipal UserDetails userDetails) {
        String accountId = userDetails.getUsername();
        List<UserRole> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(UserRole::valueOf)
                .toList();

        TokenResponse token = loginService.refreshTokens(accountId, roles);
        return ResponseEntity.ok(token);
    }
}


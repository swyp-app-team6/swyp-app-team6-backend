package org.swyp.com.backend.login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.login.controller.api.LoginApiSpec;
import org.swyp.com.backend.login.dto.LoginRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController implements LoginApiSpec {

    @Override
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        System.out.println(request.id());
        return ResponseEntity.ok().build();
    }
}


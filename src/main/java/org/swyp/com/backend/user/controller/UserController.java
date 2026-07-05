package org.swyp.com.backend.user.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.user.controller.api.UserControllerApiSpec;
import org.swyp.com.backend.user.dto.UserMeResponse;
import org.swyp.com.backend.user.dto.UserWithdrawalRequest;
import org.swyp.com.backend.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
public class UserController implements UserControllerApiSpec {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserMeResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(userService.getMe(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails userDetails,
                                            @Valid @RequestBody UserWithdrawalRequest request) {
        userService.deleteUser(Long.valueOf(userDetails.getUsername()), request.reasonCode(), request.reasonDetail());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}

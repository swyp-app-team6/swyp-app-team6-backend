package org.swyp.com.backend.profile.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.profile.controller.api.ProfileControllerApiSpec;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController implements ProfileControllerApiSpec {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<MyProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        MyProfileResponse response = profileService.getMyProfile(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MyProfileResponse> registerProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                             @Valid @RequestBody ProfileRegisterRequest registerRequest) {
        MyProfileResponse response = profileService.createProfile(Long.valueOf(userDetails.getUsername()),
                registerRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<MyProfileResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                           @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        MyProfileResponse response = profileService.updateProfile(Long.valueOf(userDetails.getUsername()),
                profileUpdateRequest);
        return ResponseEntity.ok(response);
    }
}

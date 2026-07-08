package org.swyp.com.backend.profile.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.profile.controller.api.ProfileControllerApiSpec;
import org.swyp.com.backend.profile.dto.ProfileCosmicUpdateRequest;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.QrResponse;
import org.swyp.com.backend.profile.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController implements ProfileControllerApiSpec {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ProfileResponse response = profileService.getProfileResponseByUserId(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> registerProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                           @Valid @RequestBody ProfileRegisterRequest registerRequest) {
        ProfileResponse response = profileService.createProfile(Long.valueOf(userDetails.getUsername()),
                registerRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<ProfileResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                         @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        ProfileResponse response = profileService.updateProfile(Long.valueOf(userDetails.getUsername()),
                profileUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cosmic")
    public ResponseEntity<Void> updateProfileCosmic(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ProfileCosmicUpdateRequest profileCosmicUpdateRequest) {
        profileService.updateProfileCosmic(Long.valueOf(userDetails.getUsername()), profileCosmicUpdateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserDetails userDetails) {
        profileService.deleteProfile(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/qr")
    public ResponseEntity<QrResponse> getQrUUID(@AuthenticationPrincipal UserDetails userDetails) {
        QrResponse qrResponse = profileService.getQrUUID(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.ok(qrResponse);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable("uuid") UUID uuid,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        ProfileResponse profileResponse = profileService.getProfileResponseByUUID(uuid);
        return ResponseEntity.ok(profileResponse);
    }

}

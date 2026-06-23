package org.swyp.com.backend.upload.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.upload.controller.api.UploadControllerApiSpec;
import org.swyp.com.backend.upload.dto.PresignedUploadResponse;
import org.swyp.com.backend.upload.service.UploadService;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController implements UploadControllerApiSpec {

    private final UploadService uploadService;

    @PostMapping("/presign")
    public ResponseEntity<PresignedUploadResponse> presign(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "image/jpeg") String contentType
    ) {
        return ResponseEntity.ok(uploadService.createPresignedUploadUrl(userDetails.getUsername(), contentType));
    }
}

package org.swyp.com.backend.region.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.region.dto.RegionResponse;
import org.swyp.com.backend.region.service.RegionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/region")
@SecurityRequirement(name = "bearerAuth")
public class RegionController {
    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<RegionResponse> getRegionList() {
        RegionResponse response = regionService.getRegionResponse();
        return ResponseEntity.ok(response);
    }
}

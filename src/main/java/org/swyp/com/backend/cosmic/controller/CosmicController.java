package org.swyp.com.backend.cosmic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.cosmic.controller.api.CosmicControllerApiSpec;
import org.swyp.com.backend.cosmic.dto.CosmicTestResponse;
import org.swyp.com.backend.cosmic.service.CosmicService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cosmic")
public class CosmicController implements CosmicControllerApiSpec {
    private final CosmicService cosmicService;

    @GetMapping("/test")
    public ResponseEntity<CosmicTestResponse> getCosmicTestResponse() {
        CosmicTestResponse cosmicTestResponse = cosmicService.getCosmicTestResponse();
        return ResponseEntity.ok(cosmicTestResponse);
    }
}

package org.swyp.com.backend.interest.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.interest.controller.api.InterestControllerApiSpec;
import org.swyp.com.backend.interest.dto.InterestResponse;
import org.swyp.com.backend.interest.service.InterestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interests")
@SecurityRequirement(name = "bearerAuth")
public class InterestController implements InterestControllerApiSpec {
    private final InterestService interestService;

    @GetMapping
    public ResponseEntity<InterestResponse> getInterestList() {
        InterestResponse response = interestService.getInterestResponse();
        return ResponseEntity.ok(response);
    }
}

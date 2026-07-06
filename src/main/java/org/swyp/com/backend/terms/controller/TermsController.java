package org.swyp.com.backend.terms.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.terms.controller.api.TermsControllerApiSpec;
import org.swyp.com.backend.terms.dto.TermsAgreementRequest;
import org.swyp.com.backend.terms.dto.TermsAgreementResponse;
import org.swyp.com.backend.terms.dto.TermsListResponse;
import org.swyp.com.backend.terms.service.TermsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
@SecurityRequirement(name = "bearerAuth")
public class TermsController implements TermsControllerApiSpec {

    private final TermsService termsService;

    @GetMapping
    public ResponseEntity<TermsListResponse> getTermsList() {
        return ResponseEntity.ok(termsService.getTermsList());
    }

    @PostMapping("/agreements")
    public ResponseEntity<TermsAgreementResponse> agreeToTerms(@AuthenticationPrincipal UserDetails userDetails,
                                                                @Valid @RequestBody TermsAgreementRequest request) {
        Long userId = Long.valueOf(userDetails.getUsername());
        TermsAgreementResponse response = termsService.agreeToTerms(userId, request.agreedTypes());
        return ResponseEntity.ok(response);
    }
}

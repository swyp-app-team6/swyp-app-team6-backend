package org.swyp.com.backend.report.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.report.controller.api.ReportControllerApiSpec;
import org.swyp.com.backend.report.dto.ReportCreateRequest;
import org.swyp.com.backend.report.dto.ReportResponse;
import org.swyp.com.backend.report.service.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController implements ReportControllerApiSpec {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@AuthenticationPrincipal UserDetails userDetails,
                                                        @Valid @RequestBody ReportCreateRequest request) {
        Long reporterId = Long.valueOf(userDetails.getUsername());
        ReportResponse response = reportService.createReport(reporterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

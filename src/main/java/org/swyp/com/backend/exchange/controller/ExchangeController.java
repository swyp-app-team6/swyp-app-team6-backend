package org.swyp.com.backend.exchange.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.swyp.com.backend.exchange.controller.api.ExchangeControllerApiSpec;
import org.swyp.com.backend.exchange.dto.ExchangeResponse;
import org.swyp.com.backend.exchange.service.ExchangeService;
import org.swyp.com.backend.profile.dto.profile.ProfileResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ExchangeController implements ExchangeControllerApiSpec {
    private static final Long EXCHANGE_TIMEOUT = Duration.ofMinutes(1).toMillis();
    private final ExchangeService exchangeService;

    @GetMapping("/wait")
    public DeferredResult<ResponseEntity<ProfileResponse>> waitProfileResponse(
            @AuthenticationPrincipal UserDetails userDetails) {

        DeferredResult<ResponseEntity<ProfileResponse>> result = new DeferredResult<>(EXCHANGE_TIMEOUT);

        exchangeService.waitForProfileResponse(Long.valueOf(userDetails.getUsername()), result);

        result.onTimeout(() -> {
            result.setResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
        });

        result.onCompletion(() -> {
            exchangeService.removeProfileResponse(Long.valueOf(userDetails.getUsername()));
        });

        return result;
    }

    @GetMapping("/start/{uuid}")
    public DeferredResult<ResponseEntity<ExchangeResponse>> waitExchangeResponse(
            @PathVariable("uuid") UUID uuid,
            @AuthenticationPrincipal UserDetails userDetails) {

        DeferredResult<ResponseEntity<ExchangeResponse>> result = new DeferredResult<>(EXCHANGE_TIMEOUT);

        exchangeService.waitForExchangeResponse(Long.valueOf(userDetails.getUsername()), uuid, result);

        result.onTimeout(() -> {
            result.setResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
        });

        result.onCompletion(() -> {
            exchangeService.removeExchangeResponse(Long.valueOf(userDetails.getUsername()), uuid);
        });

        return result;
    }

    @GetMapping("/accept/{profile_id}")
    public ResponseEntity<ExchangeResponse> acceptExchange(
            @PathVariable("profile_id") Long profileId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                exchangeService.getExchangeResponse(Long.valueOf(userDetails.getUsername()), profileId));
    }

    @GetMapping("/decline/{profile_id}")
    public ResponseEntity<Void> declineExchange(
            @PathVariable("profile_id") Long profileId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        exchangeService.rejectExchange(Long.valueOf(userDetails.getUsername()), profileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/cancel")
    public ResponseEntity<Void> cancelExchangeWait(
            @AuthenticationPrincipal UserDetails userDetails) {
        exchangeService.cancelExchangeWait(Long.valueOf(userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/cancel/{profile_id}")
    public ResponseEntity<Void> cancelExchangeStart(
            @PathVariable("profile_id") Long profileId,
            @AuthenticationPrincipal UserDetails userDetails) {
        exchangeService.cancelExchangeStart(Long.valueOf(userDetails.getUsername()), profileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

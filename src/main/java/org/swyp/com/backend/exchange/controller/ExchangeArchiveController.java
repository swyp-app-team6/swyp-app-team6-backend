package org.swyp.com.backend.exchange.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.exchange.controller.api.ExchangeArchiveControllerApiSpec;
import org.swyp.com.backend.exchange.dto.ExchangeCardListResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDeleteRequest;
import org.swyp.com.backend.exchange.dto.ExchangeDeleteResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDetailResponse;
import org.swyp.com.backend.exchange.dto.ExchangeLikeRequest;
import org.swyp.com.backend.exchange.dto.ExchangeLikeResponse;
import org.swyp.com.backend.exchange.dto.ExchangeReviewRequest;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.exchange.service.ExchangeArchiveService;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.RegionDetail;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange/archive")
@SecurityRequirement(name = "bearerAuth")
public class ExchangeArchiveController implements ExchangeArchiveControllerApiSpec {

    private final ExchangeArchiveService exchangeArchiveService;

    @GetMapping
    public ResponseEntity<ExchangeCardListResponse> getArchiveList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<RegionDetail> regions,
            @RequestParam(required = false) List<CosmicDatingType> types,
            @RequestParam(required = false) Boolean liked,
            @RequestParam(defaultValue = "RECENT") ExchangeSortDirection sort,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = Long.valueOf(userDetails.getUsername());
        ExchangeCardListResponse response = exchangeArchiveService.getArchiveList(userId, keyword, regions, types,
                liked, sort, cursor, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{exchangeId}")
    public ResponseEntity<ExchangeDetailResponse> getArchiveDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long exchangeId) {

        Long userId = Long.valueOf(userDetails.getUsername());
        ExchangeDetailResponse response = exchangeArchiveService.getArchiveDetail(userId, exchangeId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{exchangeId}/review")
    public ResponseEntity<ExchangeDetailResponse> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long exchangeId,
            @Valid @RequestBody ExchangeReviewRequest request) {

        Long userId = Long.valueOf(userDetails.getUsername());
        ExchangeDetailResponse response = exchangeArchiveService.updateReview(userId, exchangeId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{exchangeId}/like")
    public ResponseEntity<ExchangeLikeResponse> updateLike(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long exchangeId,
            @Valid @RequestBody ExchangeLikeRequest request) {

        Long userId = Long.valueOf(userDetails.getUsername());
        ExchangeLikeResponse response = exchangeArchiveService.updateLiked(userId, exchangeId, request.liked());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ExchangeDeleteResponse> deleteArchives(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ExchangeDeleteRequest request) {

        Long userId = Long.valueOf(userDetails.getUsername());
        ExchangeDeleteResponse response = exchangeArchiveService.deleteArchives(userId, request.exchangeIds());
        return ResponseEntity.ok(response);
    }
}

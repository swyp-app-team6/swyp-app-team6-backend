package org.swyp.com.backend.block.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swyp.com.backend.block.controller.api.BlockControllerApiSpec;
import org.swyp.com.backend.block.dto.BlockCreateRequest;
import org.swyp.com.backend.block.dto.BlockResponse;
import org.swyp.com.backend.block.service.BlockService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blocks")
@SecurityRequirement(name = "bearerAuth")
public class BlockController implements BlockControllerApiSpec {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockResponse> createBlock(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody BlockCreateRequest request) {
        Long userId = Long.valueOf(userDetails.getUsername());
        BlockResponse response = blockService.createBlock(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BlockResponse>> getBlockList(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        return ResponseEntity.ok(blockService.getBlockList(userId));
    }

    @DeleteMapping("/{blockId}")
    public ResponseEntity<Void> deleteBlock(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long blockId) {
        Long userId = Long.valueOf(userDetails.getUsername());
        blockService.deleteBlock(userId, blockId);
        return ResponseEntity.noContent().build();
    }
}

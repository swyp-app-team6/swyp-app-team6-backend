package org.swyp.com.backend.block.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.block.domain.Block;
import org.swyp.com.backend.block.domain.repository.BlockRepository;
import org.swyp.com.backend.block.dto.BlockCreateRequest;
import org.swyp.com.backend.block.dto.BlockResponse;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.image.service.ImageUrlService;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.user.domain.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;
    private final ProfileExchangeRepository profileExchangeRepository;
    private final ProfileRepository profileRepository;
    private final ImageUrlService imageUrlService;

    @Transactional
    public BlockResponse createBlock(Long blockerId, BlockCreateRequest request) {
        ProfileExchange profileExchange = profileExchangeRepository
                .findByIdAndUserId(request.profileExchangeId(), blockerId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "차단할 대상을 찾을 수 없습니다."));

        User blockerUser = profileExchange.getUser();
        User blockedUser = profileExchange.getProfile().getUser();

        Block block = blockRepository.findByBlockerUserIdAndBlockedUserId(blockerUser.getId(), blockedUser.getId())
                .orElseGet(() -> blockRepository.save(Block.createBlock(blockerUser, blockedUser)));

        Profile blockedProfile = profileExchange.getProfile();
        return new BlockResponse(block.getId(), blockedProfile.getNickname(),
                imageUrlService.toSignedThumbnailUrl(blockedProfile.getImageKey()), block.getCreatedAt());
    }

    public List<BlockResponse> getBlockList(Long blockerId) {
        List<Block> blocks = blockRepository.findAllByBlockerUserIdOrderByCreatedAtDesc(blockerId);
        return blocks.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteBlock(Long blockerId, Long blockId) {
        Block block = blockRepository.findByIdAndBlockerUserId(blockId, blockerId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "차단 내역을 찾을 수 없습니다."));

        blockRepository.delete(block);
    }

    private BlockResponse toResponse(Block block) {
        Profile blockedProfile = profileRepository.findByUserAndDeletedFalse(block.getBlockedUser()).orElse(null);
        String nickname = blockedProfile != null ? blockedProfile.getNickname() : null;
        String imageUrl = blockedProfile != null ? imageUrlService.toSignedThumbnailUrl(blockedProfile.getImageKey())
                : null;
        return new BlockResponse(block.getId(), nickname, imageUrl, block.getCreatedAt());
    }
}

package org.swyp.com.backend.block.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_AGE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_BIO;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_COSMIC_TYPE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_JOB;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_ID;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_REGION_DETAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.swyp.com.backend.support.ExchangeTestFixture;
import org.swyp.com.backend.support.ProfileTestFixture;
import org.swyp.com.backend.support.UserTestFixture;
import org.swyp.com.backend.user.domain.User;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

    private static final Long TEST_PROFILE_EXCHANGE_ID = 10L;
    private static final Long TEST_BLOCKED_USER_ID = 2L;
    private static final Long TEST_BLOCK_ID = 100L;

    @Mock
    BlockRepository blockRepository;
    @Mock
    ProfileExchangeRepository profileExchangeRepository;
    @Mock
    ProfileRepository profileRepository;

    BlockService blockService;

    @BeforeEach
    void setUp() {
        ImageUrlService imageUrlService = new ImageUrlService() {
            @Override
            public String toSignedUrl(String key) {
                return key;
            }

            @Override
            public String toSignedMainUrl(String imageKey) {
                return imageKey;
            }

            @Override
            public String toSignedThumbnailUrl(String imageKey) {
                return imageKey;
            }
        };
        blockService = new BlockService(blockRepository, profileExchangeRepository, profileRepository,
                imageUrlService);
    }

    private User blockerUser() {
        return UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
    }

    private User blockedUser() {
        return UserTestFixture.createUser(TEST_BLOCKED_USER_ID, "other@example.com", TEST_ROLE);
    }

    private ProfileExchange givenProfileExchange() {
        User reporter = blockerUser();
        User reported = blockedUser();
        Profile profile = ProfileTestFixture.createProfile(TEST_PROFILE_ID, reported, TEST_PROFILE_NICKNAME,
                TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);
        return ExchangeTestFixture.createProfileExchange(TEST_PROFILE_EXCHANGE_ID, reporter, profile, null, null,
                null);
    }

    @Test
    void createBlock_newTarget_savesBlock() {
        // given
        ProfileExchange profileExchange = givenProfileExchange();
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.of(profileExchange));
        when(blockRepository.findByBlockerUserIdAndBlockedUserId(TEST_USER_ID, TEST_BLOCKED_USER_ID))
                .thenReturn(Optional.empty());
        when(blockRepository.save(any(Block.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlockCreateRequest request = new BlockCreateRequest(TEST_PROFILE_EXCHANGE_ID);

        // when
        BlockResponse response = blockService.createBlock(TEST_USER_ID, request);

        // then
        assertThat(response.nickname()).isEqualTo(TEST_PROFILE_NICKNAME);

        ArgumentCaptor<Block> captor = ArgumentCaptor.forClass(Block.class);
        verify(blockRepository).save(captor.capture());
        Block saved = captor.getValue();
        assertThat(saved.getBlockerUser().getId()).isEqualTo(TEST_USER_ID);
        assertThat(saved.getBlockedUser().getId()).isEqualTo(TEST_BLOCKED_USER_ID);
    }

    @Test
    void createBlock_alreadyBlocked_returnsExistingWithoutSaving() {
        // given
        ProfileExchange profileExchange = givenProfileExchange();
        Block existing = Block.createBlock(blockerUser(), blockedUser());
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.of(profileExchange));
        when(blockRepository.findByBlockerUserIdAndBlockedUserId(TEST_USER_ID, TEST_BLOCKED_USER_ID))
                .thenReturn(Optional.of(existing));

        BlockCreateRequest request = new BlockCreateRequest(TEST_PROFILE_EXCHANGE_ID);

        // when
        blockService.createBlock(TEST_USER_ID, request);

        // then
        verify(blockRepository, never()).save(any());
    }

    @Test
    void createBlock_profileExchangeNotFoundOrNotOwned_throwsNotFound() {
        when(profileExchangeRepository.findByIdAndUserId(TEST_PROFILE_EXCHANGE_ID, TEST_USER_ID))
                .thenReturn(Optional.empty());

        BlockCreateRequest request = new BlockCreateRequest(TEST_PROFILE_EXCHANGE_ID);

        assertThrows(BusinessException.class, () -> blockService.createBlock(TEST_USER_ID, request));
        verify(blockRepository, never()).save(any());
    }

    @Test
    void getBlockList_returnsBlockedProfiles() {
        // given
        Block block = Block.createBlock(blockerUser(), blockedUser());
        Profile blockedProfile = ProfileTestFixture.createProfile(TEST_PROFILE_ID, blockedUser(),
                TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO,
                TEST_COSMIC_TYPE);
        when(blockRepository.findAllByBlockerUserIdOrderByCreatedAtDesc(TEST_USER_ID)).thenReturn(List.of(block));
        when(profileRepository.findByUserAndDeletedFalse(block.getBlockedUser()))
                .thenReturn(Optional.of(blockedProfile));

        // when
        List<BlockResponse> responses = blockService.getBlockList(TEST_USER_ID);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).nickname()).isEqualTo(TEST_PROFILE_NICKNAME);
    }

    @Test
    void deleteBlock_owned_deletesBlock() {
        // given
        Block block = Block.createBlock(blockerUser(), blockedUser());
        when(blockRepository.findByIdAndBlockerUserId(TEST_BLOCK_ID, TEST_USER_ID)).thenReturn(Optional.of(block));

        // when
        blockService.deleteBlock(TEST_USER_ID, TEST_BLOCK_ID);

        // then
        verify(blockRepository).delete(block);
    }

    @Test
    void deleteBlock_notFoundOrNotOwned_throwsNotFound() {
        when(blockRepository.findByIdAndBlockerUserId(TEST_BLOCK_ID, TEST_USER_ID)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> blockService.deleteBlock(TEST_USER_ID, TEST_BLOCK_ID));
        verify(blockRepository, never()).delete(any(Block.class));
    }
}

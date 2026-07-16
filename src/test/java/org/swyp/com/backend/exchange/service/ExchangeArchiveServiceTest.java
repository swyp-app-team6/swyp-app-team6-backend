package org.swyp.com.backend.exchange.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.CosmicTestFixture.createCosmic;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_AGE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_JOB;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_REGION_DETAIL;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfile;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;
import static org.swyp.com.backend.support.UserTestFixture.createUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.block.domain.Block;
import org.swyp.com.backend.block.domain.repository.BlockRepository;
import org.swyp.com.backend.exchange.domain.Exchange;
import org.swyp.com.backend.exchange.domain.MatchedInterest;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.domain.repository.MatchedInterestRepository;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.exchange.dto.ExchangeCardListResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDeleteResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDetailResponse;
import org.swyp.com.backend.exchange.dto.ExchangeLikeResponse;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.image.service.ImageUrlService;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.service.ProfileService;
import org.swyp.com.backend.report.service.ReportService;
import org.swyp.com.backend.support.ExchangeTestFixture;
import org.swyp.com.backend.support.ProfileTestFixture;
import org.swyp.com.backend.user.domain.User;

@ExtendWith(MockitoExtension.class)
class ExchangeArchiveServiceTest {

    @Mock
    ProfileExchangeRepository profileExchangeRepository;
    @Mock
    ProfileInterestRepository profileInterestRepository;
    @Mock
    MatchedInterestRepository matchedInterestRepository;
    @Mock
    ProfileService profileService;
    @Mock
    ReportService reportService;
    @Mock
    BlockRepository blockRepository;

    ExchangeArchiveService exchangeArchiveService;

    @BeforeEach
    void setUp() {
        ImageUrlService imageUrlService = new ImageUrlService() {
            @Override
            public String toSignedUrl(String imageKey) {
                return imageKey;
            }

            @Override
            public String toSignedThumbnailUrl(String imageKey) {
                return imageKey;
            }
        };
        exchangeArchiveService = new ExchangeArchiveService(profileExchangeRepository, profileInterestRepository,
                matchedInterestRepository, profileService, reportService, blockRepository, imageUrlService);
    }

    private ProfileExchange buildRow(Long id, LocalDateTime createdAt) {
        User counterpartUser = createUser(2L, "counterpart@example.com", TEST_ROLE);
        Profile counterpartProfile = createProfile(id, counterpartUser, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY,
                TEST_GENDER, TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, "안녕하세요", null);
        Exchange exchange = ExchangeTestFixture.createExchange(id, createdAt);
        return ExchangeTestFixture.createProfileExchange(id, createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE),
                counterpartProfile, exchange, null, null);
    }

    @Test
    void getArchiveList_필터없이_조회_성공() {
        // given
        ProfileExchange row = buildRow(1L, LocalDateTime.now());
        when(profileExchangeRepository.searchArchive(eq(TEST_USER_ID), any(), any(), any(), any(), any(), any(),
                eq(21)))
                .thenReturn(List.of(row));
        when(profileExchangeRepository.countArchive(eq(TEST_USER_ID), any(), any(), any(), any())).thenReturn(1L);
        when(profileInterestRepository.findByProfileIn(anyList())).thenReturn(List.of());
        when(matchedInterestRepository.findByExchangeIn(anyList())).thenReturn(List.of());

        // when
        ExchangeCardListResponse response = exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                ExchangeSortDirection.RECENT, null, 20);

        // then
        assertThat(response.exchanges()).hasSize(1);
        assertThat(response.exchanges().get(0).exchangeId()).isEqualTo(1L);
        assertThat(response.exchanges().get(0).imageUrl()).isEqualTo(TEST_IMAGE_KEY);
        assertThat(response.exchanges().get(0).isLiked()).isFalse();
        assertThat(response.exchanges().get(0).isBlocked()).isFalse();
        assertThat(response.exchanges().get(0).blockId()).isNull();
        assertThat(response.totalCount()).isEqualTo(1L);
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void getArchiveList_차단한상대는_is_blocked_true와_block_id_포함되어_노출() {
        // given: 차단해도 보관함 목록에서 사라지지 않고 차단 정보만 함께 내려옴
        ProfileExchange row = buildRow(1L, LocalDateTime.now());
        Long counterpartUserId = row.getProfile().getUser().getId();
        User blocker = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Block block = Block.createBlock(blocker, row.getProfile().getUser());
        ReflectionTestUtils.setField(block, "id", 10L);

        when(profileExchangeRepository.searchArchive(eq(TEST_USER_ID), any(), any(), any(), any(), any(), any(),
                eq(21)))
                .thenReturn(List.of(row));
        when(profileExchangeRepository.countArchive(eq(TEST_USER_ID), any(), any(), any(), any())).thenReturn(1L);
        when(profileInterestRepository.findByProfileIn(anyList())).thenReturn(List.of());
        when(matchedInterestRepository.findByExchangeIn(anyList())).thenReturn(List.of());
        when(blockRepository.findAllByBlockerUserIdAndBlockedUserIdIn(TEST_USER_ID, List.of(counterpartUserId)))
                .thenReturn(List.of(block));

        // when
        ExchangeCardListResponse response = exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                ExchangeSortDirection.RECENT, null, 20);

        // then
        assertThat(response.exchanges()).hasSize(1);
        assertThat(response.exchanges().get(0).isBlocked()).isTrue();
        assertThat(response.exchanges().get(0).blockId()).isEqualTo(10L);
    }

    @Test
    void getArchiveList_다음페이지존재시_nextCursor값존재() {
        // given: size=1 요청, repository가 size+1(=2)건을 돌려주는 상황을 흉내
        ProfileExchange row1 = buildRow(1L, LocalDateTime.now().minusMinutes(1));
        ProfileExchange row2 = buildRow(2L, LocalDateTime.now());
        when(profileExchangeRepository.searchArchive(eq(TEST_USER_ID), any(), any(), any(), any(), any(), any(), eq(2)))
                .thenReturn(List.of(row2, row1));
        when(profileExchangeRepository.countArchive(eq(TEST_USER_ID), any(), any(), any(), any())).thenReturn(2L);
        when(profileInterestRepository.findByProfileIn(anyList())).thenReturn(List.of());
        when(matchedInterestRepository.findByExchangeIn(anyList())).thenReturn(List.of());

        // when
        ExchangeCardListResponse response = exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                ExchangeSortDirection.RECENT, null, 1);

        // then
        assertThat(response.exchanges()).hasSize(1);
        assertThat(response.nextCursor()).isNotNull();
    }

    @Test
    void getArchiveList_마지막페이지_nextCursor_null() {
        // given
        ProfileExchange row = buildRow(1L, LocalDateTime.now());
        when(profileExchangeRepository.searchArchive(eq(TEST_USER_ID), any(), any(), any(), any(), any(), any(), eq(2)))
                .thenReturn(List.of(row));
        when(profileExchangeRepository.countArchive(eq(TEST_USER_ID), any(), any(), any(), any())).thenReturn(1L);
        when(profileInterestRepository.findByProfileIn(anyList())).thenReturn(List.of());
        when(matchedInterestRepository.findByExchangeIn(anyList())).thenReturn(List.of());

        // when
        ExchangeCardListResponse response = exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                ExchangeSortDirection.RECENT, null, 1);

        // then
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void getArchiveList_빈목록() {
        // given
        when(profileExchangeRepository.searchArchive(eq(TEST_USER_ID), any(), any(), any(), any(), any(), any(),
                eq(21)))
                .thenReturn(List.of());
        when(profileExchangeRepository.countArchive(eq(TEST_USER_ID), any(), any(), any(), any())).thenReturn(0L);

        // when
        ExchangeCardListResponse response = exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                ExchangeSortDirection.RECENT, null, 20);

        // then
        assertThat(response.exchanges()).isEmpty();
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void getArchiveList_잘못된커서_BusinessException() {
        assertThrows(BusinessException.class, () ->
                exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                        ExchangeSortDirection.RECENT, "invalid-cursor", 20));
    }

    @Test
    void getArchiveList_size범위밖_BusinessException() {
        assertThrows(BusinessException.class, () ->
                exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                        ExchangeSortDirection.RECENT, null, 0));
        assertThrows(BusinessException.class, () ->
                exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, null,
                        ExchangeSortDirection.RECENT, null, 51));
    }

    @Test
    void getArchiveList_liked필터_repository에그대로전달() {
        // given
        when(profileExchangeRepository.searchArchive(eq(TEST_USER_ID), any(), any(), any(), eq(true), any(), any(),
                eq(21))).thenReturn(List.of());
        when(profileExchangeRepository.countArchive(eq(TEST_USER_ID), any(), any(), any(), eq(true))).thenReturn(0L);

        // when
        exchangeArchiveService.getArchiveList(TEST_USER_ID, null, null, null, true, ExchangeSortDirection.RECENT,
                null, 20);

        // then
        verify(profileExchangeRepository).searchArchive(eq(TEST_USER_ID), any(), any(), any(), eq(true), any(),
                any(), eq(21));
        verify(profileExchangeRepository).countArchive(eq(TEST_USER_ID), any(), any(), any(), eq(true));
    }

    @Test
    void getArchiveDetail_성공() {
        // given
        ProfileExchange row = buildRow(1L, LocalDateTime.now());
        when(profileExchangeRepository.findByIdAndUserId(1L, TEST_USER_ID)).thenReturn(Optional.of(row));

        MatchedInterest matchedInterest = ExchangeTestFixture.createMatchedInterest(1L, row.getExchange(),
                InterestType.TRAVEL);
        when(matchedInterestRepository.findByExchange(row.getExchange())).thenReturn(List.of(matchedInterest));

        ProfileResponse fakeProfileResponse = new ProfileResponse(row.getProfile().getId(), TEST_PROFILE_NICKNAME,
                TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE, null, TEST_JOB, List.of(), "안녕하세요", null, null, null, List.of(),
                List.of());
        when(profileService.getProfileResponseById(row.getProfile().getId())).thenReturn(fakeProfileResponse);

        User myUser = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile myProfile = ProfileTestFixture.createProfile(99L, myUser, "나", TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION_DETAIL, TEST_JOB, "내소개", null);
        ReflectionTestUtils.setField(myProfile, "cosmic", createCosmic(1L, null, "detail", "image", false));

        // when
        ExchangeDetailResponse response = exchangeArchiveService.getArchiveDetail(TEST_USER_ID, 1L);

        // then
        assertThat(response.exchangeId()).isEqualTo(1L);
        assertThat(response.isMatched()).isTrue();
        assertThat(response.matchedInterests()).hasSize(1);
        assertThat(response.isLiked()).isFalse();
        assertThat(response.profile()).isEqualTo(fakeProfileResponse);
    }

    @Test
    void getArchiveDetail_존재하지않는id_BusinessException() {
        // given
        when(profileExchangeRepository.findByIdAndUserId(999L, TEST_USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> exchangeArchiveService.getArchiveDetail(TEST_USER_ID, 999L));
    }

    @Test
    void updateLiked_true로_설정_성공() {
        // given
        ProfileExchange row = buildRow(1L, LocalDateTime.now());
        when(profileExchangeRepository.findByIdAndUserId(1L, TEST_USER_ID)).thenReturn(Optional.of(row));

        // when
        ExchangeLikeResponse response = exchangeArchiveService.updateLiked(TEST_USER_ID, 1L, true);

        // then
        assertThat(response.exchangeId()).isEqualTo(1L);
        assertThat(response.isLiked()).isTrue();
    }

    @Test
    void updateLiked_false로_설정_성공() {
        // given
        ProfileExchange row = buildRow(1L, LocalDateTime.now());
        ReflectionTestUtils.setField(row, "liked", true);
        when(profileExchangeRepository.findByIdAndUserId(1L, TEST_USER_ID)).thenReturn(Optional.of(row));

        // when
        ExchangeLikeResponse response = exchangeArchiveService.updateLiked(TEST_USER_ID, 1L, false);

        // then
        assertThat(response.isLiked()).isFalse();
    }

    @Test
    void updateLiked_존재하지않는id_BusinessException() {
        // given
        when(profileExchangeRepository.findByIdAndUserId(999L, TEST_USER_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessException.class, () -> exchangeArchiveService.updateLiked(TEST_USER_ID, 999L, true));
    }

    @Test
    void deleteExchanges_전부본인소유_전체삭제() {
        // given
        ProfileExchange row1 = buildRow(1L, LocalDateTime.now());
        ProfileExchange row2 = buildRow(2L, LocalDateTime.now());
        when(profileExchangeRepository.findAllByIdInAndUserId(List.of(1L, 2L), TEST_USER_ID))
                .thenReturn(List.of(row1, row2));

        // when
        ExchangeDeleteResponse response = exchangeArchiveService.deleteArchives(TEST_USER_ID, List.of(1L, 2L));

        // then
        assertThat(response.deletedCount()).isEqualTo(2);
        assertThat(response.deletedIds()).containsExactlyInAnyOrder(1L, 2L);
        verify(profileExchangeRepository).deleteAll(List.of(row1, row2));
    }

    @Test
    void deleteExchanges_일부타인소유_본인소유만삭제() {
        // given: 3개 요청했지만 본인 소유는 1개뿐인 상황
        ProfileExchange row1 = buildRow(1L, LocalDateTime.now());
        when(profileExchangeRepository.findAllByIdInAndUserId(List.of(1L, 2L, 3L), TEST_USER_ID))
                .thenReturn(List.of(row1));

        // when
        ExchangeDeleteResponse response = exchangeArchiveService.deleteArchives(TEST_USER_ID, List.of(1L, 2L, 3L));

        // then
        assertThat(response.deletedCount()).isEqualTo(1);
        assertThat(response.deletedIds()).containsExactly(1L);
    }

    @Test
    void deleteExchanges_전부존재하지않음_deletedCount0_예외아님() {
        // given
        when(profileExchangeRepository.findAllByIdInAndUserId(List.of(999L), TEST_USER_ID)).thenReturn(List.of());

        // when
        ExchangeDeleteResponse response = exchangeArchiveService.deleteArchives(TEST_USER_ID, List.of(999L));

        // then
        assertThat(response.deletedCount()).isEqualTo(0);
        assertThat(response.deletedIds()).isEmpty();
    }
}

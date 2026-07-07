package org.swyp.com.backend.exchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.exchange.domain.Exchange;
import org.swyp.com.backend.exchange.domain.MatchedInterest;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.domain.repository.MatchedInterestRepository;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.exchange.dto.ExchangeCardListResponse;
import org.swyp.com.backend.exchange.dto.ExchangeCardResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDeleteResponse;
import org.swyp.com.backend.exchange.dto.ExchangeDetailResponse;
import org.swyp.com.backend.exchange.dto.ExchangeLikeResponse;
import org.swyp.com.backend.exchange.dto.ExchangeMyProfileSummary;
import org.swyp.com.backend.exchange.dto.ExchangeReviewRequest;
import org.swyp.com.backend.exchange.dto.ExchangeSortDirection;
import org.swyp.com.backend.exchange.dto.cursor.ExchangeCursor;
import org.swyp.com.backend.exchange.dto.cursor.ExchangeCursorCodec;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.RegionDetail;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.interest.dto.InterestTypeLabel;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileInterest;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.service.ProfileService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeArchiveService {

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 50;

    private final ProfileExchangeRepository profileExchangeRepository;
    private final ProfileInterestRepository profileInterestRepository;
    private final MatchedInterestRepository matchedInterestRepository;
    private final ProfileService profileService;

    public ExchangeCardListResponse getArchiveList(Long userId, String keyword, List<RegionDetail> regions,
                                                   List<CosmicDatingType> types, Boolean liked,
                                                   ExchangeSortDirection direction,
                                                   String cursor, int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "size는 " + MIN_SIZE + "~" + MAX_SIZE + " 사이여야 합니다.");
        }

        ExchangeCursor decodedCursor = cursor != null ? ExchangeCursorCodec.decode(cursor) : null;

        List<ProfileExchange> rows = profileExchangeRepository.searchArchive(userId, keyword, regions, types, liked,
                direction, decodedCursor, size + 1);

        boolean hasNext = rows.size() > size;
        List<ProfileExchange> pageRows = hasNext ? rows.subList(0, size) : rows;

        long totalCount = profileExchangeRepository.countArchive(userId, keyword, regions, types, liked);

        Map<Long, List<InterestTypeLabel>> interestsByProfileId = findInterestsByProfileId(pageRows);
        Map<Long, List<InterestTypeLabel>> matchedInterestsByExchangeId = findMatchedInterestsByExchangeId(pageRows);

        List<ExchangeCardResponse> cards = pageRows.stream()
                .map(pe -> toCardResponse(pe, interestsByProfileId, matchedInterestsByExchangeId))
                .toList();

        String nextCursor = null;
        if (hasNext) {
            ProfileExchange last = pageRows.get(pageRows.size() - 1);
            nextCursor = ExchangeCursorCodec.encode(
                    new ExchangeCursor(last.getExchange().getCreatedAt(), last.getId()));
        }

        return new ExchangeCardListResponse(cards, totalCount, nextCursor);
    }

    public ExchangeDetailResponse getArchiveDetail(Long userId, Long exchangeId) {
        ProfileExchange profileExchange = profileExchangeRepository.findByIdAndUserId(exchangeId, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "교환 정보를 찾을 수 없습니다."));

        List<MatchedInterest> matchedInterests = matchedInterestRepository.findByExchange(
                profileExchange.getExchange());
        boolean isMatched = !matchedInterests.isEmpty();
        List<InterestTypeLabel> matchedInterestLabels = matchedInterests.stream()
                .map(mi -> new InterestTypeLabel(mi.getType(), mi.getType().getLabel()))
                .toList();

        ProfileResponse profileResponse = profileService.getProfileResponseById(profileExchange.getProfile().getId());
        ExchangeMyProfileSummary myProfile = toMyProfileSummary(profileService.getProfileByUserId(userId));

        return new ExchangeDetailResponse(profileExchange.getId(), profileExchange.getExchange().getCreatedAt(),
                isMatched, matchedInterestLabels, profileExchange.getMemo(), profileExchange.getScore(),
                profileExchange.getLiked(), myProfile, profileResponse);
    }

    @Transactional
    public ExchangeLikeResponse updateLiked(Long userId, Long exchangeId, boolean liked) {
        ProfileExchange profileExchange = profileExchangeRepository.findByIdAndUserId(exchangeId, userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "교환 정보를 찾을 수 없습니다."));

        profileExchange.updateLiked(liked);

        return new ExchangeLikeResponse(profileExchange.getId(), profileExchange.getLiked());
    }

    @Transactional
    public ExchangeDeleteResponse deleteArchives(Long userId, List<Long> exchangeIds) {
        List<ProfileExchange> owned = profileExchangeRepository.findAllByIdInAndUserId(exchangeIds, userId);

        profileExchangeRepository.deleteAll(owned);

        List<Long> deletedIds = owned.stream().map(ProfileExchange::getId).toList();
        return new ExchangeDeleteResponse(deletedIds.size(), deletedIds);
    }

    private Map<Long, List<InterestTypeLabel>> findInterestsByProfileId(List<ProfileExchange> pageRows) {
        List<Profile> profiles = pageRows.stream().map(ProfileExchange::getProfile).toList();
        if (profiles.isEmpty()) {
            return Map.of();
        }

        List<ProfileInterest> profileInterests = profileInterestRepository.findByProfileIn(profiles);
        return profileInterests.stream()
                .collect(Collectors.groupingBy(
                        pi -> pi.getProfile().getId(),
                        Collectors.mapping(
                                pi -> new InterestTypeLabel(pi.getInterest().getType(),
                                        pi.getInterest().getType().getLabel()),
                                Collectors.toList())));
    }

    private Map<Long, List<InterestTypeLabel>> findMatchedInterestsByExchangeId(List<ProfileExchange> pageRows) {
        List<Exchange> exchanges = pageRows.stream().map(ProfileExchange::getExchange).toList();
        if (exchanges.isEmpty()) {
            return Map.of();
        }

        List<MatchedInterest> matchedInterests = matchedInterestRepository.findByExchangeIn(exchanges);
        return matchedInterests.stream()
                .collect(Collectors.groupingBy(
                        mi -> mi.getExchange().getId(),
                        Collectors.mapping(mi -> new InterestTypeLabel(mi.getType(), mi.getType().getLabel()),
                                Collectors.toList())));
    }

    private ExchangeCardResponse toCardResponse(ProfileExchange pe,
                                                Map<Long, List<InterestTypeLabel>> interestsByProfileId,
                                                Map<Long, List<InterestTypeLabel>> matchedInterestsByExchangeId) {
        Profile profile = pe.getProfile();
        Cosmic cosmic = profile.getCosmic();

        return new ExchangeCardResponse(
                pe.getId(),
                profile.getNickname(),
                cosmic != null ? cosmic.getType() : null,
                cosmic != null ? cosmic.getImageKey() : null,
                interestsByProfileId.getOrDefault(profile.getId(), new ArrayList<>()),
                profile.getBio(),
                matchedInterestsByExchangeId.getOrDefault(pe.getExchange().getId(), new ArrayList<>()),
                pe.getMemo(),
                pe.getScore(),
                pe.getLiked(),
                pe.getExchange().getCreatedAt());
    }

    private ExchangeMyProfileSummary toMyProfileSummary(Profile myProfile) {
        Cosmic cosmic = myProfile.getCosmic();
        return new ExchangeMyProfileSummary(myProfile.getNickname(), cosmic != null ? cosmic.getType() : null,
                cosmic != null ? cosmic.getImageKey() : null);
    }

    @Transactional
    public ExchangeDetailResponse updateReview(Long userId, Long exchangeId, ExchangeReviewRequest request) {
        ProfileExchange profileExchange = profileExchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "교환 정보를 찾을 수 없습니다."));

        profileExchange.updateReview(request.review(), request.score());

        return getArchiveDetail(userId, exchangeId);
    }
}

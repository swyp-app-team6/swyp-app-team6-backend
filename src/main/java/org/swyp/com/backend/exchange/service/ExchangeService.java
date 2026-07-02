package org.swyp.com.backend.exchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;
import org.swyp.com.backend.exchange.domain.Exchange;
import org.swyp.com.backend.exchange.domain.MatchedInterest;
import org.swyp.com.backend.exchange.domain.ProfileExchange;
import org.swyp.com.backend.exchange.domain.repository.ExchangeRepository;
import org.swyp.com.backend.exchange.domain.repository.MatchedInterestRepository;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.exchange.dto.ExchangeResponse;
import org.swyp.com.backend.exchange.dto.ExchangeResult;
import org.swyp.com.backend.global.enumeration.ExchangeStatus;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.interest.domain.Interest;
import org.swyp.com.backend.interest.dto.InterestTypeLabel;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.service.ProfileService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeService {

    private final ProfileService profileService;
    private final ExchangeRepository exchangeRepository;
    private final ProfileExchangeRepository profileExchangeRepository;
    private final MatchedInterestRepository matchedInterestRepository;

    private final Map<Long, DeferredResult<ResponseEntity<ProfileResponse>>> exchangeStartPending = new ConcurrentHashMap<>();
    private final Map<KeyPair, DeferredResult<ResponseEntity<ExchangeResponse>>> exchangeAcceptPending = new ConcurrentHashMap<>();

    public void waitForProfileResponse(Long userId,
                                       DeferredResult<ResponseEntity<ProfileResponse>> result) {
        Profile profile = profileService.getProfileByUserId(userId);
        exchangeStartPending.put(profile.getId(), result);
    }

    public void waitForExchangeResponse(Long userId, UUID targetUUID,
                                        DeferredResult<ResponseEntity<ExchangeResponse>> result) {

        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileByUUID(targetUUID);

        DeferredResult<ResponseEntity<ProfileResponse>> targetWaiting =
                exchangeStartPending.remove(targetProfile.getId());

        if (targetWaiting == null || targetWaiting.isSetOrExpired()) {
            throw new BusinessException(HttpStatus.CONFLICT, "상대방이 교환 대기 중이 아닙니다.");
        }

        ProfileResponse response = profileService.getProfileResponseByUserId(userId);

        KeyPair key = new KeyPair(myProfile.getId(), targetProfile.getId());

        boolean success = targetWaiting.setResult(
                ResponseEntity.ok(response)
        );

        if (success) {
            exchangeAcceptPending.put(key, result);
        }
    }

    @Transactional
    public ExchangeResponse getExchangeResponse(Long userId, Long targetProfileId) {
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileById(targetProfileId);

        KeyPair key = new KeyPair(targetProfile.getId(), myProfile.getId());

        DeferredResult<ResponseEntity<ExchangeResponse>> targetWaiting =
                exchangeAcceptPending.remove(key);

        if (targetWaiting == null || targetWaiting.isSetOrExpired()) {
            throw new BusinessException(HttpStatus.CONFLICT, "상대방이 교환 대기 중이 아닙니다.");
        }

        Exchange exchange = Exchange.createExchange();
        exchangeRepository.save(exchange);

        List<Interest> myProfileInterestList = profileService.getInterestListByProfile(myProfile);
        List<Interest> targetProfileInterestList = profileService.getInterestListByProfile(targetProfile);
        List<InterestType> matchedInterestTypeList = createMatchedInterestTypeList(myProfileInterestList,
                targetProfileInterestList);

        boolean isMatched = !matchedInterestTypeList.isEmpty();
        if (isMatched) {
            List<MatchedInterest> matchedInterestList = matchedInterestTypeList.stream().map(mi ->
                    MatchedInterest.createMatchedInterest(exchange, mi)
            ).toList();
            matchedInterestRepository.saveAll(matchedInterestList);
        }

        ProfileExchange myProfileExchange = ProfileExchange.createProfileExchange(myProfile.getUser(), targetProfile,
                exchange);
        ProfileExchange targetProfileExchange = ProfileExchange.createProfileExchange(targetProfile.getUser(),
                myProfile, exchange);
        profileExchangeRepository.save(myProfileExchange);
        profileExchangeRepository.save(targetProfileExchange);

        ExchangeResult myExchangeResult = new ExchangeResult(isMatched,
                matchedInterestTypeList.stream().map(mi -> new InterestTypeLabel(mi, mi.getLabel())).toList(),
                myProfileExchange.getMemo(), myProfileExchange.getScore(), exchange.getCreatedAt(),
                profileService.getProfileResponseById(targetProfile.getId()));
        ExchangeResult targetExchangeResult = new ExchangeResult(isMatched,
                matchedInterestTypeList.stream().map(mi -> new InterestTypeLabel(mi, mi.getLabel())).toList(),
                targetProfileExchange.getMemo(), targetProfileExchange.getScore(), exchange.getCreatedAt(),
                profileService.getProfileResponseByUserId(userId));

        ExchangeResponse myResponse = new ExchangeResponse(ExchangeStatus.ACCEPTED, myExchangeResult);
        ExchangeResponse targetResponse = new ExchangeResponse(ExchangeStatus.ACCEPTED, targetExchangeResult);

        boolean success = targetWaiting.setResult(
                ResponseEntity.ok(targetResponse)
        );

        if (!success) {
            throw new BusinessException(HttpStatus.CONFLICT, "상대방이 교환 대기 중이 아닙니다.");
        }

        return myResponse;
    }

    private List<InterestType> createMatchedInterestTypeList(List<Interest> a,
                                                             List<Interest> b) {
        List<InterestType> matchedInterest = new ArrayList<>();

        int i = 0;
        int j = 0;

        while (i < a.size() && j < b.size()) {
            Interest ai = a.get(i);
            Interest bi = b.get(j);

            int compare = ai.getId().compareTo(bi.getId());

            if (compare == 0) {
                matchedInterest.add(ai.getType());
                i++;
                j++;
            } else if (compare < 0) {
                i++;
            } else {
                j++;
            }
        }

        return matchedInterest;
    }

    public void rejectExchange(Long userId, Long targetProfileId) {
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileById(targetProfileId);

        KeyPair key = new KeyPair(targetProfile.getId(), myProfile.getId());

        DeferredResult<ResponseEntity<ExchangeResponse>> targetWaiting =
                exchangeAcceptPending.remove(key);

        if (targetWaiting == null || targetWaiting.isSetOrExpired()) {
            throw new BusinessException(HttpStatus.CONFLICT, "상대방이 교환 대기 중이 아닙니다.");
        }

        ExchangeResponse response = new ExchangeResponse(ExchangeStatus.DECLINED, null);

        targetWaiting.setResult(
                ResponseEntity.ok(response)
        );
    }

    public void removeProfileResponse(Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);

        DeferredResult<ResponseEntity<ProfileResponse>> removed =
                exchangeStartPending.remove(profile.getId());
    }

    public void removeExchangeResponse(Long userId, UUID targetUUID) {
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileByUUID(targetUUID);

        KeyPair key = new KeyPair(myProfile.getId(), targetProfile.getId());

        DeferredResult<ResponseEntity<ExchangeResponse>> removed =
                exchangeAcceptPending.remove(key);
    }

    public void cancelExchangeWait(Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);

        DeferredResult<ResponseEntity<ProfileResponse>> waiting =
                exchangeStartPending.remove(profile.getId());

        if (waiting == null || waiting.isSetOrExpired()) {
            throw new BusinessException(HttpStatus.CONFLICT, "교환 대기 중이 아닙니다.");
        }

        waiting.setResult(
                ResponseEntity.noContent().build()
        );
    }

    public void cancelExchangeStart(Long userId, Long targetProfileId) {
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileById(targetProfileId);

        KeyPair key = new KeyPair(myProfile.getId(), targetProfile.getId());

        DeferredResult<ResponseEntity<ExchangeResponse>> waiting =
                exchangeAcceptPending.remove(key);

        if (waiting == null || waiting.isSetOrExpired()) {
            throw new BusinessException(HttpStatus.CONFLICT, "교환 대기 중이 아닙니다.");
        }

        waiting.setResult(
                ResponseEntity.noContent().build()
        );
    }
}

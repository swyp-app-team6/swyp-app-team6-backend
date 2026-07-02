package org.swyp.com.backend.exchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.swyp.com.backend.profile.domain.Interest;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.service.ProfileService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExchangeService {

    private final ProfileService profileService;
    private final ExchangeRepository exchangeRepository;
    private final ProfileExchangeRepository profileExchangeRepository;
    private final MatchedInterestRepository matchedInterestRepository;

    private final Map<Long, DeferredResult<ResponseEntity<ProfileResponse>>> waiting1 = new ConcurrentHashMap<>();
    private final Map<KeyPair, DeferredResult<ResponseEntity<ExchangeResponse>>> waiting2 = new ConcurrentHashMap<>();

    // B가 exchange/wait 호출시
    public void waitForProfileResponse(Long userId,
                                       DeferredResult<ResponseEntity<ProfileResponse>> result) { // userId : B
        Profile profile = profileService.getProfileByUserId(userId);
        // B 프로필 정보 대기
        waiting1.put(profile.getId(), result);
    }

    // A가 B의 uuid와 함께 exchange/start 호출시
    public void waitForExchangeResponse(Long userId, UUID targetUUID, // userId : A, targetUUID : B
                                        DeferredResult<ResponseEntity<ExchangeResponse>> result) {

        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileByUUID(targetUUID);

        DeferredResult<ResponseEntity<ProfileResponse>> targetWaiting =
                waiting1.remove(targetProfile.getId());

        if (targetWaiting == null || targetWaiting.isSetOrExpired()) {
            throw new BusinessException(HttpStatus.CONFLICT, "상대방이 교환 대기 중이 아닙니다.");
        }

        ProfileResponse response = profileService.getProfileResponseByUserId(userId);

        // (A, B)
        KeyPair key = new KeyPair(myProfile.getId(), targetProfile.getId());
        log.info("(" + key.waitingProfileId() + " ," + key.expectedProfileId() + " )");

        // A 프로필을 target으로 전송
        boolean success = targetWaiting.setResult(
                ResponseEntity.ok(response)
        );

        if (success) {
            // A 교환 결과 정보 대기
            waiting2.put(key, result);
        }
    }

    // B가 exchange/accept 호출시
    @Transactional
    public ExchangeResponse getExchangeResponse(Long userId, Long targetProfileId) {// userId : B, targetProfileId :A
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileById(targetProfileId);

        // (A, B)
        KeyPair key = new KeyPair(targetProfile.getId(), myProfile.getId());

        log.info("(" + key.waitingProfileId() + " ," + key.expectedProfileId() + " )");

        DeferredResult<ResponseEntity<ExchangeResponse>> targetWaiting =
                waiting2.remove(key);

        if (targetWaiting == null || targetWaiting.isSetOrExpired()) {
            log.info("교환 실패");
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

        ExchangeResult myExchangeResult = new ExchangeResult(isMatched, matchedInterestTypeList,
                myProfileExchange.getMemo(), myProfileExchange.getScore(), exchange.getCreatedAt(),
                profileService.getProfileResponseById(targetProfile.getId()));
        ExchangeResult targetExchangeResult = new ExchangeResult(isMatched, matchedInterestTypeList,
                targetProfileExchange.getMemo(), targetProfileExchange.getScore(), exchange.getCreatedAt(),
                profileService.getProfileResponseByUserId(userId));

        ExchangeResponse myResponse = new ExchangeResponse(ExchangeStatus.ACCEPTED, myExchangeResult);
        ExchangeResponse targetResponse = new ExchangeResponse(ExchangeStatus.ACCEPTED, targetExchangeResult);

        boolean success = targetWaiting.setResult(
                ResponseEntity.ok(targetResponse)
        );

        if (!success) {
            log.info("교환 실패");
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

    // B가 거절 exchange/decline
    public void rejectExchange(Long userId, Long targetProfileId) {
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileById(targetProfileId);

        KeyPair key = new KeyPair(targetProfile.getId(), myProfile.getId());

        DeferredResult<ResponseEntity<ExchangeResponse>> targetWaiting =
                waiting2.remove(key);

        if (targetWaiting == null || targetWaiting.isSetOrExpired()) {
            log.info("교환 실패");
            throw new BusinessException(HttpStatus.CONFLICT, "상대방이 교환 대기 중이 아닙니다.");
        }

        log.info("교환 거절");
        ExchangeResponse response = new ExchangeResponse(ExchangeStatus.DECLINED, null);

        targetWaiting.setResult(
                ResponseEntity.ok(response)
        );
    }

    // onCompletion(timeout)
    public void removeProfileResponse(Long userId) {
        Profile profile = profileService.getProfileByUserId(userId);

        DeferredResult<ResponseEntity<ProfileResponse>> removed =
                waiting1.remove(profile.getId());

        if (removed != null) {
            log.info("waiting1 removed");
        }
    }

    // onCompletion(timeout)
    public void removeExchangeResponse(Long userId, UUID targetUUID) {
        Profile myProfile = profileService.getProfileByUserId(userId);
        Profile targetProfile = profileService.getProfileByUUID(targetUUID);

        KeyPair key = new KeyPair(myProfile.getId(), targetProfile.getId());

        DeferredResult<ResponseEntity<ExchangeResponse>> removed =
                waiting2.remove(key);

        if (removed != null) {
            log.info("waiting2 removed");
        }
    }

    // 교환 취소

    // 교환 취소
}

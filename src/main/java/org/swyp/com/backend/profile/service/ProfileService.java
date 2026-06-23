package org.swyp.com.backend.profile.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.Interest;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileInterest;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final InterestRepository interestRepository;
    private final ProfileInterestRepository profileInterestRepository;

    public MyProfileResponse getMyProfile(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        List<Interest> interestList = profileInterestRepository.findByProfile(profile).stream()
                .map(ProfileInterest::getInterest).toList();

        return toMyProfileResponseDto(profile, interestList);
    }

    public ProfileResponse getUserProfile(final Long uuid) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public MyProfileResponse createProfile(final Long userId, final ProfileRegisterRequest profileForm) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        if (profileRepository.findByUser(user).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 프로필을 생성하였습니다.");
        }

        List<Interest> interestList = interestRepository.findByTypeInAndDeletedFalse(profileForm.interests());

        Profile profile = Profile.createProfile(user, profileForm.nickname(), profileForm.imageKey(),
                profileForm.gender(),
                profileForm.bio(), profileForm.keyword(), profileForm.topic());
        List<ProfileInterest> profileInterestList = new ArrayList<>();

        for (Interest interest : interestList) {
            profileInterestList.add(ProfileInterest.createProfileInterest(profile, interest));
        }

        profileRepository.save(profile);
        profileInterestRepository.saveAll(profileInterestList);

        return toMyProfileResponseDto(profile, interestList);
    }

    @Transactional
    public MyProfileResponse updateProfile(final Long userId, final ProfileRegisterRequest profileForm) {
        throw new UnsupportedOperationException();
    }

    private MyProfileResponse toMyProfileResponseDto(Profile profile, List<Interest> interestList) {
        List<InterestType> interestTypeList = new ArrayList<>();

        for (Interest interest : interestList) {
            interestTypeList.add(interest.getType());
        }

        return new MyProfileResponse(profile.getId(), profile.getNickname(),
                profile.getImageKey(), profile.getGender(), profile.getBio(), profile.getKeyword(), profile.getTopic(),
                interestTypeList);
    }
}

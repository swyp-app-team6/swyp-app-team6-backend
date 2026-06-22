package org.swyp.com.backend.profile.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final InterestRepository interestRepository;
    private final ProfileInterestRepository profileInterestRepository;

    public MyProfileResponse getMyProfile(final Long userId, final Long profileId) {
        throw new UnsupportedOperationException();
    }

    public ProfileResponse getUserProfile(final Long uuid) {
        throw new UnsupportedOperationException();
    }

    public List<MyProfileResponse> getMyProfiles(final Long userId) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public MyProfileResponse createProfile(final Long userId, final ProfileRegisterRequest profileForm) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public MyProfileResponse updateProfile(final Long userId, final ProfileRegisterRequest profileForm) {
        throw new UnsupportedOperationException();
    }
}

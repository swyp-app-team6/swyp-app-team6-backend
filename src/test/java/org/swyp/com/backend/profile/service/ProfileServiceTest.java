package org.swyp.com.backend.profile.service;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.OAuthProvider;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    InterestRepository interestRepository;
    @Mock
    ProfileInterestRepository profileInterestRepository;
    ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(userRepository, profileRepository, interestRepository,
                profileInterestRepository);
    }

    @Test
    void createProfileSuccessTest() {
        // given
        User user = createUser();
        ProfileRegisterRequest request = createRegisterForm();
        // then
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            profileService.createProfile(user.getId(), request);
        });
    }

    private ProfileRegisterRequest createRegisterForm() {
        return new ProfileRegisterRequest("testNickname", Gender.M,
                "/testUrl", "test bio", "testKeyword", "testTopic", List.of(InterestType.TRAVEL));

    }

    private User createUser() {
        return new User(1L, "test@example.com", UserRole.USER,
                OAuthProvider.GOOGLE, "123", LocalDateTime.now(), null);
    }

}
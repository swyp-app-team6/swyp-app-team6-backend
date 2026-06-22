package org.swyp.com.backend.profile.service;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.swyp.com.backend.profile.domain.Interest;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
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
        List<Interest> interestList = createInterestList();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        MyProfileResponse response = profileService.createProfile(user.getId(), request);

        // then
        Assertions.assertEquals(response.nickname(), request.nickname());
    }

    private List<Interest> createInterestList() {
        return List.of(createInterest());
    }

    private ProfileRegisterRequest createRegisterForm() {
        return new ProfileRegisterRequest("testNickname", Gender.M,
                "/testUrl", "test bio", "testKeyword",
                "testTopic", List.of(InterestType.TRAVEL));

    }

    private User createUser() {
        return new User(1L, "test@example.com", UserRole.USER,
                OAuthProvider.GOOGLE, "123", LocalDateTime.now(), null);
    }

    private Interest createInterest() {
        return new Interest(1L, InterestType.TRAVEL, false);
    }

}
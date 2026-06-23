package org.swyp.com.backend.profile.service;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.UserRole;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.Interest;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileInterest;
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
        when(profileRepository.findByUser(user)).thenReturn(Optional.empty());
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        MyProfileResponse response = profileService.createProfile(user.getId(), request);

        // then
        Assertions.assertEquals(response.nickname(), request.nickname());
    }

    @Test
    void createProfileFailTest_ProfileAlreadyExist() {
        // given
        User user = createUser();
        ProfileRegisterRequest request = createRegisterForm();
        Profile profile = createProfile(user, 1L, request.nickname());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        // then
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            profileService.createProfile(1L, request);
        });
        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createProfileFailTest_NotValidUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ProfileRegisterRequest request = createRegisterForm();

        // then
        Assertions.assertThrows(BusinessException.class, () -> {
            profileService.createProfile(1L, request);
        });
    }

    @Test
    void getProfileSuccessTest() {
        // given
        User user = createUser();
        ProfileRegisterRequest request = createRegisterForm();
        Profile profile = createProfile(user, 1L, request.nickname());
        Interest interest = createInterest();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(profileInterestRepository.findByProfile(profile))
                .thenReturn(List.of(ProfileInterest.createProfileInterest(profile, interest)));

        // when
        MyProfileResponse response = profileService.getMyProfile(user.getId());

        // then
        Assertions.assertEquals(request.nickname(), response.nickname());
    }

    @Test
    void getProfileFailTest_ProfileNotExist() {
        // given
        User user = createUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.empty());

        // then
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            profileService.getMyProfile(user.getId());
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
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
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "email", "test@example.com");
        ReflectionTestUtils.setField(user, "role", UserRole.USER);
        return user;
    }

    private Interest createInterest() {
        Interest interest = new Interest();
        ReflectionTestUtils.setField(interest, "id", 1L);
        ReflectionTestUtils.setField(interest, "type", InterestType.TRAVEL);
        ReflectionTestUtils.setField(interest, "deleted", false);
        return interest;
    }

    private Profile createProfile(User user, Long id, String nickname) {
        Profile profile = new Profile();
        ReflectionTestUtils.setField(profile, "id", id);
        ReflectionTestUtils.setField(profile, "user", user);
        ReflectionTestUtils.setField(profile, "nickname", nickname);
        return profile;
    }

}
package org.swyp.com.backend.profile.service;

import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_BIO;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_KEYWORD;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_ID;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_TOPIC;
import static org.swyp.com.backend.support.ProfileTestFixture.createInterestList;
import static org.swyp.com.backend.support.ProfileTestFixture.createInterestTypeList;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfile;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfileForm;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfileInterestList;
import static org.swyp.com.backend.support.ProfileTestFixture.createUpdateProfileForm;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;
import static org.swyp.com.backend.support.UserTestFixture.createUser;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
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
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_GENDER, TEST_IMAGE_KEY, TEST_BIO,
                TEST_KEYWORD, TEST_TOPIC, interestTypeList);

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
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_GENDER, TEST_IMAGE_KEY, TEST_BIO,
                TEST_KEYWORD, TEST_TOPIC, interestTypeList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));

        // then
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            profileService.createProfile(TEST_USER_ID, request);
        });
        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    void createProfileFailTest_NotValidUser() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_GENDER, TEST_IMAGE_KEY, TEST_BIO,
                TEST_KEYWORD, TEST_TOPIC, interestTypeList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(BusinessException.class, () -> {
            profileService.createProfile(TEST_USER_ID, request);
        });
    }

    @Test
    void getProfileSuccessTest() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);
        List<ProfileInterest> profileInterestList = createProfileInterestList(profile, interestList);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_GENDER, TEST_IMAGE_KEY, TEST_BIO,
                TEST_KEYWORD, TEST_TOPIC, interestTypeList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(profileInterestRepository.findByProfile(profile))
                .thenReturn(profileInterestList);

        // when
        MyProfileResponse response = profileService.getMyProfile(user.getId());

        // then
        Assertions.assertEquals(request.nickname(), response.nickname());
    }

    @Test
    void getProfileFailTest_ProfileNotExist() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.empty());

        // then
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            profileService.getMyProfile(user.getId());
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateProfileNameSuccessTest() {
        // given
        final String updateName = TEST_PROFILE_NICKNAME + "1";

        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileUpdateRequest request = createUpdateProfileForm(updateName, TEST_IMAGE_KEY, TEST_BIO,
                TEST_KEYWORD, TEST_TOPIC, interestTypeList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        MyProfileResponse response = profileService.updateProfile(user.getId(), request);

        // then
        Assertions.assertNotEquals(response.nickname(), TEST_PROFILE_NICKNAME);
    }

    @Test
    void updateProfileInterestSuccessTest() {
        // given
        List<InterestType> exInterestTypeList = createInterestTypeList(InterestType.MUSIC);
        List<InterestType> updatedInterestTypeList = createInterestTypeList(InterestType.TRAVEL, InterestType.READING,
                InterestType.RESTAURANT);

        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME);

        List<InterestType> interestTypeList = updatedInterestTypeList;
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileUpdateRequest request = createUpdateProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_BIO,
                TEST_KEYWORD, TEST_TOPIC, interestTypeList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        MyProfileResponse response = profileService.updateProfile(user.getId(), request);

        // then
        Assertions.assertNotEquals(response.interests().size(), exInterestTypeList.size());
    }

}
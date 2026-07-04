package org.swyp.com.backend.user.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_AGE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_BIO;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_COSMIC_TYPE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_JOB;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_ID;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_REGION_DETAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_ROLE;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_EMAIL;
import static org.swyp.com.backend.support.UserTestFixture.TEST_USER_ID;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.swyp.com.backend.auth.jwt.domain.repository.RefreshTokenRepository;
import org.swyp.com.backend.auth.oauth.apple.service.AppleAuthService;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.support.ProfileTestFixture;
import org.swyp.com.backend.support.UserTestFixture;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    ProfileInterestRepository profileInterestRepository;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    AppleAuthService appleAuthService;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, profileRepository, profileInterestRepository,
                refreshTokenRepository, appleAuthService);
    }

    @Test
    void deleteUserSuccessTest() {
        // given
        User user = UserTestFixture.createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = ProfileTestFixture.createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY,
                TEST_GENDER, TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.of(profile));

        // when
        userService.deleteUser(user.getId());

        // then
        verify(appleAuthService).revoke(user.getId());
        verify(profileRepository).delete(profile);
        verify(profileInterestRepository).deleteByProfile(profile);
        verify(userRepository).delete(user);
    }
}
package org.swyp.com.backend.profile.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.global.enumeration.CustomQuestionType.BINARY;
import static org.swyp.com.backend.global.enumeration.CustomQuestionType.BLANK;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_AGE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_BIO;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_COSMIC;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_JOB;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_ID;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_REGION;
import static org.swyp.com.backend.support.ProfileTestFixture.createInterestList;
import static org.swyp.com.backend.support.ProfileTestFixture.createInterestTypeList;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfile;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfileForm;
import static org.swyp.com.backend.support.ProfileTestFixture.createProfileInterestList;
import static org.swyp.com.backend.support.ProfileTestFixture.createUpdateProfileForm;
import static org.swyp.com.backend.support.QuestionTestFixture.TEST_ANSWER;
import static org.swyp.com.backend.support.QuestionTestFixture.TEST_MULTIPLE_CHOICE_ANSWER1;
import static org.swyp.com.backend.support.QuestionTestFixture.TEST_MULTIPLE_CHOICE_ANSWER2;
import static org.swyp.com.backend.support.QuestionTestFixture.TEST_MULTIPLE_CHOICE_QUESTION;
import static org.swyp.com.backend.support.QuestionTestFixture.TEST_SHORT_ANSWER_QUESTION;
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
import org.swyp.com.backend.profile.domain.ProfileChoiceTemplate;
import org.swyp.com.backend.profile.domain.ProfileInterest;
import org.swyp.com.backend.profile.domain.ProfileShortTemplate;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileChoiceTemplateRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileShortTemplateRepository;
import org.swyp.com.backend.profile.dto.ChoiceTemplate;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.ShortTemplate;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;
import org.swyp.com.backend.question.domain.repository.MultipleChoiceAnswerRepository;
import org.swyp.com.backend.question.domain.repository.MultipleChoiceQuestionRepository;
import org.swyp.com.backend.question.domain.repository.ShortAnswerQuestionRepository;
import org.swyp.com.backend.question.service.QuestionService;
import org.swyp.com.backend.support.QuestionTestFixture;
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
    @Mock
    ProfileChoiceTemplateRepository profileChoiceTemplateRepository;
    @Mock
    ProfileShortTemplateRepository profileShortTemplateRepository;
    @Mock
    MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    @Mock
    MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    @Mock
    ShortAnswerQuestionRepository shortAnswerQuestionRepository;

    ProfileService profileService;
    QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionService = new QuestionService(multipleChoiceQuestionRepository, multipleChoiceAnswerRepository,
                shortAnswerQuestionRepository);
        profileService = new ProfileService(questionService, userRepository, profileRepository, interestRepository,
                profileInterestRepository, profileChoiceTemplateRepository, profileShortTemplateRepository);
    }

    @Test
    void createProfileSuccessTest_Required() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION, TEST_JOB, interestTypeList, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.empty());
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        MyProfileResponse response = profileService.createProfile(user.getId(), request);

        // then
        Assertions.assertEquals(response.nickname(), request.nickname());
        verify(profileChoiceTemplateRepository, never()).saveAll(any(List.class));
        verify(profileShortTemplateRepository, never()).saveAll(any(List.class));
    }

    @Test
    void createProfileSuccessTest_Optional() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION, TEST_JOB, TEST_BIO, TEST_COSMIC);

        // entity-setup
        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);

        MultipleChoiceQuestion choiceQuestion = QuestionTestFixture.createMultipleChoiceQuestion(1L, BINARY,
                TEST_MULTIPLE_CHOICE_QUESTION, false);
        MultipleChoiceAnswer choiceAnswer1 = QuestionTestFixture.createMultipleChoiceAnswer(1L, choiceQuestion, 1,
                TEST_MULTIPLE_CHOICE_ANSWER1, false);
        MultipleChoiceAnswer choiceAnswer2 = QuestionTestFixture.createMultipleChoiceAnswer(2L, choiceQuestion, 2,
                TEST_MULTIPLE_CHOICE_ANSWER2, false);
        ShortAnswerQuestion shortQuestion = QuestionTestFixture.createShortAnswerQuestion(1L, BLANK,
                TEST_SHORT_ANSWER_QUESTION, false);

        // dto-setup
        ChoiceTemplate choiceTemplate = QuestionTestFixture.createChoiceTemplate(choiceQuestion.getId(),
                choiceQuestion.getType(),
                choiceQuestion.getContent(), choiceAnswer1.getAnswerId(), choiceAnswer1.getContent());
        ShortTemplate shortTemplate = QuestionTestFixture.createShortTemplate(shortQuestion.getId(),
                shortQuestion.getType(), shortQuestion.getContent(), TEST_ANSWER);

        List<ChoiceTemplate> choiceTemplateList = QuestionTestFixture.createchoiceTemplateList(choiceTemplate);
        List<ShortTemplate> shortTemplateList = QuestionTestFixture.createshortTemplateList(shortTemplate);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION, TEST_JOB, interestTypeList, TEST_BIO, TEST_COSMIC, choiceTemplateList, shortTemplateList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.empty());
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);
        when(multipleChoiceQuestionRepository.findByIdAndDeletedFalse(choiceQuestion.getId())).thenReturn(
                Optional.of(choiceQuestion));
        when(multipleChoiceAnswerRepository.findByQuestionAndAnswerIdAndDeletedFalse(choiceQuestion,
                choiceAnswer1.getAnswerId())).thenReturn(Optional.of(choiceAnswer1));
        when(shortAnswerQuestionRepository.findByIdAndDeletedFalse(shortQuestion.getId())).thenReturn(
                Optional.of(shortQuestion));

        // when
        MyProfileResponse response = profileService.createProfile(user.getId(), request);

        // then
        Assertions.assertEquals(response.nickname(), request.nickname());
        verify(profileChoiceTemplateRepository).saveAll(any(List.class));
        verify(profileShortTemplateRepository).saveAll(any(List.class));
    }

    @Test
    void createProfileFailTest_ProfileAlreadyExist() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION, TEST_JOB, TEST_BIO, TEST_COSMIC);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION, TEST_JOB, interestTypeList, null, null, null, null);

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

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION, TEST_JOB, interestTypeList, null, null, null, null);

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
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION, TEST_JOB, TEST_BIO, TEST_COSMIC);

        // entity-setup
        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);
        List<ProfileInterest> profileInterestList = createProfileInterestList(profile, interestList);

        MultipleChoiceQuestion choiceQuestion = QuestionTestFixture.createMultipleChoiceQuestion(1L, BINARY,
                TEST_MULTIPLE_CHOICE_QUESTION, false);
        MultipleChoiceAnswer choiceAnswer1 = QuestionTestFixture.createMultipleChoiceAnswer(1L, choiceQuestion, 1,
                TEST_MULTIPLE_CHOICE_ANSWER1, false);
        MultipleChoiceAnswer choiceAnswer2 = QuestionTestFixture.createMultipleChoiceAnswer(2L, choiceQuestion, 2,
                TEST_MULTIPLE_CHOICE_ANSWER2, false);
        ShortAnswerQuestion shortQuestion = QuestionTestFixture.createShortAnswerQuestion(1L, BLANK,
                TEST_SHORT_ANSWER_QUESTION, false);

        List<ProfileChoiceTemplate> profileChoiceTemplateList = QuestionTestFixture.createProfileChoiceTemplateList(
                profile, List.of(choiceAnswer1));
        List<ProfileShortTemplate> profileShortTemplateList = QuestionTestFixture.createProfileShortTemplateList(
                profile, List.of(shortQuestion), List.of(
                        TEST_ANSWER));

        // dto-setup
        ChoiceTemplate choiceTemplate = QuestionTestFixture.createChoiceTemplate(choiceQuestion.getId(),
                choiceQuestion.getType(),
                choiceQuestion.getContent(), choiceAnswer1.getAnswerId(), choiceAnswer1.getContent());
        ShortTemplate shortTemplate = QuestionTestFixture.createShortTemplate(shortQuestion.getId(),
                shortQuestion.getType(), shortQuestion.getContent(), TEST_ANSWER);

        List<ChoiceTemplate> choiceTemplateList = QuestionTestFixture.createchoiceTemplateList(choiceTemplate);
        List<ShortTemplate> shortTemplateList = QuestionTestFixture.createshortTemplateList(shortTemplate);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION, TEST_JOB, interestTypeList, TEST_BIO, TEST_COSMIC, choiceTemplateList, shortTemplateList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(profileInterestRepository.findByProfile(profile))
                .thenReturn(profileInterestList);
        when(profileChoiceTemplateRepository.findByProfile(profile)).thenReturn(profileChoiceTemplateList);
        when(profileShortTemplateRepository.findByProfile(profile)).thenReturn(profileShortTemplateList);

        // when
        MyProfileResponse response = profileService.getMyProfile(user.getId());

        // then
        Assertions.assertEquals(request.nickname(), response.nickname());
        Assertions.assertEquals(request.choiceTemplate(), response.choiceTemplate());
        Assertions.assertEquals(request.shortTemplate(), response.shortTemplate());
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
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION, TEST_JOB, TEST_BIO, TEST_COSMIC);

        ProfileUpdateRequest request = createUpdateProfileForm(updateName, null, null,
                null, null, null, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));

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
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION, TEST_JOB, TEST_BIO, TEST_COSMIC);

        List<InterestType> interestTypeList = updatedInterestTypeList;
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileUpdateRequest request = createUpdateProfileForm(null, null, null,
                null, null, interestTypeList, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUser(user)).thenReturn(Optional.of(profile));
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        MyProfileResponse response = profileService.updateProfile(user.getId(), request);

        // then
        Assertions.assertNotEquals(response.interests().size(), exInterestTypeList.size());
    }

}
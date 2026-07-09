package org.swyp.com.backend.profile.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.swyp.com.backend.global.enumeration.CustomQuestionType.BINARY;
import static org.swyp.com.backend.global.enumeration.CustomQuestionType.BLANK;
import static org.swyp.com.backend.support.CosmicTestFixture.TEST_COSMIC_DELETED;
import static org.swyp.com.backend.support.CosmicTestFixture.TEST_COSMIC_DETAIL;
import static org.swyp.com.backend.support.CosmicTestFixture.TEST_COSMIC_ID;
import static org.swyp.com.backend.support.CosmicTestFixture.TEST_COSMIC_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_AGE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_BIO;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_COSMIC_TYPE;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_GENDER;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_IMAGE_KEY;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_JOB;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_ID;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_PROFILE_NICKNAME;
import static org.swyp.com.backend.support.ProfileTestFixture.TEST_REGION_DETAIL;
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
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.cosmic.domain.repository.CosmicRepository;
import org.swyp.com.backend.cosmic.domain.repository.CosmicTypeTestRepository;
import org.swyp.com.backend.cosmic.service.CosmicService;
import org.swyp.com.backend.exchange.domain.repository.ProfileExchangeRepository;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.interest.domain.Interest;
import org.swyp.com.backend.interest.domain.repository.InterestRepository;
import org.swyp.com.backend.interest.service.InterestService;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoice;
import org.swyp.com.backend.profile.domain.ProfileInterest;
import org.swyp.com.backend.profile.domain.ProfileShort;
import org.swyp.com.backend.profile.domain.repository.ProfileChoiceRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileShortRepository;
import org.swyp.com.backend.profile.dto.ChoiceTemplate;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.ShortTemplate;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;
import org.swyp.com.backend.question.domain.repository.MultipleChoiceAnswerRepository;
import org.swyp.com.backend.question.domain.repository.MultipleChoiceQuestionRepository;
import org.swyp.com.backend.question.domain.repository.ShortAnswerQuestionRepository;
import org.swyp.com.backend.question.service.QuestionService;
import org.swyp.com.backend.support.CosmicTestFixture;
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
    ProfileChoiceRepository profileChoiceRepository;
    @Mock
    ProfileShortRepository profileShortRepository;
    @Mock
    MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    @Mock
    MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    @Mock
    ShortAnswerQuestionRepository shortAnswerQuestionRepository;
    @Mock
    CosmicRepository cosmicRepository;
    @Mock
    CosmicTypeTestRepository cosmicTypeTestRepository;
    @Mock
    ProfileExchangeRepository profileExchangeRepository;

    ProfileService profileService;
    QuestionService questionService;
    CosmicService cosmicService;
    InterestService interestService;

    @BeforeEach
    void setUp() {
        cosmicService = new CosmicService(cosmicRepository, cosmicTypeTestRepository);
        questionService = new QuestionService(multipleChoiceQuestionRepository, multipleChoiceAnswerRepository,
                shortAnswerQuestionRepository);
        interestService = new InterestService(interestRepository);
        profileService = new ProfileService(questionService, cosmicService, interestService, userRepository,
                profileRepository,
                profileInterestRepository, profileChoiceRepository, profileShortRepository, profileExchangeRepository);
    }

    @Test
    void createProfileSuccessTest_Required() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION_DETAIL, TEST_JOB, interestTypeList, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.empty());
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        ProfileResponse response = profileService.createProfile(user.getId(), request);

        // then
        Assertions.assertEquals(response.nickname(), request.nickname());
        verify(profileChoiceRepository, never()).saveAll(any(List.class));
        verify(profileShortRepository, never()).saveAll(any(List.class));
    }

    @Test
    void createProfileSuccessTest_Optional() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);
        Cosmic cosmic = CosmicTestFixture.createCosmic(TEST_COSMIC_ID, TEST_COSMIC_TYPE, TEST_COSMIC_DETAIL,
                TEST_COSMIC_IMAGE_KEY, TEST_COSMIC_DELETED);

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
                TEST_REGION_DETAIL, TEST_JOB, interestTypeList, TEST_BIO, TEST_COSMIC_TYPE, choiceTemplateList,
                shortTemplateList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.empty());
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);
        when(multipleChoiceQuestionRepository.findByIdAndDeletedFalse(choiceQuestion.getId())).thenReturn(
                Optional.of(choiceQuestion));
        when(multipleChoiceAnswerRepository.findByQuestionAndAnswerIdAndDeletedFalse(choiceQuestion,
                choiceAnswer1.getAnswerId())).thenReturn(Optional.of(choiceAnswer1));
        when(shortAnswerQuestionRepository.findByIdAndDeletedFalse(shortQuestion.getId())).thenReturn(
                Optional.of(shortQuestion));
        when(cosmicRepository.findByType(TEST_COSMIC_TYPE)).thenReturn(Optional.of(cosmic));

        // when
        ProfileResponse response = profileService.createProfile(user.getId(), request);

        // then
        Assertions.assertEquals(response.nickname(), request.nickname());
        verify(profileChoiceRepository).saveAll(any(List.class));
        verify(profileShortRepository).saveAll(any(List.class));
    }

    @Test
    void createProfileFailTest_ProfileAlreadyExist() {
        // given
        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);

        List<InterestType> interestTypeList = createInterestTypeList(InterestType.TRAVEL);

        ProfileRegisterRequest request = createProfileForm(TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER, TEST_AGE,
                TEST_REGION_DETAIL, TEST_JOB, interestTypeList, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.of(profile));

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
                TEST_REGION_DETAIL, TEST_JOB, interestTypeList, null, null, null, null);

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
                TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);

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

        List<ProfileChoice> profileChoiceList = QuestionTestFixture.createProfileChoiceList(
                profile, List.of(choiceAnswer1));
        List<ProfileShort> profileShortList = QuestionTestFixture.createProfileShortList(
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
                TEST_REGION_DETAIL, TEST_JOB, interestTypeList, TEST_BIO, TEST_COSMIC_TYPE, choiceTemplateList,
                shortTemplateList);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.of(profile));
        when(profileInterestRepository.findByProfile(profile))
                .thenReturn(profileInterestList);
        when(profileChoiceRepository.findByProfile(profile)).thenReturn(profileChoiceList);
        when(profileShortRepository.findByProfile(profile)).thenReturn(profileShortList);

        // when
        ProfileResponse response = profileService.getProfileResponseByUserId(user.getId());

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
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.empty());

        // then
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            profileService.getProfileResponseByUserId(user.getId());
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateProfileNameSuccessTest() {
        // given
        final String updateName = TEST_PROFILE_NICKNAME + "1";

        User user = createUser(TEST_USER_ID, TEST_USER_EMAIL, TEST_ROLE);
        Profile profile = createProfile(TEST_PROFILE_ID, user, TEST_PROFILE_NICKNAME, TEST_IMAGE_KEY, TEST_GENDER,
                TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);

        ProfileUpdateRequest request = createUpdateProfileForm(updateName, null, null,
                null, null, null, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.of(profile));

        // when
        ProfileResponse response = profileService.updateProfile(user.getId(), request);

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
                TEST_AGE, TEST_REGION_DETAIL, TEST_JOB, TEST_BIO, TEST_COSMIC_TYPE);

        List<InterestType> interestTypeList = updatedInterestTypeList;
        List<Interest> interestList = createInterestList(interestTypeList);

        ProfileUpdateRequest request = createUpdateProfileForm(null, null, null,
                null, null, interestTypeList, null, null, null, null);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(profileRepository.findByUserAndDeletedFalse(user)).thenReturn(Optional.of(profile));
        when(interestRepository.findByTypeInAndDeletedFalse(request.interests())).thenReturn(interestList);

        // when
        ProfileResponse response = profileService.updateProfile(user.getId(), request);

        // then
        Assertions.assertNotEquals(response.interests().size(), exInterestTypeList.size());
    }

}
package org.swyp.com.backend.profile.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.cosmic.service.CosmicService;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.Interest;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoice;
import org.swyp.com.backend.profile.domain.ProfileInterest;
import org.swyp.com.backend.profile.domain.ProfileShort;
import org.swyp.com.backend.profile.domain.repository.InterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileChoiceRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileInterestRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileRepository;
import org.swyp.com.backend.profile.domain.repository.ProfileShortRepository;
import org.swyp.com.backend.profile.dto.ChoiceTemplate;
import org.swyp.com.backend.profile.dto.MyProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.ShortTemplate;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;
import org.swyp.com.backend.question.service.QuestionService;
import org.swyp.com.backend.user.domain.User;
import org.swyp.com.backend.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private final QuestionService questionService;
    private final CosmicService cosmicService;

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final InterestRepository interestRepository;
    private final ProfileInterestRepository profileInterestRepository;

    private final ProfileChoiceRepository profileChoiceRepository;
    private final ProfileShortRepository profileShortRepository;

    public MyProfileResponse getMyProfile(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        List<ProfileInterest> ProfileInterestList = profileInterestRepository.findByProfile(profile);
        List<ProfileChoice> profileChoiceList = profileChoiceRepository.findByProfile(profile);
        List<ProfileShort> profileShortList = profileShortRepository.findByProfile(profile);

        return toMyProfileResponseDto(profile, ProfileInterestList, profileChoiceList, profileShortList);
    }

    public ProfileResponse getUserProfile(final Long uuid) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public MyProfileResponse createProfile(final Long userId, final ProfileRegisterRequest profileForm) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        if (profileRepository.findByUser(user).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 프로필을 생성하였습니다.");
        }

        Cosmic cosmic =
                profileForm.cosmicType() != null ? cosmicService.getCosmicByCosmicType(profileForm.cosmicType()) : null;

        Profile profile = Profile.createProfile(user, profileForm.nickname(), profileForm.imageKey(),
                profileForm.gender(), profileForm.age(), profileForm.region(), profileForm.job(),
                profileForm.bio(), cosmic);

        List<ProfileInterest> profileInterestList = toProfileInterestList(profile, profileForm.interests());

        profileRepository.save(profile);
        profileInterestRepository.saveAll(profileInterestList);

        List<ProfileChoice> profileChoiceList = new ArrayList<>();
        List<ProfileShort> profileShortList = new ArrayList<>();

        if (profileForm.choiceTemplate() != null) {
            profileChoiceList = profileChoiceRepository.saveAll(
                    questionService.toProfileChoiceList(profile, profileForm.choiceTemplate()));
        }

        if (profileForm.shortTemplate() != null) {
            profileShortList = profileShortRepository.saveAll(
                    questionService.toProfileShortList(profile, profileForm.shortTemplate()));
        }

        return toMyProfileResponseDto(profile, profileInterestList, profileChoiceList,
                profileShortList);
    }

    @Transactional
    public MyProfileResponse updateProfile(final Long userId, final ProfileUpdateRequest profileForm) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        Cosmic cosmic =
                profileForm.cosmicType() != null ? cosmicService.getCosmicByCosmicType(profileForm.cosmicType()) : null;

        profile.updateProfile(profileForm.nickname(), profileForm.imageKey(), profileForm.age(), profileForm.region(),
                profileForm.job(), profileForm.bio(), cosmic);

        if (profileForm.interests() != null) {
            profileInterestRepository.deleteByProfile(profile);
            profileInterestRepository.saveAll(toProfileInterestList(profile, profileForm.interests()));
        }

        if (profileForm.choiceTemplate() != null) {
            profileChoiceRepository.deleteByProfile(profile);
            profileChoiceRepository.saveAll(
                    questionService.toProfileChoiceList(profile, profileForm.choiceTemplate()));
        }

        if (profileForm.shortTemplate() != null) {
            profileShortRepository.deleteByProfile(profile);
            profileShortRepository.saveAll(
                    questionService.toProfileShortList(profile, profileForm.shortTemplate()));
        }

        List<ProfileInterest> profileInterestList = profileInterestRepository.findByProfile(profile);
        List<ProfileChoice> profileChoiceList = profileChoiceRepository.findByProfile(profile);
        List<ProfileShort> profileShortList = profileShortRepository.findByProfile(profile);

        return toMyProfileResponseDto(profile, profileInterestList, profileChoiceList,
                profileShortList);
    }

    @Transactional
    public void deleteProfile(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUser(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        profileInterestRepository.deleteByProfile(profile);
        profileChoiceRepository.deleteByProfile(profile);
        profileShortRepository.deleteByProfile(profile);
        profileRepository.delete(profile);
    }

    private MyProfileResponse toMyProfileResponseDto(Profile profile, List<ProfileInterest> interestList,
                                                     List<ProfileChoice> profileChoiceList,
                                                     List<ProfileShort> profileShortList) {

        List<InterestType> interestTypeList = new ArrayList<>();
        List<ChoiceTemplate> choiceTemplateList = new ArrayList<>();
        List<ShortTemplate> shortTemplateList = new ArrayList<>();

        for (ProfileInterest interest : interestList) {
            interestTypeList.add(interest.getInterest().getType());
        }

        for (ProfileChoice profileChoice : profileChoiceList) {
            choiceTemplateList.add(
                    toChoiceTemplate(profileChoice.getAnswer().getQuestion(), profileChoice.getAnswer()));
        }

        for (ProfileShort profileShort : profileShortList) {
            shortTemplateList.add(toShortTemplate(profileShort.getQuestion(), profileShort.getAnswer()));
        }

        Cosmic cosmic = profile.getCosmic();
        CosmicDatingType type = cosmic != null ? cosmic.getType() : null;
        String imageKey = cosmic != null ? cosmic.getImageKey() : null;
        String detail = cosmic != null ? cosmic.getDetail() : null;

        return new MyProfileResponse(profile.getId(), profile.getNickname(),
                profile.getImageKey(), profile.getGender(), profile.getAge(), profile.getRegion(), profile.getJob(),
                interestTypeList, profile.getBio(), type, imageKey, detail, choiceTemplateList, shortTemplateList);
    }

    private ChoiceTemplate toChoiceTemplate(MultipleChoiceQuestion question, MultipleChoiceAnswer answer) {
        return new ChoiceTemplate(question.getId(), question.getType(),
                question.getContent(), answer.getAnswerId(), answer.getContent());
    }

    private ShortTemplate toShortTemplate(ShortAnswerQuestion question, String answer) {
        return new ShortTemplate(question.getId(), question.getType(), question.getContent(), answer);
    }

    private List<ProfileInterest> toProfileInterestList(Profile profile, List<InterestType> interestTypeList) {
        List<Interest> interestList = interestRepository.findByTypeInAndDeletedFalse(interestTypeList);

        List<ProfileInterest> profileInterestList = new ArrayList<>();
        for (Interest interest : interestList) {
            profileInterestList.add(ProfileInterest.createProfileInterest(profile, interest));
        }

        return profileInterestList;
    }
}

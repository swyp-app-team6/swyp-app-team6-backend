package org.swyp.com.backend.profile.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.cosmic.service.CosmicService;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.interest.domain.Interest;
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
import org.swyp.com.backend.profile.dto.InterestTypeLabel;
import org.swyp.com.backend.profile.dto.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.ProfileResponse;
import org.swyp.com.backend.profile.dto.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.QrResponse;
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
    private final InterestService interestService;

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final ProfileInterestRepository profileInterestRepository;

    private final ProfileChoiceRepository profileChoiceRepository;
    private final ProfileShortRepository profileShortRepository;

    private static final Long QR_TIMEOUT = Duration.ofMinutes(3).toMillis();

    public Profile getProfileById(Long profileId) {
        return profileRepository.findByIdAndDeletedFalse(profileId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));
    }

    public Profile getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        return profileRepository.findByUserAndDeletedFalse(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));
    }

    public Profile getProfileByUUID(UUID uuid) {
        Profile profile = profileRepository.findByQrAndDeletedFalse(uuid).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        Date now = new Date();

        if (profile.getQrExpiresAt().before(now)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "만료된 QR 코드 입니다.");
        }

        return profile;
    }

    public List<Interest> getInterestListByProfile(Profile profile) {
        return profileInterestRepository.findByProfileOrderByInterestId(profile).stream()
                .map(ProfileInterest::getInterest).toList();
    }

    public ProfileResponse getProfileResponseById(Long profileId) {
        Profile profile = profileRepository.findByIdAndDeletedFalse(profileId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        List<ProfileInterest> ProfileInterestList = profileInterestRepository.findByProfile(profile);
        List<ProfileChoice> profileChoiceList = profileChoiceRepository.findByProfile(profile);
        List<ProfileShort> profileShortList = profileShortRepository.findByProfile(profile);

        return toProfileResponseDto(profile, ProfileInterestList, profileChoiceList, profileShortList);
    }

    public ProfileResponse getProfileResponseByUserId(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUserAndDeletedFalse(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        List<ProfileInterest> ProfileInterestList = profileInterestRepository.findByProfile(profile);
        List<ProfileChoice> profileChoiceList = profileChoiceRepository.findByProfile(profile);
        List<ProfileShort> profileShortList = profileShortRepository.findByProfile(profile);

        return toProfileResponseDto(profile, ProfileInterestList, profileChoiceList, profileShortList);
    }

    public ProfileResponse getProfileResponseByUUID(UUID uuid) {
        Profile profile = profileRepository.findByQrAndDeletedFalse(uuid).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        Date now = new Date();

        if (profile.getQrExpiresAt().before(now)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "만료된 QR 코드 입니다.");
        }

        List<ProfileInterest> ProfileInterestList = profileInterestRepository.findByProfile(profile);
        List<ProfileChoice> profileChoiceList = profileChoiceRepository.findByProfile(profile);
        List<ProfileShort> profileShortList = profileShortRepository.findByProfile(profile);

        return toProfileResponseDto(profile, ProfileInterestList, profileChoiceList, profileShortList);
    }

    @Transactional
    public ProfileResponse createProfile(final Long userId, final ProfileRegisterRequest profileForm) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        if (profileRepository.findByUserAndDeletedFalse(user).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "이미 프로필을 생성하였습니다.");
        }

        Cosmic cosmic =
                profileForm.cosmicType() != null ? cosmicService.getCosmicByCosmicType(profileForm.cosmicType()) : null;

        Profile profile = Profile.createProfile(user, profileForm.nickname(), profileForm.imageKey(),
                profileForm.gender(), profileForm.age(), profileForm.region(), profileForm.job(),
                profileForm.bio(), cosmic);

        List<ProfileInterest> profileInterestList = interestService.toProfileInterestList(profile,
                profileForm.interests());

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

        return toProfileResponseDto(profile, profileInterestList, profileChoiceList,
                profileShortList);
    }

    @Transactional
    public ProfileResponse updateProfile(final Long userId, final ProfileUpdateRequest profileForm) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUserAndDeletedFalse(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        Cosmic cosmic =
                profileForm.cosmicType() != null ? cosmicService.getCosmicByCosmicType(profileForm.cosmicType()) : null;

        profile.updateProfile(profileForm.nickname(), profileForm.imageKey(), profileForm.age(), profileForm.region(),
                profileForm.job(), profileForm.bio(), cosmic);

        if (profileForm.interests() != null) {
            profileInterestRepository.deleteByProfile(profile);
            profileInterestRepository.saveAll(interestService.toProfileInterestList(profile, profileForm.interests()));
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

        return toProfileResponseDto(profile, profileInterestList, profileChoiceList,
                profileShortList);
    }

    @Transactional
    public void deleteProfile(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUserAndDeletedFalse(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        profile.deleteProfile();
    }

    private ProfileResponse toProfileResponseDto(Profile profile, List<ProfileInterest> interestList,
                                                 List<ProfileChoice> profileChoiceList,
                                                 List<ProfileShort> profileShortList) {

        List<InterestTypeLabel> interestTypeLabelList = new ArrayList<>();
        List<ChoiceTemplate> choiceTemplateList = new ArrayList<>();
        List<ShortTemplate> shortTemplateList = new ArrayList<>();

        for (ProfileInterest interest : interestList) {
            interestTypeLabelList.add(new InterestTypeLabel(interest.getInterest().getType(),
                    interest.getInterest().getType().getLabel()));
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

        return new ProfileResponse(profile.getId(), profile.getNickname(),
                profile.getImageKey(), profile.getGender(), profile.getAge(), profile.getRegion(), profile.getJob(),
                interestTypeLabelList, profile.getBio(), type, imageKey, detail, choiceTemplateList, shortTemplateList);
    }


    private ChoiceTemplate toChoiceTemplate(MultipleChoiceQuestion question, MultipleChoiceAnswer answer) {
        return new ChoiceTemplate(question.getId(), question.getType(),
                question.getContent(), answer.getAnswerId(), answer.getContent());
    }

    private ShortTemplate toShortTemplate(ShortAnswerQuestion question, String answer) {
        return new ShortTemplate(question.getId(), question.getType(), question.getContent(), answer);
    }

    @Transactional
    public QrResponse getQrUUID(final Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByUserAndDeletedFalse(user).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "프로필 정보를 찾을 수 없습니다."));

        Date now = new Date();

        if (profile.getQr() == null
                || (profile.getQrExpiresAt() != null && profile.getQrExpiresAt().before(now))) {

            profile.updateProfileQR(UUID.randomUUID(), new Date(now.getTime() + QR_TIMEOUT));
        }

        return new QrResponse(profile.getQr());
    }
}

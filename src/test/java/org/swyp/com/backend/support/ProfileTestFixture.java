package org.swyp.com.backend.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.enumeration.Gender;
import org.swyp.com.backend.global.enumeration.InterestType;
import org.swyp.com.backend.global.enumeration.RegionDetail;
import org.swyp.com.backend.interest.domain.Interest;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileInterest;
import org.swyp.com.backend.profile.dto.profile.ChoiceTemplate;
import org.swyp.com.backend.profile.dto.profile.ProfileRegisterRequest;
import org.swyp.com.backend.profile.dto.profile.ProfileUpdateRequest;
import org.swyp.com.backend.profile.dto.profile.ShortTemplate;
import org.swyp.com.backend.user.domain.User;

public class ProfileTestFixture {

    public static final Long TEST_PROFILE_ID = 1L;
    public static final String TEST_PROFILE_NICKNAME = "TestNickName";
    public static final String TEST_IMAGE_KEY = "test/image-key";
    public static final Gender TEST_GENDER = Gender.M;
    public static final Integer TEST_AGE = 25;
    public static final RegionDetail TEST_REGION_DETAIL = RegionDetail.SEOUL;
    public static final String TEST_JOB = "testJob";
    public static final String TEST_BIO = "testBio";
    public static final CosmicDatingType TEST_COSMIC_TYPE = CosmicDatingType.SOLA;

    public static ProfileRegisterRequest createProfileForm(String nickname, String imageKey, Gender gender, Integer age,
                                                           RegionDetail regionDetail, String job,
                                                           List<InterestType> interestTypeList, String bio,
                                                           CosmicDatingType cosmicType,
                                                           List<ChoiceTemplate> choiceTemplateList,
                                                           List<ShortTemplate> shortTemplateList
    ) {
        return new ProfileRegisterRequest(nickname, imageKey, gender, age, regionDetail, job, interestTypeList, bio,
                cosmicType,
                choiceTemplateList, shortTemplateList);

    }

    public static ProfileUpdateRequest createUpdateProfileForm(String nickname, String imageKey, Integer age,
                                                               RegionDetail regionDetail, String job,
                                                               List<InterestType> interestTypeList, String bio,
                                                               CosmicDatingType cosmicType,
                                                               List<ChoiceTemplate> choiceTemplateList,
                                                               List<ShortTemplate> shortTemplateList
    ) {
        return new ProfileUpdateRequest(nickname, imageKey, age, regionDetail, job, interestTypeList, bio,
                cosmicType,
                choiceTemplateList, shortTemplateList);
    }

    public static List<Interest> createInterestList(List<InterestType> interestTypeList) {
        List<Interest> interestList = new ArrayList<>();
        long id = 1L;
        for (InterestType type : interestTypeList) {
            interestList.add(createInterest(id++, type, false));
        }
        return interestList;
    }

    public static List<ProfileInterest> createProfileInterestList(Profile profile, List<Interest> interestList) {
        List<ProfileInterest> profileInterestList = new ArrayList<>();
        long id = 1L;
        for (Interest interest : interestList) {
            profileInterestList.add(createProfileInterest(id++, profile, interest));
        }
        return profileInterestList;
    }

    public static List<InterestType> createInterestTypeList(InterestType... interestTypes) {
        return Arrays.stream(interestTypes).toList();
    }

    public static Interest createInterest(Long id, InterestType type, boolean deleted) {
        Interest interest = new Interest();
        ReflectionTestUtils.setField(interest, "id", id);
        ReflectionTestUtils.setField(interest, "type", type);
        ReflectionTestUtils.setField(interest, "deleted", deleted);
        return interest;
    }

    public static Profile createProfile(Long id, User user, String nickname, String imageKey, Gender gender,
                                        Integer age, RegionDetail regionDetail, String job, String bio,
                                        CosmicDatingType cosmic) {
        Profile profile = new Profile();
        ReflectionTestUtils.setField(profile, "id", id);
        ReflectionTestUtils.setField(profile, "user", user);
        ReflectionTestUtils.setField(profile, "nickname", nickname);
        ReflectionTestUtils.setField(profile, "regionDetail", regionDetail);
        return profile;
    }

    private static ProfileInterest createProfileInterest(Long id, Profile profile, Interest interest) {
        ProfileInterest profileInterest = new ProfileInterest();
        ReflectionTestUtils.setField(profileInterest, "id", id);
        ReflectionTestUtils.setField(profileInterest, "profile", profile);
        ReflectionTestUtils.setField(profileInterest, "interest", interest);
        return profileInterest;
    }
}

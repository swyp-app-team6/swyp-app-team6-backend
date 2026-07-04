package org.swyp.com.backend.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoice;
import org.swyp.com.backend.profile.domain.ProfileShort;
import org.swyp.com.backend.profile.dto.ChoiceTemplate;
import org.swyp.com.backend.profile.dto.ShortTemplate;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;

public class QuestionTestFixture {
    public static final String TEST_MULTIPLE_CHOICE_QUESTION = "testMultipleChoiceQuestion";
    public static final String TEST_MULTIPLE_CHOICE_ANSWER1 = "testMultipleChoiceAnswer1";
    public static final String TEST_MULTIPLE_CHOICE_ANSWER2 = "testMultipleChoiceAnswer2";
    public static final String TEST_SHORT_ANSWER_QUESTION = "testShortAnswerQuestion";

    public static final Integer TEST_CHOICE = 1;
    public static final String TEST_ANSWER = "testAnswer";

    public static MultipleChoiceQuestion createMultipleChoiceQuestion(Long id, CustomQuestionType type, String content,
                                                                      Boolean deleted) {
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
        ReflectionTestUtils.setField(multipleChoiceQuestion, "id", id);
        ReflectionTestUtils.setField(multipleChoiceQuestion, "type", type);
        ReflectionTestUtils.setField(multipleChoiceQuestion, "content", content);
        ReflectionTestUtils.setField(multipleChoiceQuestion, "deleted", deleted);
        return multipleChoiceQuestion;
    }

    public static MultipleChoiceAnswer createMultipleChoiceAnswer(Long id, MultipleChoiceQuestion question,
                                                                  Integer answerId, String content,
                                                                  Boolean deleted) {
        MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer();
        ReflectionTestUtils.setField(multipleChoiceAnswer, "id", id);
        ReflectionTestUtils.setField(multipleChoiceAnswer, "question", question);
        ReflectionTestUtils.setField(multipleChoiceAnswer, "answerId", answerId);
        ReflectionTestUtils.setField(multipleChoiceAnswer, "content", content);
        ReflectionTestUtils.setField(multipleChoiceAnswer, "deleted", deleted);
        return multipleChoiceAnswer;
    }

    public static ShortAnswerQuestion createShortAnswerQuestion(Long id, CustomQuestionType type, String content,
                                                                Boolean deleted) {
        ShortAnswerQuestion shortAnswerQuestion = new ShortAnswerQuestion();
        ReflectionTestUtils.setField(shortAnswerQuestion, "id", id);
        ReflectionTestUtils.setField(shortAnswerQuestion, "type", type);
        ReflectionTestUtils.setField(shortAnswerQuestion, "content", content);
        ReflectionTestUtils.setField(shortAnswerQuestion, "deleted", deleted);
        return shortAnswerQuestion;
    }

    public static List<ChoiceTemplate> createchoiceTemplateList(ChoiceTemplate... choiceTemplate) {
        return Arrays.stream(choiceTemplate).toList();
    }

    public static List<ShortTemplate> createshortTemplateList(ShortTemplate... shortTemplate) {
        return Arrays.stream(shortTemplate).toList();
    }

    public static List<ProfileChoice> createProfileChoiceList(Profile profile,
                                                              List<MultipleChoiceAnswer> multipleChoiceAnswerList) {
        List<ProfileChoice> profileChoiceList = new ArrayList<>();
        long id = 1L;
        for (MultipleChoiceAnswer multipleChoiceAnswer : multipleChoiceAnswerList) {
            profileChoiceList.add(createProfileChoice(id++, profile, multipleChoiceAnswer));
        }
        return profileChoiceList;
    }

    public static List<ProfileShort> createProfileShortList(Profile profile,
                                                            List<ShortAnswerQuestion> shortAnswerQuestionList,
                                                            List<String> answer) {
        List<ProfileShort> profileShortList = new ArrayList<>();
        long id = 1L;
        for (int i = 0; i < shortAnswerQuestionList.size(); i++) {
            profileShortList.add(
                    createProfileShort(id++, profile, shortAnswerQuestionList.get(i), answer.get(i)));
        }
        return profileShortList;
    }

    private static ProfileChoice createProfileChoice(Long id, Profile profile,
                                                     MultipleChoiceAnswer answer) {
        ProfileChoice profileChoice = new ProfileChoice();
        ReflectionTestUtils.setField(profileChoice, "id", id);
        ReflectionTestUtils.setField(profileChoice, "profile", profile);
        ReflectionTestUtils.setField(profileChoice, "answer", answer);
        return profileChoice;
    }

    private static ProfileShort createProfileShort(Long id, Profile profile,
                                                   ShortAnswerQuestion question, String answer) {
        ProfileShort profileShort = new ProfileShort();
        ReflectionTestUtils.setField(profileShort, "id", id);
        ReflectionTestUtils.setField(profileShort, "profile", profile);
        ReflectionTestUtils.setField(profileShort, "question", question);
        ReflectionTestUtils.setField(profileShort, "answer", answer);
        return profileShort;
    }

    public static ChoiceTemplate createChoiceTemplate(Long questionId, CustomQuestionType type, String question,
                                                      Integer answerId, String answer) {
        return new ChoiceTemplate(questionId, type, question, answerId, answer);
    }

    public static ShortTemplate createShortTemplate(Long questionId, CustomQuestionType type, String question,
                                                    String answer) {
        return new ShortTemplate(questionId, type, question, answer);
    }
}

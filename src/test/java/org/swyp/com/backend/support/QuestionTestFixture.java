package org.swyp.com.backend.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;
import org.swyp.com.backend.global.enumeration.CustomQuestionType;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoiceTemplate;
import org.swyp.com.backend.profile.domain.ProfileShortTemplate;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;
import org.swyp.com.backend.question.dto.ChoiceTemplate;
import org.swyp.com.backend.question.dto.ShortTemplate;

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

    public static List<ProfileChoiceTemplate> createProfileChoiceTemplateList(Profile profile,
                                                                              List<MultipleChoiceAnswer> multipleChoiceAnswerList) {
        List<ProfileChoiceTemplate> profileChoiceTemplateList = new ArrayList<>();
        long id = 1L;
        for (MultipleChoiceAnswer multipleChoiceAnswer : multipleChoiceAnswerList) {
            profileChoiceTemplateList.add(createProfileChoiceTemplate(id++, profile, multipleChoiceAnswer));
        }
        return profileChoiceTemplateList;
    }

    public static List<ProfileShortTemplate> createProfileShortTemplateList(Profile profile,
                                                                            List<ShortAnswerQuestion> shortAnswerQuestionList,
                                                                            List<String> answer) {
        List<ProfileShortTemplate> profileShortTemplateList = new ArrayList<>();
        long id = 1L;
        for (int i = 0; i < shortAnswerQuestionList.size(); i++) {
            profileShortTemplateList.add(
                    createProfileShortTemplate(id++, profile, shortAnswerQuestionList.get(i), answer.get(i)));
        }
        return profileShortTemplateList;
    }

    private static ProfileChoiceTemplate createProfileChoiceTemplate(Long id, Profile profile,
                                                                     MultipleChoiceAnswer answer) {
        ProfileChoiceTemplate profileChoiceTemplate = new ProfileChoiceTemplate();
        ReflectionTestUtils.setField(profileChoiceTemplate, "id", id);
        ReflectionTestUtils.setField(profileChoiceTemplate, "profile", profile);
        ReflectionTestUtils.setField(profileChoiceTemplate, "answer", answer);
        return profileChoiceTemplate;
    }

    private static ProfileShortTemplate createProfileShortTemplate(Long id, Profile profile,
                                                                   ShortAnswerQuestion question, String answer) {
        ProfileShortTemplate profileShortTemplate = new ProfileShortTemplate();
        ReflectionTestUtils.setField(profileShortTemplate, "id", id);
        ReflectionTestUtils.setField(profileShortTemplate, "profile", profile);
        ReflectionTestUtils.setField(profileShortTemplate, "question", question);
        ReflectionTestUtils.setField(profileShortTemplate, "answer", answer);
        return profileShortTemplate;
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

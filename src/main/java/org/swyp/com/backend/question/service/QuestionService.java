package org.swyp.com.backend.question.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoice;
import org.swyp.com.backend.profile.domain.ProfileShort;
import org.swyp.com.backend.profile.dto.ChoiceTemplate;
import org.swyp.com.backend.profile.dto.ShortTemplate;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;
import org.swyp.com.backend.question.domain.repository.MultipleChoiceAnswerRepository;
import org.swyp.com.backend.question.domain.repository.MultipleChoiceQuestionRepository;
import org.swyp.com.backend.question.domain.repository.ShortAnswerQuestionRepository;
import org.swyp.com.backend.question.dto.CustomQuestionResponse;
import org.swyp.com.backend.question.dto.MultipleAnswer;
import org.swyp.com.backend.question.dto.MultipleQuestion;
import org.swyp.com.backend.question.dto.ShortQuestion;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    private final ShortAnswerQuestionRepository shortAnswerQuestionRepository;

    public CustomQuestionResponse getCustomQuestionResponse() {
        List<MultipleQuestion> multipleQuestionList =
                multipleChoiceQuestionRepository.findByDeletedFalseWithAnswers().stream()
                        .map(question -> new MultipleQuestion(
                                question.getId(),
                                question.getType(),
                                question.getContent(),
                                question.getAnswers().stream()
                                        .filter(a -> !a.getDeleted())
                                        .map(answer -> new MultipleAnswer(
                                                answer.getAnswerId(),
                                                answer.getContent()
                                        ))
                                        .toList()
                        ))
                        .toList();

        List<ShortQuestion> shortQuestionList = shortAnswerQuestionRepository.findByDeletedFalse().stream()
                .map(question ->
                        new ShortQuestion(question.getId(), question.getType(), question.getContent())).toList();

        return new CustomQuestionResponse(multipleQuestionList,
                shortQuestionList);
    }

    public List<ProfileChoice> toProfileChoiceList(Profile profile, List<ChoiceTemplate> choiceTemplateList) {
        List<ProfileChoice> profileChoiceList = new ArrayList<>();

        for (ChoiceTemplate choiceTemplate : choiceTemplateList) {
            MultipleChoiceQuestion question = multipleChoiceQuestionRepository
                    .findByIdAndDeletedFalse(choiceTemplate.questionId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "질문 템플릿 정보를 찾을 수 없습니다."));
            MultipleChoiceAnswer answer = multipleChoiceAnswerRepository
                    .findByQuestionAndAnswerIdAndDeletedFalse(question, choiceTemplate.answerId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "답변 템플릿 정보를 찾을 수 없습니다."));

            profileChoiceList.add(ProfileChoice.createProfileChoice(profile, answer));
        }

        return profileChoiceList;
    }

    public List<ProfileShort> toProfileShortList(Profile profile, List<ShortTemplate> shortTemplateList) {
        List<ProfileShort> profileShortList = new ArrayList<>();

        for (ShortTemplate shortTemplate : shortTemplateList) {
            ShortAnswerQuestion question = shortAnswerQuestionRepository
                    .findByIdAndDeletedFalse(shortTemplate.questionId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "질문 템플릿 정보를 찾을 수 없습니다."));
            String answer = shortTemplate.answer();

            profileShortList.add(ProfileShort.createProfileShortTemplate(profile, question, answer));
        }

        return profileShortList;
    }
}

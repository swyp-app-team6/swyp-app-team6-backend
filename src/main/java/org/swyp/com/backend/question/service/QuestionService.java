package org.swyp.com.backend.question.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.global.exception.BusinessException;
import org.swyp.com.backend.profile.domain.Profile;
import org.swyp.com.backend.profile.domain.ProfileChoiceTemplate;
import org.swyp.com.backend.profile.domain.ProfileShortTemplate;
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
        List<MultipleChoiceQuestion> questions =
                multipleChoiceQuestionRepository.findByDeletedFalse();

        Map<Long, List<MultipleChoiceAnswer>> answerMap =
                multipleChoiceAnswerRepository.findByDeletedFalse().stream()
                        .collect(Collectors.groupingBy(answer -> answer.getQuestion().getId()));

        List<MultipleQuestion> multipleQuestionList =
                questions.stream()
                        .map(question -> new MultipleQuestion(
                                question.getId(),
                                question.getType(),
                                question.getContent(),
                                answerMap.getOrDefault(question.getId(), List.of())
                                        .stream()
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

    public List<ProfileChoiceTemplate> toProfileChoiceList(Profile profile, List<ChoiceTemplate> choiceTemplateList) {
        List<ProfileChoiceTemplate> profileChoiceTemplateList = new ArrayList<>();

        for (ChoiceTemplate choiceTemplate : choiceTemplateList) {
            MultipleChoiceQuestion question = multipleChoiceQuestionRepository
                    .findByIdAndDeletedFalse(choiceTemplate.questionId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "질문 템플릿 정보를 찾을 수 없습니다."));
            MultipleChoiceAnswer answer = multipleChoiceAnswerRepository
                    .findByQuestionAndAnswerIdAndDeletedFalse(question, choiceTemplate.answerId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "답변 템플릿 정보를 찾을 수 없습니다."));

            profileChoiceTemplateList.add(ProfileChoiceTemplate.createProfileChoiceTemplate(profile, answer));
        }

        return profileChoiceTemplateList;
    }

    public List<ProfileShortTemplate> toProfileShortList(Profile profile, List<ShortTemplate> shortTemplateList) {
        List<ProfileShortTemplate> profileShortTemplateList = new ArrayList<>();

        for (ShortTemplate shortTemplate : shortTemplateList) {
            ShortAnswerQuestion question = shortAnswerQuestionRepository
                    .findByIdAndDeletedFalse(shortTemplate.questionId())
                    .orElseThrow(() ->
                            new BusinessException(HttpStatus.NOT_FOUND, "질문 템플릿 정보를 찾을 수 없습니다."));
            String answer = shortTemplate.answer();

            profileShortTemplateList.add(ProfileShortTemplate.createProfileShortTemplate(profile, question, answer));
        }

        return profileShortTemplateList;
    }
}

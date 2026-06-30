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
        List<Long> questionIds = choiceTemplateList.stream().map(ChoiceTemplate::questionId).toList();

        Map<Long, MultipleChoiceQuestion> questionMap = multipleChoiceQuestionRepository.findAllById(questionIds)
                .stream()
                .filter(q -> !q.getDeleted())
                .collect(Collectors.toMap(MultipleChoiceQuestion::getId, q -> q));

        // (questionId, answerId) → answer
        Map<Long, Map<Integer, MultipleChoiceAnswer>> answerMap =
                multipleChoiceAnswerRepository.findByQuestionIdInAndDeletedFalse(questionIds).stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getQuestion().getId(),
                                Collectors.toMap(MultipleChoiceAnswer::getAnswerId, a -> a)
                        ));

        return choiceTemplateList.stream()
                .map(choiceTemplate -> {
                    MultipleChoiceQuestion question = questionMap.get(choiceTemplate.questionId());
                    if (question == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "질문 템플릿 정보를 찾을 수 없습니다.");
                    }
                    MultipleChoiceAnswer answer = answerMap
                            .getOrDefault(choiceTemplate.questionId(), Map.of())
                            .get(choiceTemplate.answerId());
                    if (answer == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "답변 템플릿 정보를 찾을 수 없습니다.");
                    }
                    return ProfileChoice.createProfileChoice(profile, answer);
                })
                .toList();
    }

    public List<ProfileShort> toProfileShortList(Profile profile, List<ShortTemplate> shortTemplateList) {
        List<Long> questionIds = shortTemplateList.stream().map(ShortTemplate::questionId).toList();

        Map<Long, ShortAnswerQuestion> questionMap = shortAnswerQuestionRepository.findAllById(questionIds)
                .stream()
                .filter(q -> !q.getDeleted())
                .collect(Collectors.toMap(ShortAnswerQuestion::getId, q -> q));

        return shortTemplateList.stream()
                .map(shortTemplate -> {
                    ShortAnswerQuestion question = questionMap.get(shortTemplate.questionId());
                    if (question == null) {
                        throw new BusinessException(HttpStatus.NOT_FOUND, "질문 템플릿 정보를 찾을 수 없습니다.");
                    }
                    return ProfileShort.createProfileShortTemplate(profile, question, shortTemplate.answer());
                })
                .toList();
    }
}

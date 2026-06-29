package org.swyp.com.backend.cosmic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.CosmicTypeQuestion;
import org.swyp.com.backend.cosmic.domain.repository.CosmicTypeQuestionRepository;
import org.swyp.com.backend.cosmic.dto.CosmicTest;
import org.swyp.com.backend.cosmic.dto.CosmicTestAnswer;
import org.swyp.com.backend.cosmic.dto.CosmicTestResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CosmicService {
    private final CosmicTypeQuestionRepository cosmicTypeQuestionRepository;

    public CosmicTestResponse getCosmicTestResponse() {
        List<CosmicTypeQuestion> cosmicTypeQuestionList = cosmicTypeQuestionRepository.findByDeletedFalse();

        List<CosmicTest> cosmicTestList = cosmicTypeQuestionList.stream()
                .collect(Collectors.groupingBy(
                        CosmicTypeQuestion::getQuestionId
                ))
                .values().stream()
                .map(questionGroup -> {
                    CosmicTypeQuestion cosmicTypeQuestion = questionGroup.getFirst();

                    List<CosmicTestAnswer> cosmicTestAnswerList = questionGroup.stream()
                            .map(question ->
                                    new CosmicTestAnswer(question.getAnswerId(), question.getAnswer(),
                                            question.getCosmic(), question.getScore()))
                            .toList();

                    return new CosmicTest(cosmicTypeQuestion.getQuestionId(), cosmicTypeQuestion.getContent(),
                            cosmicTestAnswerList);
                })
                .toList();

        return new CosmicTestResponse(cosmicTestList);
    }
}

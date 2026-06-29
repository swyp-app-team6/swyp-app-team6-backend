package org.swyp.com.backend.cosmic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.CosmicTypeTest;
import org.swyp.com.backend.cosmic.domain.repository.CosmicTypeTestRepository;
import org.swyp.com.backend.cosmic.dto.CosmicTest;
import org.swyp.com.backend.cosmic.dto.CosmicTestAnswer;
import org.swyp.com.backend.cosmic.dto.CosmicTestResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CosmicService {
    private final CosmicTypeTestRepository cosmicTypeTestRepository;

    public CosmicTestResponse getCosmicTestResponse() {
        List<CosmicTypeTest> cosmicTypeTestList = cosmicTypeTestRepository.findByDeletedFalse();

        List<CosmicTest> cosmicTestList = cosmicTypeTestList.stream()
                .collect(Collectors.groupingBy(
                        CosmicTypeTest::getQuestionId
                ))
                .values().stream()
                .map(questionGroup -> {
                    CosmicTypeTest cosmicTypeTest = questionGroup.getFirst();

                    List<CosmicTestAnswer> cosmicTestAnswerList = questionGroup.stream()
                            .map(question ->
                                    new CosmicTestAnswer(question.getAnswerId(), question.getAnswer(),
                                            question.getCosmic(), question.getScore()))
                            .toList();

                    return new CosmicTest(cosmicTypeTest.getQuestionId(), cosmicTypeTest.getContent(),
                            cosmicTestAnswerList);
                })
                .toList();

        return new CosmicTestResponse(cosmicTestList);
    }
}

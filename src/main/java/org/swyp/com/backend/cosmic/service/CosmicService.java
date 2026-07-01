package org.swyp.com.backend.cosmic.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.Cosmic;
import org.swyp.com.backend.cosmic.domain.CosmicTypeTest;
import org.swyp.com.backend.cosmic.domain.repository.CosmicRepository;
import org.swyp.com.backend.cosmic.domain.repository.CosmicTypeTestRepository;
import org.swyp.com.backend.cosmic.dto.CosmicTest;
import org.swyp.com.backend.cosmic.dto.CosmicTestAnswer;
import org.swyp.com.backend.cosmic.dto.CosmicTestResponse;
import org.swyp.com.backend.global.enumeration.CosmicDatingType;
import org.swyp.com.backend.global.exception.BusinessException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CosmicService {
    private final CosmicRepository cosmicRepository;
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
                                            question.getType(), question.getScore()))
                            .toList();

                    return new CosmicTest(cosmicTypeTest.getQuestionId(), cosmicTypeTest.getContent(),
                            cosmicTestAnswerList);
                })
                .toList();

        return new CosmicTestResponse(cosmicTestList);
    }

    public Cosmic getCosmicByCosmicType(CosmicDatingType cosmicDatingType) {
        return cosmicRepository.findByType(cosmicDatingType).orElseThrow(() ->
                new BusinessException(HttpStatus.NOT_FOUND, "코스믹 유형 정보를 찾을 수 없습니다."));
    }
}

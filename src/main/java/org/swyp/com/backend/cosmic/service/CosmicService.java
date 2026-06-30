package org.swyp.com.backend.cosmic.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyp.com.backend.cosmic.domain.repository.CosmicQuestionRepository;
import org.swyp.com.backend.cosmic.dto.CosmicTest;
import org.swyp.com.backend.cosmic.dto.CosmicTestAnswer;
import org.swyp.com.backend.cosmic.dto.CosmicTestResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CosmicService {

    private final CosmicQuestionRepository cosmicQuestionRepository;

    public CosmicTestResponse getCosmicTestResponse() {
        List<CosmicTest> cosmicTestList = cosmicQuestionRepository.findByDeletedFalse().stream()
                .map(question -> new CosmicTest(
                        question.getId().intValue(),
                        question.getContent(),
                        question.getAnswers().stream()
                                .map(answer -> new CosmicTestAnswer(
                                        answer.getId().intValue(),
                                        answer.getAnswer(),
                                        answer.getCosmic(),
                                        answer.getScore()))
                                .toList()
                ))
                .toList();

        return new CosmicTestResponse(cosmicTestList);
    }
}

package org.swyp.com.backend.question.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.question.domain.MultipleChoiceAnswer;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;

public interface MultipleChoiceAnswerRepository extends JpaRepository<MultipleChoiceAnswer, Long> {

    List<MultipleChoiceAnswer> findByQuestionAndDeletedFalse(MultipleChoiceQuestion question);

    Optional<MultipleChoiceAnswer> findByQuestionAndAnswerIdAndDeletedFalse(MultipleChoiceQuestion question,
                                                                            Integer answerId);
}

package org.swyp.com.backend.question.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swyp.com.backend.question.domain.MultipleChoiceQuestion;

public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
    Optional<MultipleChoiceQuestion> findByIdAndDeletedFalse(Long id);

    List<MultipleChoiceQuestion> findByDeletedFalse();

    List<MultipleChoiceQuestion> findByIdInAndDeletedFalse(List<Long> ids);

    @Query("SELECT DISTINCT q FROM MultipleChoiceQuestion q LEFT JOIN FETCH q.answers a WHERE q.deleted = false AND a.deleted = false")
    List<MultipleChoiceQuestion> findByDeletedFalseWithAnswers();
}

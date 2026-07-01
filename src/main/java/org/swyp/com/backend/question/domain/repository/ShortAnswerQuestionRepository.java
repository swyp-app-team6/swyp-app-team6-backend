package org.swyp.com.backend.question.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.question.domain.ShortAnswerQuestion;

public interface ShortAnswerQuestionRepository extends JpaRepository<ShortAnswerQuestion, Long> {
    Optional<ShortAnswerQuestion> findByIdAndDeletedFalse(Long id);

    List<ShortAnswerQuestion> findByDeletedFalse();

    List<ShortAnswerQuestion> findByIdInAndDeletedFalse(List<Long> ids);
}

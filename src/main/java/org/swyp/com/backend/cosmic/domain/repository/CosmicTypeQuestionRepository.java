package org.swyp.com.backend.cosmic.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.cosmic.domain.CosmicTypeQuestion;

public interface CosmicTypeQuestionRepository extends JpaRepository<CosmicTypeQuestion, Long> {
    List<CosmicTypeQuestion> findByDeletedFalse();
}

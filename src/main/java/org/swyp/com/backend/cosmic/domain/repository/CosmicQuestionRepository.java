package org.swyp.com.backend.cosmic.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.cosmic.domain.CosmicQuestion;

public interface CosmicQuestionRepository extends JpaRepository<CosmicQuestion, Long> {

    List<CosmicQuestion> findByDeletedFalse();
}

package org.swyp.com.backend.question.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.question.domain.DatingTypeQuestion;

public interface DatingTypeQuestionRepository extends JpaRepository<DatingTypeQuestion, Long> {
}

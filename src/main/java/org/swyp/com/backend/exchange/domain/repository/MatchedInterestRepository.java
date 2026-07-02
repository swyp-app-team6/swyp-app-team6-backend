package org.swyp.com.backend.exchange.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.exchange.domain.MatchedInterest;

public interface MatchedInterestRepository extends JpaRepository<MatchedInterest, Long> {
}

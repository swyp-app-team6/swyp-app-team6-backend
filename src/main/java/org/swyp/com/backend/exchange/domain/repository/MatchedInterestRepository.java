package org.swyp.com.backend.exchange.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.exchange.domain.Exchange;
import org.swyp.com.backend.exchange.domain.MatchedInterest;

public interface MatchedInterestRepository extends JpaRepository<MatchedInterest, Long> {
    List<MatchedInterest> findByExchange(Exchange exchange);

    List<MatchedInterest> findByExchangeIn(List<Exchange> exchanges);
}

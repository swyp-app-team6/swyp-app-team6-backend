package org.swyp.com.backend.exchange.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.exchange.domain.Exchange;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
}

package org.swyp.com.backend.exchange.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.exchange.domain.ProfileExchange;

public interface ProfileExchangeRepository extends JpaRepository<ProfileExchange, Long> {
}

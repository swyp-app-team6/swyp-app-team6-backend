package org.swyp.com.backend.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyp.com.backend.user.domain.WithdrawalLog;

public interface WithdrawalLogJpaRepository extends JpaRepository<WithdrawalLog, Long> {

}

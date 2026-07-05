package org.swyp.com.backend.user.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.swyp.com.backend.user.domain.WithdrawalLog;

@Repository
@RequiredArgsConstructor
public class WithdrawalLogRepositoryImpl implements WithdrawalLogRepository {
    private final WithdrawalLogJpaRepository withdrawalLogJpaRepository;

    @Override
    public WithdrawalLog save(WithdrawalLog withdrawalLog) {
        return withdrawalLogJpaRepository.save(withdrawalLog);
    }
}

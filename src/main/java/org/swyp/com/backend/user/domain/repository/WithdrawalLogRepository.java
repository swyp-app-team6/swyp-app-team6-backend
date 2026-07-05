package org.swyp.com.backend.user.domain.repository;

import org.swyp.com.backend.user.domain.WithdrawalLog;

public interface WithdrawalLogRepository {

    WithdrawalLog save(WithdrawalLog withdrawalLog);
}
